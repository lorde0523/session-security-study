# 세션 기반 API 보안 설계

## 목표

Gradle, Java 21, Oracle DB, Spring Security, Swagger를 사용하는 학습용 Spring Boot 프로젝트를 만든다.

가장 먼저 구현할 기능은 `SessionVo`를 기준으로 API 접근 권한을 제어하는 것이다. 세션 정보는 두 가지 방식으로 가져올 수 있어야 한다.

1. 실제 애플리케이션 흐름: `HttpSession`에 저장된 `SessionVo`를 읽는다.
2. 백엔드 단독 테스트 흐름: 화면이 아직 없을 때 Swagger에서 요청 헤더로 값을 보내고, 그 값으로 임시 `SessionVo`를 만든다.

두 방식은 같은 권한 검증 로직을 사용해야 한다. 그래야 Swagger 테스트용 코드가 실제 보안 흐름과 분리된 우회로가 되지 않는다.

## 프로젝트 기준

Spring Boot 3.x, Gradle, Java 21을 사용한다.

주요 의존성은 다음과 같다.

- Spring Web
- Spring Security
- Spring Validation
- Spring Data JPA
- Oracle JDBC driver
- Springdoc OpenAPI for Swagger UI

첫 구현에서는 Oracle 연결 설정을 `application.yml`에 넣어두되, 보안 샘플 기능은 실제 DB 데이터에 의존하지 않는다. 이렇게 하면 처음 학습할 때 요청 흐름, 세션 처리, 권한 검증에 집중할 수 있다.

## 핵심 모델

`SessionVo`는 현재 요청 사용자의 인증 정보를 담는 객체다.

필드는 다음과 같다.

- `userId`: 사용자 ID
- `userName`: 사용자 이름
- `uuid`: 화면 meta 정보로 넘어오는 uuid
- `client`: 화면 meta 정보로 넘어오는 client
- `roles`: 사용자의 권한 목록. 예: `ADMIN`, `USER`, `MANAGER`

Spring Security 내부에서는 권한을 `ROLE_` 접두사가 붙은 authority로 변환한다. 예를 들어 `ADMIN`은 `ROLE_ADMIN`으로 변환된다.

## 세션 정보 조회 방식

`SessionResolver` 컴포넌트를 만든다.

공개 역할은 하나다.

```java
Optional<SessionVo> resolve(HttpServletRequest request)
```

조회 순서는 다음과 같다.

1. `HttpSession`에 저장된 `SessionVo`를 먼저 확인한다.
2. 세션에 값이 없으면 개발/Swagger 테스트용 요청 헤더를 확인한다.

개발용 헤더는 다음과 같다.

- `X-USER-ID`
- `X-USER-NAME`
- `X-UUID`
- `X-CLIENT`
- `X-ROLES`

`X-ROLES`는 쉼표로 여러 권한을 받을 수 있다. 예: `USER`, `ADMIN,USER`, `MANAGER`

헤더 기반 fallback은 설정으로 켜고 끌 수 있어야 한다.

```yaml
app:
  security:
    header-session-enabled: true
```

이 설정을 두는 이유는 Swagger 테스트용 기능이 운영 환경에서 의도치 않게 열리는 것을 막기 위해서다.

## 검증 규칙

보호 대상 API 요청은 다음 조건을 만족해야 한다.

- `SessionVo`가 존재해야 한다.
- `uuid`가 비어 있으면 안 된다.
- `client`가 비어 있으면 안 된다.
- 최소 한 개 이상의 권한이 있어야 한다.
- 사용자의 권한이 요청 API에 접근 가능한 권한이어야 한다.

`SessionVo`가 없거나 필수 값이 잘못된 경우 `401 Unauthorized`를 반환한다.

`SessionVo`는 있지만 권한이 부족한 경우 `403 Forbidden`을 반환한다.

Swagger, 샘플 로그인 API, 샘플 로그아웃 API 같은 공개 경로는 `SessionVo` 없이 접근할 수 있다.

## 보안 흐름

Spring Security와 커스텀 `OncePerRequestFilter`를 사용한다.

필터의 역할은 다음과 같다.

1. 공개 경로는 검증하지 않고 통과시킨다.
2. `SessionResolver`로 `SessionVo`를 조회한다.
3. `uuid`, `client`, 권한 목록을 검증한다.
4. 권한 목록을 `GrantedAuthority`로 변환한다.
5. `Authentication` 객체를 만들어 `SecurityContextHolder`에 저장한다.

그 다음 URL별 접근 권한은 `SecurityFilterChain`에서 처리한다.

예시 권한 규칙은 다음과 같다.

