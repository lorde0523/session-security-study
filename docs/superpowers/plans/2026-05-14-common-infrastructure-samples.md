# Common Infrastructure Samples Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add reusable common infrastructure examples for async, cache, exception, DDL-to-entity generation, dynamic datasource, mail, Swagger, paging, Quartz, and SessionVo role checks.

**Architecture:** Common code lives under `com.example.sessionsecurity.common` and learning endpoints live under `com.example.sessionsecurity.sample`. The implementation stays thin: each feature has one reusable service/configuration and one small sample controller or service showing how other developers should use it.

**Tech Stack:** Spring Boot 3.3.x, Java 21, Spring Cache, Caffeine, Spring Mail, MyBatis 3.x starter, JPA, Quartz, Spring AOP, Springdoc OpenAPI, MockMvc/JUnit.

---

### Task 1: Dependencies And Configuration

**Files:**
- Modify: `build.gradle`
- Modify: `src/main/resources/application.yml`
- Modify: `src/test/resources/application-test.yml`

- [ ] Add cache, Caffeine, mail, MyBatis, Quartz, and AOP dependencies.
- [ ] Add common sample configuration properties for cache, mail, four Oracle users, and scheduler.
- [ ] Keep test profile on H2 and disable dynamic datasource routing in tests.

### Task 2: Common API Response And Exception

**Files:**
- Create: `src/main/java/com/example/sessionsecurity/common/response/ApiResponse.java`
- Create: `src/main/java/com/example/sessionsecurity/common/exception/BusinessException.java`
- Create: `src/main/java/com/example/sessionsecurity/common/exception/ErrorCode.java`
- Create: `src/main/java/com/example/sessionsecurity/common/exception/GlobalExceptionHandler.java`
- Create: `src/main/java/com/example/sessionsecurity/sample/exception/ExceptionSampleController.java`

- [ ] Add standard response and exception shape.
- [ ] Add sample endpoint that throws a business exception.

### Task 3: Async, Cache, Mail, Quartz Samples

**Files:**
- Create common config/services in `common.async`, `common.cache`, `common.mail`, `common.scheduler`.
- Create sample controllers/services in `sample.async`, `sample.cache`, `sample.mail`, `sample.scheduler`.

- [ ] Add async executor and sample async task.
- [ ] Add simple cache and Caffeine cache configurations using `app.cache.type`.
- [ ] Add mail sender service and sample endpoint.
- [ ] Add Quartz sample job and endpoint that registers a one-shot job.

### Task 4: SessionVo Role Check Common

**Files:**
- Create: `src/main/java/com/example/sessionsecurity/common/security/CurrentSessionProvider.java`
- Create: `src/main/java/com/example/sessionsecurity/common/security/RoleChecker.java`
- Create: `src/main/java/com/example/sessionsecurity/common/security/RequiredRoles.java`
- Create: `src/main/java/com/example/sessionsecurity/common/security/RequiredRolesAspect.java`
- Create: `src/main/java/com/example/sessionsecurity/sample/security/RoleCheckSampleController.java`

- [ ] Allow direct role checks through `RoleChecker`.
- [ ] Allow annotation role checks through `@RequiredRoles`.
- [ ] Add sample endpoints for current session, direct check, annotation check, and manager/admin check.

### Task 5: DDL Entity Generator, Dynamic Datasource, Paging, Swagger Samples

**Files:**
- Create common DDL generator in `common.ddl`.
- Create dynamic datasource classes in `common.datasource`.
- Create paging helpers in `common.paging`.
- Create sample controllers in `sample.jpa`, `sample.mybatis`, `sample.paging`, `sample.swagger`.

- [ ] Convert simple Oracle DDL text into JPA entity source text.
- [ ] Add routing datasource using four configured Oracle users and `DbUserContext`.
- [ ] Add JPA and MyBatis paging helpers.
- [ ] Add Swagger annotation sample controller.

### Task 6: Tests And Verification

**Files:**
- Create: `src/test/java/com/example/sessionsecurity/common/ddl/DdlToEntityGeneratorTest.java`
- Create: `src/test/java/com/example/sessionsecurity/common/paging/PagingSupportTest.java`
- Create: `src/test/java/com/example/sessionsecurity/common/security/RoleCheckerTest.java`

- [ ] Add focused unit tests for pure common utilities.
- [ ] Run `gradle test` when Gradle is available.
