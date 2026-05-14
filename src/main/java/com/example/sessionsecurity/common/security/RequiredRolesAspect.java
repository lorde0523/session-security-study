package com.example.sessionsecurity.common.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequiredRolesAspect {

    private final RoleChecker roleChecker;

    public RequiredRolesAspect(RoleChecker roleChecker) {
        this.roleChecker = roleChecker;
    }

    @Around("@annotation(requiredRoles)")
    public Object checkMethodRoles(ProceedingJoinPoint joinPoint, RequiredRoles requiredRoles) throws Throwable {
        roleChecker.requireAny(requiredRoles.value());
        return joinPoint.proceed();
    }

    @Around("@within(com.example.sessionsecurity.common.security.RequiredRoles)")
    public Object checkTypeRoles(ProceedingJoinPoint joinPoint) throws Throwable {
        RequiredRoles requiredRoles = AnnotationUtils.findAnnotation(
                joinPoint.getTarget().getClass(),
                RequiredRoles.class
        );
        if (requiredRoles != null) {
            roleChecker.requireAny(requiredRoles.value());
        }
        return joinPoint.proceed();
    }
}
