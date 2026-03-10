package com.blog.cache;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Aspect
@Component
public class BypassCacheAspect {

    private static final Logger log = LoggerFactory.getLogger(BypassCacheAspect.class);

    private final CacheService cacheService;
    private final ObjectMapper objectMapper;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public BypassCacheAspect(CacheService cacheService, ObjectMapper objectMapper) {
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(com.blog.cache.BypassCacheable)")
    public Object aroundCacheable(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = getSpecificMethod(joinPoint);
        BypassCacheable cacheable = method.getAnnotation(BypassCacheable.class);
        String key = evaluateAsString(cacheable.key(), method, joinPoint.getArgs());
        long ttlSeconds = resolveTtlSeconds(cacheable, method, joinPoint.getArgs());

        try {
            var cached = cacheService.get(key);
            if (cached.isPresent()) {
                JavaType javaType = objectMapper.getTypeFactory().constructType(method.getGenericReturnType());
                return objectMapper.readValue(cached.get(), javaType);
            }
        } catch (Exception ex) {
            log.warn("Bypass cache read failed for key {}", key, ex);
        }

        Object result = joinPoint.proceed();
        if (result == null && !cacheable.cacheNull()) {
            return null;
        }

        try {
            cacheService.set(key, objectMapper.writeValueAsString(result), Duration.ofSeconds(Math.max(ttlSeconds, 1L)));
        } catch (Exception ex) {
            log.warn("Bypass cache write failed for key {}", key, ex);
        }
        return result;
    }

    @AfterReturning("@annotation(com.blog.cache.BypassCacheEvict)")
    public void afterEvict(org.aspectj.lang.JoinPoint joinPoint) {
        Method method = getSpecificMethod(joinPoint);
        BypassCacheEvict evict = method.getAnnotation(BypassCacheEvict.class);
        List<String> keys = resolveValues(evict.keys(), method, joinPoint.getArgs());
        List<String> patterns = resolveValues(evict.patterns(), method, joinPoint.getArgs());
        if (keys.isEmpty() && patterns.isEmpty()) {
            return;
        }

        Runnable task = () -> {
            keys.forEach(this::deleteKeyQuietly);
            patterns.forEach(this::deletePatternQuietly);
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    task.run();
                }
            });
            return;
        }
        task.run();
    }

    private void deleteKeyQuietly(String key) {
        try {
            cacheService.delete(key);
        } catch (Exception ex) {
            log.warn("Bypass cache delete failed for key {}", key, ex);
        }
    }

    private void deletePatternQuietly(String pattern) {
        try {
            cacheService.deletePattern(pattern);
        } catch (Exception ex) {
            log.warn("Bypass cache delete failed for pattern {}", pattern, ex);
        }
    }

    private long resolveTtlSeconds(BypassCacheable cacheable, Method method, Object[] args) {
        if (!StringUtils.hasText(cacheable.ttlExpression())) {
            return cacheable.ttlSeconds();
        }
        Object value = evaluate(cacheable.ttlExpression(), method, args);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return cacheable.ttlSeconds();
    }

    private List<String> resolveValues(String[] expressions, Method method, Object[] args) {
        List<String> values = new ArrayList<>();
        for (String expression : expressions) {
            Object value = evaluate(expression, method, args);
            if (value == null) {
                continue;
            }
            if (value instanceof String) {
                values.add((String) value);
                continue;
            }
            if (value instanceof Collection<?>) {
                ((Collection<?>) value).stream().filter(item -> item != null).map(String::valueOf).forEach(values::add);
                continue;
            }
            if (value.getClass().isArray()) {
                Object[] array = (Object[]) value;
                for (Object item : array) {
                    if (item != null) {
                        values.add(String.valueOf(item));
                    }
                }
                continue;
            }
            values.add(String.valueOf(value));
        }
        return values;
    }

    private String evaluateAsString(String expression, Method method, Object[] args) {
        Object value = evaluate(expression, method, args);
        return value == null ? "" : String.valueOf(value);
    }

    private Object evaluate(String expression, Method method, Object[] args) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("args", args);
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        for (int i = 0; i < args.length; i++) {
            context.setVariable("p" + i, args[i]);
            context.setVariable("a" + i, args[i]);
            if (parameterNames != null && i < parameterNames.length) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
        Expression spelExpression = expressionParser.parseExpression(expression);
        return spelExpression.getValue(context);
    }

    private Method getSpecificMethod(org.aspectj.lang.JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return AopUtils.getMostSpecificMethod(signature.getMethod(), joinPoint.getTarget().getClass());
    }
}
