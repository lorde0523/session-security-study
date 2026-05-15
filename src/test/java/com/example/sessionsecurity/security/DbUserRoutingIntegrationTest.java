package com.example.sessionsecurity.security;

import com.example.sessionsecurity.common.datasource.DbUserContext;
import com.example.sessionsecurity.common.response.ApiResponse;
import java.util.Map;
import java.util.Optional;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Test
    void selectsDbUserFromFirstRequestPathSegment() throws Exception {
        mockMvc.perform(get("/read/test/routing-user/first-segment")
                        .header("X-USER-ID", "user")
                        .header("X-USER-NAME", "Normal User")
                        .header("X-UUID", "swagger-test-uuid")
                        .header("X-CLIENT", "swagger")
                        .header("X-ROLES", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.selectedDbUser", is("read")));
    }

    @TestConfiguration
    static class FirstSegmentRoutingTestConfiguration {

        @Bean
        FirstSegmentRoutingTestController firstSegmentRoutingTestController() {
            return new FirstSegmentRoutingTestController();
        }
    }

    @RestController
    @RequestMapping("/read/test")
    static class FirstSegmentRoutingTestController {

        @GetMapping("/routing-user/first-segment")
        ApiResponse<Map<String, String>> firstSegmentRouting() {
            return ApiResponse.ok(Map.of(
                    "selectedDbUser", Optional.ofNullable(DbUserContext.get()).orElse("none")
            ));
        }
    }
}
