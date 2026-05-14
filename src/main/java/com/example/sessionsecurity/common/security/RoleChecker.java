package com.example.sessionsecurity.common.security;

import com.example.sessionsecurity.common.exception.BusinessException;
import com.example.sessionsecurity.common.exception.ErrorCode;
import com.example.sessionsecurity.security.SessionVo;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class RoleChecker {

    private final CurrentSessionProvider currentSessionProvider;

    public RoleChecker(CurrentSessionProvider currentSessionProvider) {
        this.currentSessionProvider = currentSessionProvider;
    }

    public SessionVo requireAny(String... requiredRoles) {
        SessionVo sessionVo = currentSessionProvider.current();
        Set<String> currentRoles = sessionVo.roles().stream()
                .map(this::normalize)
                .collect(Collectors.toSet());

        boolean allowed = Arrays.stream(requiredRoles)
                .map(this::normalize)
                .anyMatch(currentRoles::contains);

        if (!allowed) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "You do not have permission for this action.");
        }

        return sessionVo;
    }

    private String normalize(String role) {
        String normalized = role == null ? "" : role.trim().toUpperCase();
        return normalized.startsWith("ROLE_") ? normalized.substring("ROLE_".length()) : normalized;
    }
}
