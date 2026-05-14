package com.example.sessionsecurity.common.security;

import com.example.sessionsecurity.common.exception.BusinessException;
import com.example.sessionsecurity.common.exception.ErrorCode;
import com.example.sessionsecurity.security.SessionVo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@FunctionalInterface
public interface CurrentSessionProvider {

    SessionVo current();

    static CurrentSessionProvider fromSecurityContext() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof SessionVo sessionVo)) {
                throw new BusinessException(ErrorCode.FORBIDDEN, "SessionVo is not available.");
            }
            return sessionVo;
        };
    }
}
