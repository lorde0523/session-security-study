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
        String previous = DbUserContext.get();
        try {
            DbUserContext.set(useDbUser.value());
            return joinPoint.proceed();
        } finally {
            if (previous == null) {
                DbUserContext.clear();
            } else {
                DbUserContext.set(previous);
            }
        }
    }
}
