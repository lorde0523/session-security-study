package com.example.sessionsecurity.common.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UseDbUserAspect {

    @Around("@annotation(useDbUser)")
    public Object route(ProceedingJoinPoint joinPoint, UseDbUser useDbUser) throws Throwable {
        try {
            DbUserContext.set(useDbUser.value());
            return joinPoint.proceed();
        } finally {
            DbUserContext.clear();
        }
    }
}
