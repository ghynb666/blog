package com.blog.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BypassCacheable {

    String key();

    long ttlSeconds() default 300;

    String ttlExpression() default "";

    boolean cacheNull() default false;
}
