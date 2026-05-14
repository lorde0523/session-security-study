# Session Security Study Project

Spring Boot, Gradle, Java 21, Oracle DB, Spring Security, Swagger를 함께 공부하기 위한 예제 프로젝트입니다.

## 핵심 흐름

이 프로젝트는 `SessionVo`와 request meta header를 함께 사용해서 API 접근 권한을 검사합니다.

역할을 나누면 다음과 같습니다.

- `SessionVo`: 사용자 정보와 권한만 저장합니다.
- request header meta: 매 요청마다 `uuid`, `client`를 전달합니다.

즉 `uuid`, `client`는 `SessionVo`에 들어가지 않습니다. 보호된 API를 호출할 때마다 아래 header로 전달되어야 합니다.

```text
X-UUID: swagger-test-uuid
X-CLIENT: swagger
```

## SessionVo를 가져오는 방법

`SessionVo`를 가져오는 방법은 두 가지입니다.

1. 실제 흐름: `/api/auth/login-sample`을 호출해서 `HttpSession`에 `SessionVo`를 저장합니다.
2. Swagger 테스트 흐름: 화면이 없을 때 `X-USER-ID`, `X-USER-NAME`, `X-ROLES` header로 임시 `SessionVo`를 만듭니다.

두 방식 모두 같은 Spring Security 필터와 URL 권한 규칙을 사용합니다.

## 실행

Java 21과 Gradle이 설치되어 있어야 합니다.

```powershell
gradle bootRun
```

실행 후 Swagger UI를 엽니다.

```text
http://localhost:8080/swagger-ui/index.html
```

## Swagger Header 테스트

Swagger 우측 상단의 Authorize 버튼에서 다음 값을 넣고 테스트할 수 있습니다.

일반 사용자 예시:

```text
X-USER-ID: user
X-USER-NAME: Normal User
X-UUID: swagger-test-uuid
X-CLIENT: swagger
X-ROLES: USER
```

관리자 예시:

```text
X-USER-ID: admin
X-USER-NAME: Admin User
X-UUID: swagger-test-uuid
X-CLIENT: swagger
X-ROLES: ADMIN,USER
```

여기서 `X-USER-ID`, `X-USER-NAME`, `X-ROLES`는 Swagger fallback용 `SessionVo`를 만들기 위한 값입니다.

`X-UUID`, `X-CLIENT`는 fallback 여부와 관계없이 매 요청의 meta 값으로 검사됩니다.

## 세션 로그인 테스트

header fallback 대신 실제 세션 흐름을 확인하려면 먼저 이 API를 호출합니다.

```http
POST /api/auth/login-sample
```

요청 본문:

```json
{
  "userId": "admin",
  "userName": "Admin User"
}
```

이 API는 `userId`, `userName`, `roles`만 가진 `SessionVo`를 `HttpSession`에 저장합니다.

그 다음 보호된 API를 호출할 때도 request meta header는 반드시 보내야 합니다.

```text
X-UUID: swagger-test-uuid
X-CLIENT: swagger
```

## 권한별 API

- `GET /api/common/me`: 인증된 사용자 전체
- `GET /api/user/profile`: `USER`, `MANAGER`, `ADMIN`
- `GET /api/manager/report`: `MANAGER`, `ADMIN`
- `GET /api/admin/dashboard`: `ADMIN`

## 중요 파일

- `src/main/java/com/example/sessionsecurity/security/SessionVo.java`: 사용자와 권한만 담는 세션 정보
- `src/main/java/com/example/sessionsecurity/security/SessionResolver.java`: 세션 또는 테스트 header에서 `SessionVo` 조회
- `src/main/java/com/example/sessionsecurity/security/SessionAuthenticationFilter.java`: `SessionVo`와 request meta header 검증 후 Spring Security 인증 생성
- `src/main/java/com/example/sessionsecurity/security/SecurityConfig.java`: URL별 접근 권한 설정
- `src/main/java/com/example/sessionsecurity/auth/AuthController.java`: 샘플 로그인/로그아웃
- `src/main/java/com/example/sessionsecurity/api/SampleApiController.java`: 권한 테스트용 API
- `src/main/java/com/example/sessionsecurity/swagger/SwaggerConfig.java`: Swagger header 설정

