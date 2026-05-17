package com.example.sessionsecurity.common.multiDb2;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UseMultiDb2Aspect {

    @Around("@annotation(useMultiDb2)")
    public Object routeMethod(ProceedingJoinPoint joinPoint, UseMultiDb2 useMultiDb2) throws Throwable {
        return route(joinPoint, useMultiDb2.value());
    }

    @Around("@within(com.example.sessionsecurity.common.multiDb2.UseMultiDb2)")
    public Object routeClass(ProceedingJoinPoint joinPoint) throws Throwable {
        UseMultiDb2 useMultiDb2 = AnnotationUtils.findAnnotation(
                joinPoint.getTarget().getClass(),
                UseMultiDb2.class
        );
        return route(joinPoint, useMultiDb2.value());
    }

    private Object route(ProceedingJoinPoint joinPoint, DataSourceKey key) throws Throwable {
        try {
            MultiDb2DataSourceContext.set(key);
            return joinPoint.proceed();
        } finally {
            MultiDb2DataSourceContext.clear();
        }
    }
}
