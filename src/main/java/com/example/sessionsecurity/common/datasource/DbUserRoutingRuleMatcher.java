package com.example.sessionsecurity.common.datasource;

import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

@Component
@ConditionalOnProperty(prefix = "app.datasource", name = "routing-enabled", havingValue = "true")
public class DbUserRoutingRuleMatcher {

    private final MultiDataSourceProperties multiDataSourceProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public DbUserRoutingRuleMatcher(MultiDataSourceProperties multiDataSourceProperties) {
        this.multiDataSourceProperties = multiDataSourceProperties;
    }

    public String resolve(String requestUri) {
        for (Map.Entry<String, String> entry : multiDataSourceProperties.getUrlUserMappings().entrySet()) {
            if (pathMatcher.match(entry.getKey(), requestUri)) {
                return entry.getValue();
            }
        }
        return multiDataSourceProperties.getDefaultUser();
    }
}
