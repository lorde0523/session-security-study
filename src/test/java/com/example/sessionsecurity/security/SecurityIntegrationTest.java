package com.example.sessionsecurity.security;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginSampleIsPublicAndCreatesSession() throws Exception {
        mockMvc.perform(post("/api/auth/login-sample")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "admin",
                                  "userName": "Admin User"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is("admin")))
                .andExpect(jsonPath("$.roles[0]", is("ADMIN")))
                .andExpect(jsonPath("$.roles[1]", is("USER")));
    }

    @Test
    void securedApiWithoutSessionReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/common/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.code", is("UNAUTHORIZED")));
    }

    @Test
    void securedApiWithoutMetaUuidReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/common/me")
                        .header("X-USER-ID", "user")
                        .header("X-CLIENT", "swagger")
                        .header("X-ROLES", "USER"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.code", is("UNAUTHORIZED")));
    }

    @Test
    void securedApiWithoutMetaClientReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/common/me")
                        .header("X-USER-ID", "user")
                        .header("X-UUID", "swagger-test-uuid")
                        .header("X-ROLES", "USER"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.code", is("UNAUTHORIZED")));
    }

    @Test
    void userRoleCanAccessUserApiWithHeaderFallback() throws Exception {
        mockMvc.perform(get("/api/user/profile")
                        .header("X-USER-ID", "user")
                        .header("X-USER-NAME", "Normal User")
                        .header("X-UUID", "swagger-test-uuid")
                        .header("X-CLIENT", "swagger")
                        .header("X-ROLES", "USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("user profile")))
                .andExpect(jsonPath("$.session.userId", is("user")));
    }

    @Test
    void userRoleCannotAccessAdminApi() throws Exception {
        mockMvc.perform(get("/api/admin/dashboard")
                        .header("X-USER-ID", "user")
                        .header("X-USER-NAME", "Normal User")
                        .header("X-UUID", "swagger-test-uuid")
                        .header("X-CLIENT", "swagger")
                        .header("X-ROLES", "USER"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)))
                .andExpect(jsonPath("$.code", is("FORBIDDEN")));
    }

    @Test
    void sessionLoginAllowsCommonApiAccess() throws Exception {
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login-sample")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": "manager",
                                  "userName": "Manager User"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);

        mockMvc.perform(get("/api/common/me")
                        .session(session)
                        .header("X-UUID", "swagger-test-uuid")
                        .header("X-CLIENT", "swagger"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("current session")))
                .andExpect(jsonPath("$.session.userId", is("manager")))
                .andExpect(jsonPath("$.session.roles[0]", is("MANAGER")))
                .andExpect(jsonPath("$.session.roles[1]", is("USER")));
    }
}
