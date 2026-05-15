package com.example.sessionsecurity.common.datasource;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

class MultiDataSourceConfigTest {

    @Test
    void throwsWhenUrlMappingReferencesUndefinedUser() {
        MultiDataSourceConfig config = new MultiDataSourceConfig();
        DataSourceProperties base = new DataSourceProperties();
        base.setUrl("jdbc:h2:mem:testdb;MODE=Oracle;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        base.setDriverClassName("org.h2.Driver");

        MultiDataSourceProperties properties = new MultiDataSourceProperties();
        properties.setDefaultUser("main");

        MultiDataSourceProperties.DbUserProperties mainUser = new MultiDataSourceProperties.DbUserProperties();
        mainUser.setUsername("sa");
        mainUser.setPassword("");
        properties.setUsers(Map.of("main", mainUser));

        LinkedHashMap<String, String> mappings = new LinkedHashMap<>();
        mappings.put("/api/sample/**", "unknown");
        properties.setUrlUserMappings(mappings);

        assertThrows(IllegalStateException.class, () -> config.dataSource(base, properties));
    }
}
