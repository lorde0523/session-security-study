package com.example.sessionsecurity.security;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "app.datasource.routing-enabled=true",
        "app.datasource.default-user=main",
        "app.datasource.users.main.username=sa",
        "app.datasource.users.main.password=",
        "app.datasource.users.read.username=sa",
        "app.datasource.users.read.password=",
        "app.datasource.users.report.username=sa",
        "app.datasource.users.report.password=",
        "app.datasource.url-user-mappings[/api/sample/mybatis/routing-user/by-url]=read",
        "app.datasource.url-user-mappings[/api/sample/mybatis/routing-user/by-url-with-annotation]=read"
})
class DbUserRoutingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void selectsDbUserByUrlPattern() throws Exception {
        mockMvc.perform(get("/api/sample/mybatis/routing-user/by-url")
                        .header("X-USER-ID", "user")
                        .header("X-USER-NAME", "Normal User")
                        .header("X-UUID", "swagger-test-uuid")
                        .header("X-CLIENT", "swagger")
                        .header("X-ROLES", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.selectedDbUser", is("read")));
    }

    @Test
    void fallsBackToMainWhenNoUrlPatternMatches() throws Exception {
        mockMvc.perform(get("/api/sample/mybatis/routing-user/default")
                        .header("X-USER-ID", "user")
                        .header("X-USER-NAME", "Normal User")
                        .header("X-UUID", "swagger-test-uuid")
                        .header("X-CLIENT", "swagger")
                        .header("X-ROLES", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.selectedDbUser", is("main")));
    }

    @Test
    void useDbUserAnnotationOverridesUrlRouting() throws Exception {
        mockMvc.perform(get("/api/sample/mybatis/routing-user/by-url-with-annotation")
                        .header("X-USER-ID", "user")
                        .header("X-USER-NAME", "Normal User")
                        .header("X-UUID", "swagger-test-uuid")
                        .header("X-CLIENT", "swagger")
                        .header("X-ROLES", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.selectedDbUser", is("report")));
    }
}
