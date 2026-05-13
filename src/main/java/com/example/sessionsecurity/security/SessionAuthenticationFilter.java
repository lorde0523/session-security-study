package com.example.sessionsecurity.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/actuator/health"
    );

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final SessionResolver sessionResolver;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    public SessionAuthenticationFilter(
            SessionResolver sessionResolver,
            AuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.sessionResolver = sessionResolver;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_PATHS.stream().anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            SessionVo sessionVo = sessionResolver.resolve(request)
                    .orElseThrow(() -> new SessionValidationException("Session information is missing."));
            validate(sessionVo, request);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            sessionVo,
                            null,
                            toAuthorities(sessionVo.roles())
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (SessionValidationException ex) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new InsufficientAuthenticationException(ex.getMessage(), ex)
            );
        }
    }

    private void validate(SessionVo sessionVo, HttpServletRequest request) {
        if (!StringUtils.hasText(request.getHeader("X-UUID"))) {
            throw new SessionValidationException("Request meta uuid is required.");
        }
        if (!StringUtils.hasText(request.getHeader("X-CLIENT"))) {
            throw new SessionValidationException("Request meta client is required.");
        }
        if (sessionVo.roles().isEmpty()) {
            throw new SessionValidationException("At least one role is required.");
        }
    }

    private List<SimpleGrantedAuthority> toAuthorities(List<String> roles) {
        return roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
