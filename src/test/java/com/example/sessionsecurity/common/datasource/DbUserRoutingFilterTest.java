package com.example.sessionsecurity.common.datasource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.servlet.FilterChain;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class DbUserRoutingFilterTest {

    @AfterEach
    void tearDown() {
        DbUserContext.clear();
    }

    @Test
    void setsMappedUserAndClearsAfterRequest() throws Exception {
        MultiDataSourceProperties properties = new MultiDataSourceProperties();
        properties.setDefaultUser("main");
        LinkedHashMap<String, String> mappings = new LinkedHashMap<>();
        mappings.put("/api/read/**", "read");
        properties.setUrlUserMappings(mappings);

        DbUserRoutingFilter filter = new DbUserRoutingFilter(new DbUserRoutingRuleMatcher(properties));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/read/items");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> assertEquals("read", DbUserContext.get());

        filter.doFilter(request, response, chain);

        assertNull(DbUserContext.get());
    }

    @Test
    void fallsBackToDefaultUserWhenNoPatternMatches() throws Exception {
        MultiDataSourceProperties properties = new MultiDataSourceProperties();
        properties.setDefaultUser("main");

        DbUserRoutingFilter filter = new DbUserRoutingFilter(new DbUserRoutingRuleMatcher(properties));

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/other/path");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> assertEquals("main", DbUserContext.get());

        filter.doFilter(request, response, chain);

        assertNull(DbUserContext.get());
    }
}
