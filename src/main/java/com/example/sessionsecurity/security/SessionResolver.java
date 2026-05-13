package com.example.sessionsecurity.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class SessionResolver {

    private final AppSecurityProperties properties;

    public SessionResolver(AppSecurityProperties properties) {
        this.properties = properties;
    }

    public Optional<SessionVo> resolve(HttpServletRequest request) {
        return resolveFromHttpSession(request)
                .or(() -> resolveFromHeaders(request));
    }

    private Optional<SessionVo> resolveFromHttpSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Optional.empty();
        }

        Object value = session.getAttribute(SessionVo.SESSION_ATTRIBUTE_NAME);
        if (value instanceof SessionVo sessionVo) {
            return Optional.of(sessionVo);
        }
        return Optional.empty();
    }

    private Optional<SessionVo> resolveFromHeaders(HttpServletRequest request) {
        if (!properties.isHeaderSessionEnabled()) {
            return Optional.empty();
        }

        String userId = request.getHeader("X-USER-ID");
        String userName = request.getHeader("X-USER-NAME");
        String uuid = request.getHeader("X-UUID");
        String client = request.getHeader("X-CLIENT");
        List<String> roles = parseRoles(request.getHeader("X-ROLES"));

        if (!StringUtils.hasText(userId)
                && !StringUtils.hasText(uuid)
                && !StringUtils.hasText(client)
                && roles.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new SessionVo(userId, userName, uuid, client, roles));
    }

    private List<String> parseRoles(String headerValue) {
        if (!StringUtils.hasText(headerValue)) {
            return List.of();
        }

        return Arrays.stream(headerValue.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .map(role -> role.startsWith("ROLE_") ? role.substring("ROLE_".length()) : role)
                .map(String::toUpperCase)
                .distinct()
                .toList();
    }
}
