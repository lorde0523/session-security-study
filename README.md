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

## 테스트

```powershell
gradle test
```

테스트는 MockMvc로 Spring Security 필터, 세션, request meta header 검증을 함께 확인합니다.