## 공통 샘플 모듈

다른 개발자가 가져다 쓰기 쉽게 공통 기능은 `common`, 사용 예시는 `sample` 패키지에 나눠두었습니다.

```text
com.example.sessionsecurity.common
com.example.sessionsecurity.sample
```

추가된 공통 샘플은 다음과 같습니다.

- Async: `common.async`, `sample.async`
- Cache simple/Caffeine: `common.cache`, `sample.cache`
- Exception/API response: `common.exception`, `common.response`, `sample.exception`
- SessionVo 권한 체크: `common.security`, `sample.security`
- DDL to JPA Entity generator: `common.ddl`, `sample.jpa`
- Multi DB dynamic routing: `common.datasource`, `sample.mybatis`
- SMTP mail: `common.mail`, `sample.mail`
- Paging for JPA/MyBatis: `common.paging`, `sample.paging`
- Quartz scheduler: `common.scheduler`, `sample.scheduler`
- Swagger 작성 샘플: `sample.swagger`

## Cache 설정

기본 Spring Cache와 Caffeine을 설정값으로 바꿔가며 테스트할 수 있습니다.

```yaml
app:
  cache:
    type: simple # simple | caffeine
```

샘플 API:

- `GET /api/sample/cache/simple/{id}`
- `GET /api/sample/cache/caffeine/{id}`
- `DELETE /api/sample/cache`

## SessionVo 권한 체크 공통 사용법

직접 체크:

```java
roleChecker.requireAny("ADMIN", "MANAGER");
```

어노테이션 체크:

```java
@RequiredRoles("ADMIN")
public ApiResponse<?> adminOnly() {
    return ApiResponse.ok();
}
```

샘플 API:

- `GET /api/sample/roles/me`
- `GET /api/sample/roles/admin-direct`
- `GET /api/sample/roles/admin-annotation`
- `GET /api/sample/roles/manager-or-admin`

## Multi DB 설정

Oracle JDBC URL은 같고 username/password만 4개로 나뉘는 상황을 기준으로 구성했습니다.

```yaml
app:
  datasource:
    routing-enabled: true
    users:
      main:
        username: sample
        password: sample
      batch:
        username: sample_batch
        password: sample_batch
      read:
        username: sample_read
        password: sample_read
      report:
        username: sample_report
        password: sample_report
```

서비스나 매퍼 호출 메서드에 붙여 사용할 수 있습니다.

```java
@UseDbUser("report")
public void selectReportData() {
    // JPA repository or MyBatis mapper call
}
```

## Paging

JPA는 `PageRequestDto.toPageable()`을 사용하고, MyBatis는 `offset`, `limit` 파라미터를 사용합니다.

```java
PageRequestDto request = new PageRequestDto(1, 20);
Pageable pageable = request.toPageable();
Map<String, Integer> params = MyBatisPagingSupport.parameters(request);
```

Oracle MyBatis SQL 예시:

```sql
SELECT *
FROM TB_SAMPLE
ORDER BY SAMPLE_ID DESC
OFFSET #{offset} ROWS FETCH NEXT #{limit} ROWS ONLY
```

## DDL to Entity

간단한 Oracle `CREATE TABLE` DDL을 JPA Entity 소스 문자열로 변환하는 학습용 generator를 넣었습니다.

샘플 API:

- `POST /api/sample/jpa/entity-source`

## SMTP / Quartz / Async

샘플 API:

- `GET /api/sample/async/message`
- `POST /api/sample/mail`
- `POST /api/sample/quartz/once`

SMTP는 `spring.mail.*`, Quartz는 Spring Boot 기본 in-memory store 기준입니다.

## 테스트

```powershell
gradle test
```

테스트는 MockMvc로 Spring Security 필터, 세션, request meta header 검증을 함께 확인합니다.
