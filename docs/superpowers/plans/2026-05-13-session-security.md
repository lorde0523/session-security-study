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

- [ ] **Step 3: Create application config**

```yaml
spring:
  application:
    name: session-security
  datasource:
    url: jdbc:oracle:thin:@localhost:1521/XEPDB1
    username: sample
    password: sample
    driver-class-name: oracle.jdbc.OracleDriver
  jpa:
    hibernate:
      ddl-auto: none
    open-in-view: false

app:
  security:
    header-session-enabled: true
```

- [ ] **Step 4: Create boot application class**

```java
package com.example.sessionsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SessionSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(SessionSecurityApplication.class, args);
    }
}
```

### Task 2: Security Tests First

**Files:**
- Create: `src/test/java/com/example/sessionsecurity/security/SecurityIntegrationTest.java`
- Create: `src/test/resources/application-test.yml`

- [ ] **Step 1: Write MockMvc tests for public, unauthorized, forbidden, header fallback, and session login behavior**

```java
// See the implemented test file for exact assertions. Tests cover:
// public auth access, 401 without session, 401 missing request meta uuid/client,
// USER access to user API, USER forbidden from admin API,
// and session login enabling common API access.
```

- [ ] **Step 2: Run the test and verify RED**

Run: `gradle test --tests com.example.sessionsecurity.security.SecurityIntegrationTest`

Expected: FAIL before production security classes exist.

### Task 3: Session Model And Resolver

**Files:**
- Create: `src/main/java/com/example/sessionsecurity/security/SessionVo.java`
- Create: `src/main/java/com/example/sessionsecurity/security/AppSecurityProperties.java`
- Create: `src/main/java/com/example/sessionsecurity/security/SessionResolver.java`
- Create: `src/main/java/com/example/sessionsecurity/security/SessionValidationException.java`

- [ ] **Step 1: Implement immutable-ish session model, config properties, resolver, and validation exception**

```java
// SessionResolver checks HttpSession first, then enabled development headers:
// X-USER-ID, X-USER-NAME, X-ROLES.
```

- [ ] **Step 2: Run tests**

Run: `gradle test --tests com.example.sessionsecurity.security.SecurityIntegrationTest`

Expected: still FAIL until filter, config, and controllers are implemented.

### Task 4: Security Filter And Handlers

**Files:**
- Create: `src/main/java/com/example/sessionsecurity/security/SessionAuthenticationFilter.java`
- Create: `src/main/java/com/example/sessionsecurity/security/JsonAuthenticationEntryPoint.java`
- Create: `src/main/java/com/example/sessionsecurity/security/JsonAccessDeniedHandler.java`
- Create: `src/main/java/com/example/sessionsecurity/security/SecurityConfig.java`

- [ ] **Step 1: Implement filter that creates Spring Security authentication from `SessionVo`**

```java
// The filter validates SessionVo, request meta headers, and roles before setting SecurityContextHolder.
```

- [ ] **Step 2: Configure URL access rules**

```java
// /api/admin/** ADMIN
// /api/manager/** MANAGER or ADMIN
// /api/user/** USER, MANAGER, ADMIN
// /api/common/** authenticated
```

- [ ] **Step 3: Run tests**

Run: `gradle test --tests com.example.sessionsecurity.security.SecurityIntegrationTest`

Expected: controller-related failures remain until sample APIs exist.

### Task 5: Auth, Sample APIs, And Swagger

**Files:**
- Create: `src/main/java/com/example/sessionsecurity/auth/LoginSampleRequest.java`
- Create: `src/main/java/com/example/sessionsecurity/auth/AuthController.java`
- Create: `src/main/java/com/example/sessionsecurity/api/SampleApiController.java`
- Create: `src/main/java/com/example/sessionsecurity/swagger/SwaggerConfig.java`

- [ ] **Step 1: Implement sample login/logout**

```java
// login-sample stores SessionVo in HttpSession.
// logout invalidates HttpSession.
```

- [ ] **Step 2: Implement role-protected sample APIs**

```java
// common, user, manager, admin endpoints return current SessionVo and message.
```

- [ ] **Step 3: Configure Swagger headers**

```java
// Add OpenAPI security schemes for X-USER-ID, X-USER-NAME, X-UUID, X-CLIENT, X-ROLES.
```

- [ ] **Step 4: Run tests**

Run: `gradle test`

Expected: PASS when Java and Gradle are available.

### Task 6: Verification And Notes

**Files:**
- Modify: `README.md`

- [ ] **Step 1: Add learning notes and Swagger examples**

```markdown
# Session Security

Run with `gradle bootRun`, then open `http://localhost:8080/swagger-ui/index.html`.
Use either `/api/auth/login-sample` or the Swagger headers to test protected APIs.
```

- [ ] **Step 2: Final verification**

Run: `gradle test`

Expected: PASS when Java and Gradle are available.
