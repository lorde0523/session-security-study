package com.example.sessionsecurity.common.datasource;

import com.zaxxer.hikari.HikariDataSource;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableConfigurationProperties(MultiDataSourceProperties.class)
@ConditionalOnProperty(prefix = "app.datasource", name = "routing-enabled", havingValue = "true")
public class MultiDataSourceConfig {

    @Bean
    @Primary
    DataSource dataSource(DataSourceProperties baseProperties, MultiDataSourceProperties multiProperties) {
        Map<Object, Object> targets = new LinkedHashMap<>();
        multiProperties.getUsers().forEach((key, user) ->
                targets.put(key, createDataSource(baseProperties, user))
        );

        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(targets);
        routingDataSource.setDefaultTargetDataSource(targets.getOrDefault("main", targets.values().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("At least one datasource user is required."))));
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }

    private DataSource createDataSource(
            DataSourceProperties baseProperties,
            MultiDataSourceProperties.DbUserProperties userProperties
    ) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(baseProperties.getUrl());
        dataSource.setDriverClassName(baseProperties.getDriverClassName());
        dataSource.setUsername(userProperties.getUsername());
        dataSource.setPassword(userProperties.getPassword());
        dataSource.setInitializationFailTimeout(-1);
        return dataSource;
    }
}
