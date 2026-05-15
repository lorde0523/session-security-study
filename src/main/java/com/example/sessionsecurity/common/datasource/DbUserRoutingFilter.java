package com.example.sessionsecurity.common.datasource;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@ConditionalOnProperty(prefix = "app.datasource", name = "routing-enabled", havingValue = "true")
public class DbUserRoutingFilter extends OncePerRequestFilter {

    private final DbUserRoutingRuleMatcher ruleMatcher;

    public DbUserRoutingFilter(DbUserRoutingRuleMatcher ruleMatcher) {
        this.ruleMatcher = ruleMatcher;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            DbUserContext.set(ruleMatcher.resolve(request.getRequestURI()));
            filterChain.doFilter(request, response);
        } finally {
            DbUserContext.clear();
        }
    }
}
