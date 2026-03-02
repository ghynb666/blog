package com.blog.aspect;

import com.blog.annotation.RequireAuth;
import com.blog.util.JwtUtil;
import com.blog.common.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthAspect {

    @Autowired
    private JwtUtil jwtUtil;

    @Around("@annotation(requireAuth)")
    public Object checkAuth(ProceedingJoinPoint joinPoint, RequireAuth requireAuth) throws Throwable {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) {
            return Result.error(401, "未授权");
        }
        HttpServletRequest request = attrs.getRequest();
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            token = request.getParameter("token");
        }
        if (token != null && jwtUtil.validateToken(token)) {
            Long userId = jwtUtil.getUserId(token);
            request.setAttribute("userId", userId);
            return joinPoint.proceed();
        }
        return Result.error(401, "未授权");
    }
}
