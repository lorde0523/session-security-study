package com.example.sessionsecurity.common.multiDb2;

import com.zaxxer.hikari.HikariDataSource;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(MultiDb2DataSourceProperties.class)
@ConditionalOnProperty(prefix = "app.datasource", name = "enum-routing-enabled", havingValue = "true")
public class MultiDb2DataSourceConfig {

    @Bean
    DataSource multiDb2DataSource(MultiDb2DataSourceProperties properties) {
        Map<DataSourceKey, Object> enumTargetDataSources = new EnumMap<>(DataSourceKey.class);

        for (Map.Entry<String, MultiDb2DataSourceProperties.User> entry : properties.getUsers().entrySet()) {
            DataSourceKey key = parseDataSourceKey(entry.getKey());
            MultiDb2DataSourceProperties.User user = entry.getValue();

            enumTargetDataSources.put(key, createDataSource(
                    key.name().toLowerCase() + "-pool",
                    properties,
                    user
            ));
        }

        DataSourceKey defaultKey = parseDataSourceKey(properties.getDefaultKey());
        Object defaultDataSource = enumTargetDataSources.get(defaultKey);

        if (defaultDataSource == null) {
            throw new IllegalStateException("Default DataSource is not configured. defaultKey=" + defaultKey);
        }

        MultiDb2RoutingDataSource routingDataSource = new MultiDb2RoutingDataSource();
        routingDataSource.setTargetDataSources(new LinkedHashMap<>(enumTargetDataSources));
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    private DataSourceKey parseDataSourceKey(String key) {
        try {
            return DataSourceKey.valueOf(key);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "Invalid DataSourceKey: " + key + ". Check app.datasource.users or default-key.",
                    e
            );
        }
    }

    private DataSource createDataSource(
            String poolName,
            MultiDb2DataSourceProperties properties,
            MultiDb2DataSourceProperties.User user
    ) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setPoolName(poolName);
        dataSource.setDriverClassName(properties.getDriverClassName());
        dataSource.setJdbcUrl(properties.getUrl());
        dataSource.setUsername(user.getUsername());
        dataSource.setPassword(user.getPassword());
        dataSource.setInitializationFailTimeout(-1);
        return dataSource;
    }
}