- `/api/admin/**`: `ADMIN`
- `/api/manager/**`: `MANAGER` 또는 `ADMIN`
- `/api/user/**`: `USER`, `MANAGER`, `ADMIN`
- `/api/common/**`: 인증된 사용자 전체

## 샘플 로그인

실제 세션 흐름을 이해하기 위한 백엔드 단독 샘플 로그인 API를 만든다.

엔드포인트는 다음과 같다.

```http
POST /api/auth/login-sample
```

요청 본문 예시는 다음과 같다.

```json
{
  "userId": "admin",
  "userName": "Admin User",
  "uuid": "swagger-test-uuid",
  "client": "swagger"
}
```

학습용으로 사용자 ID에 따라 권한을 코드에서 부여한다.

- `admin`: `ADMIN`, `USER`
- `manager`: `MANAGER`, `USER`
- 그 외 사용자: `USER`

이 API는 `SessionVo`를 `HttpSession`에 저장하고, 저장된 세션 정보를 응답으로 반환한다.

로그아웃 API도 만든다.

```http
POST /api/auth/logout
```

이 API는 현재 세션을 무효화한다.

## Swagger 지원

Swagger UI에서 전역 헤더를 넣을 수 있게 설정한다.

- `X-USER-ID`
- `X-USER-NAME`
- `X-UUID`
- `X-CLIENT`
- `X-ROLES`

Swagger에서는 두 가지 방식으로 보호된 API를 테스트할 수 있다.

1. `/api/auth/login-sample`을 먼저 호출해서 서버 세션 쿠키를 사용한다.
2. `app.security.header-session-enabled=true`일 때 요청 헤더를 직접 넣는다.

## 샘플 API

권한별 접근 결과를 바로 확인할 수 있도록 작은 샘플 컨트롤러를 만든다.

엔드포인트는 다음과 같다.

- `GET /api/common/me`: 정상 `SessionVo`가 있는 모든 사용자
- `GET /api/user/profile`: `USER`, `MANAGER`, `ADMIN`
- `GET /api/manager/report`: `MANAGER`, `ADMIN`
- `GET /api/admin/dashboard`: `ADMIN`

각 API는 현재 세션 정보와 짧은 메시지를 반환한다. 이렇게 하면 어떤 방식으로 인증되었는지, 어떤 권한으로 통과했는지 쉽게 확인할 수 있다.

## 에러 응답

보안 에러는 일관된 JSON 형식으로 반환한다.

`401 Unauthorized` 예시는 다음과 같다.

```json
{
  "status": 401,
  "code": "UNAUTHORIZED",
  "message": "Session information is missing or invalid."
}
```

`403 Forbidden` 예시는 다음과 같다.

```json
{
  "status": 403,
  "code": "FORBIDDEN",
  "message": "You do not have permission to access this API."
}
```

컨트롤러에 도달하기 전에 보안 에러가 발생할 수 있으므로 Spring Security의 authentication entry point와 access denied handler에서 이 JSON을 만든다.

## 설정

`application.yml`을 사용한다.

중요 설정 예시는 다음과 같다.

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521/XEPDB1
    username: sample
    password: sample
    driver-class-name: oracle.jdbc.OracleDriver

app:
  security:
    header-session-enabled: true
```

Oracle 설정은 로컬 개발용 예시 값이며 나중에 실제 환경에 맞게 바꿀 수 있다.

## 테스트 전략

보안 동작을 중심으로 MockMvc 테스트를 작성한다.

테스트 범위는 다음과 같다.

- Swagger와 auth 공개 경로는 세션 없이 접근 가능하다.
- 보호된 API를 세션 없이 호출하면 401을 반환한다.
- `uuid` 또는 `client`가 없으면 401을 반환한다.
- 헤더 fallback으로 `USER` 권한을 보내면 `/api/user/profile`에 접근 가능하다.
- 헤더 fallback으로 `USER` 권한만 보내면 `/api/admin/dashboard` 접근은 403이 된다.
- 샘플 로그인 API로 세션을 만들면 세션 기반으로 보호된 API에 접근할 수 있다.

MockMvc를 사용하는 이유는 실제 서버를 띄우지 않고도 Spring Security 필터와 세션 동작을 함께 검증할 수 있기 때문이다.

## 이번 단계에서 제외하는 것

이번 구현에서는 실제 사용자 테이블, 비밀번호 검증, JWT, refresh token, 프론트 화면, DB 기반 권한 관리는 만들지 않는다.

이 기능들은 세션과 권한 검증 흐름을 충분히 이해한 뒤 다음 단계에서 추가한다.
