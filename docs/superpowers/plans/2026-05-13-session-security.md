# Session Security Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a Java 21 Spring Boot Gradle project that demonstrates session-based API authorization with a Swagger/header fallback.

**Architecture:** A custom `OncePerRequestFilter` resolves `SessionVo` from `HttpSession` first and from development headers second. It validates uuid/client/roles, writes an `Authentication` to the Spring Security context, and lets `SecurityFilterChain` enforce URL role rules.

**Tech Stack:** Java 21, Spring Boot 3.3.x, Gradle, Spring Security, Spring Web, Spring Validation, Spring Data JPA, Oracle JDBC, Springdoc OpenAPI, JUnit 5, MockMvc.

---

### Task 1: Project Skeleton

**Files:**
- Create: `settings.gradle`
- Create: `build.gradle`
- Create: `src/main/resources/application.yml`
- Create: `src/main/java/com/example/sessionsecurity/SessionSecurityApplication.java`

- [ ] **Step 1: Create Gradle settings**

```groovy
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = 'session-security'
```

- [ ] **Step 2: Create Gradle build**

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.5'
    id 'io.spring.dependency-management' version '1.1.6'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'com.oracle.database.jdbc:ojdbc11'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.springframework.security:spring-security-test'
    testRuntimeOnly 'com.h2database:h2'
}

tasks.named('test') {
    useJUnitPlatform()
}
```

### Task 2: Security Tests First

**Files:**
- Create: `src/test/java/com/example/sessionsecurity/security/SecurityIntegrationTest.java`
- Create: `src/test/resources/application-test.yml`

- [ ] Write MockMvc tests for public login, unauthorized requests, missing meta values, header fallback authorization, forbidden admin access, and session login access.
- [ ] Run `gradle test --tests com.example.sessionsecurity.security.SecurityIntegrationTest` and expect failure before production classes exist.

### Task 3: Session Model And Resolver

**Files:**
- Create: `src/main/java/com/example/sessionsecurity/security/SessionVo.java`
- Create: `src/main/java/com/example/sessionsecurity/security/AppSecurityProperties.java`
- Create: `src/main/java/com/example/sessionsecurity/security/SessionResolver.java`
- Create: `src/main/java/com/example/sessionsecurity/security/SessionValidationException.java`

- [ ] Implement `SessionVo` as the session user context.
- [ ] Implement `AppSecurityProperties` for `app.security.header-session-enabled`.
- [ ] Implement `SessionResolver` to check `HttpSession` first, then `X-USER-ID`, `X-USER-NAME`, `X-UUID`, `X-CLIENT`, `X-ROLES` headers.

### Task 4: Security Filter And Handlers

**Files:**
- Create: `src/main/java/com/example/sessionsecurity/security/SessionAuthenticationFilter.java`
- Create: `src/main/java/com/example/sessionsecurity/security/JsonAuthenticationEntryPoint.java`
- Create: `src/main/java/com/example/sessionsecurity/security/JsonAccessDeniedHandler.java`
- Create: `src/main/java/com/example/sessionsecurity/security/SecurityConfig.java`

- [ ] Implement filter validation for session, uuid, client, and roles.
- [ ] Convert roles to Spring Security authorities with the `ROLE_` prefix.
- [ ] Configure public paths and role rules for `/api/admin/**`, `/api/manager/**`, `/api/user/**`, and `/api/common/**`.
- [ ] Return JSON for 401 and 403 responses.

### Task 5: Auth, Sample APIs, And Swagger

**Files:**
- Create: `src/main/java/com/example/sessionsecurity/auth/LoginSampleRequest.java`
- Create: `src/main/java/com/example/sessionsecurity/auth/AuthController.java`
- Create: `src/main/java/com/example/sessionsecurity/api/SampleApiController.java`
- Create: `src/main/java/com/example/sessionsecurity/swagger/SwaggerConfig.java`

- [ ] Implement `/api/auth/login-sample` to store `SessionVo` in `HttpSession`.
- [ ] Implement `/api/auth/logout` to invalidate the session.
- [ ] Implement protected sample APIs for common, user, manager, and admin roles.
- [ ] Configure Swagger global header inputs.

### Task 6: Verification And Notes

**Files:**
- Modify: `README.md`

- [ ] Document Swagger header examples and session login flow.
- [ ] Run `gradle test` when Java 21 and Gradle are available.
