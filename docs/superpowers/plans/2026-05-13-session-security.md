# Session Security Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a Java 21 Spring Boot Gradle project that demonstrates session-based API authorization with a Swagger/header fallback.

**Architecture:** A custom `OncePerRequestFilter` resolves `SessionVo` from `HttpSession` first and from development headers second. It validates request meta headers (`X-UUID`, `X-CLIENT`) separately from `SessionVo`, writes an `Authentication` to the Spring Security context, and lets `SecurityFilterChain` enforce URL role rules.

**Tech Stack:** Java 21, Spring Boot 3.3.x, Gradle, Spring Security, Spring Web, Spring Validation, Spring Data JPA, Oracle JDBC, Springdoc OpenAPI, JUnit 5, MockMvc.

---

### Task 1: Project Skeleton

**Files:**
- Create: `settings.gradle`
- Create: `build.gradle`
- Create: `src/main/resources/application.yml`
- Create: `src/main/java/com/example/sessionsecurity/SessionSecurityApplication.java`

### Task 2: Security Tests First

**Files:**
- Create: `src/test/java/com/example/sessionsecurity/security/SecurityIntegrationTest.java`
- Create: `src/test/resources/application-test.yml`

- [ ] Write MockMvc tests for public login, unauthorized requests, missing request meta values, header fallback authorization, forbidden admin access, and session login access.
- [ ] Run `gradle test --tests com.example.sessionsecurity.security.SecurityIntegrationTest` and expect failure before production classes exist.

### Task 3: Session Model And Resolver

**Files:**
- Create: `src/main/java/com/example/sessionsecurity/security/SessionVo.java`
- Create: `src/main/java/com/example/sessionsecurity/security/AppSecurityProperties.java`
- Create: `src/main/java/com/example/sessionsecurity/security/SessionResolver.java`
- Create: `src/main/java/com/example/sessionsecurity/security/SessionValidationException.java`

- [ ] Implement `SessionVo` with only user and role fields.
- [ ] Implement `AppSecurityProperties` for `app.security.header-session-enabled`.
- [ ] Implement `SessionResolver` to check `HttpSession` first, then `X-USER-ID`, `X-USER-NAME`, `X-ROLES` headers.

### Task 4: Security Filter And Handlers

**Files:**
- Create: `src/main/java/com/example/sessionsecurity/security/SessionAuthenticationFilter.java`
- Create: `src/main/java/com/example/sessionsecurity/security/JsonAuthenticationEntryPoint.java`
- Create: `src/main/java/com/example/sessionsecurity/security/JsonAccessDeniedHandler.java`
- Create: `src/main/java/com/example/sessionsecurity/security/SecurityConfig.java`

- [ ] Implement filter validation for `SessionVo`, `X-UUID`, `X-CLIENT`, and roles.
- [ ] Convert roles to Spring Security authorities with the `ROLE_` prefix.
- [ ] Configure public paths and role rules for `/api/admin/**`, `/api/manager/**`, `/api/user/**`, and `/api/common/**`.
- [ ] Return JSON for 401 and 403 responses.

### Task 5: Auth, Sample APIs, And Swagger

**Files:**
- Create: `src/main/java/com/example/sessionsecurity/auth/LoginSampleRequest.java`
- Create: `src/main/java/com/example/sessionsecurity/auth/AuthController.java`
- Create: `src/main/java/com/example/sessionsecurity/api/SampleApiController.java`
- Create: `src/main/java/com/example/sessionsecurity/swagger/SwaggerConfig.java`

- [ ] Implement `/api/auth/login-sample` to store only user and role data in `HttpSession`.
- [ ] Implement `/api/auth/logout` to invalidate the session.
- [ ] Implement protected sample APIs for common, user, manager, and admin roles.
- [ ] Configure Swagger global header inputs, including request meta headers.

### Task 6: Verification And Notes

**Files:**
- Modify: `README.md`

- [ ] Document Swagger header examples and session login flow.
- [ ] Run `gradle test` when Java 21 and Gradle are available.
