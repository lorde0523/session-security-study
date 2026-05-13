# Session Security Study Project

Spring Boot, Gradle, Java 21, Oracle DB, Spring Security, Swagger를 함께 공부하기 위한 예제 프로젝트입니다.

## 핵심 흐름

이 프로젝트는 `SessionVo`를 기준으로 API 접근 권한을 검사합니다.

`SessionVo`를 가져오는 방법은 두 가지입니다.

1. 실제 흐름: `/api/auth/login-sample`을 호출해서 `HttpSession`에 `SessionVo`를 저장합니다.
2. Swagger 테스트 흐름: 화면이 없을 때 요청 헤더로 임시 `SessionVo` 값을 보냅니다.

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

## Swagger 헤더 테스트

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

## 세션 로그인 테스트

헤더 대신 실제 세션 흐름을 확인하려면 먼저 이 API를 호출합니다.

```http
POST /api/auth/login-sample
```

요청 본문:

```json
{
  "userId": "admin",
  "userName": "Admin User",
  "uuid": "swagger-test-uuid",
  "client": "swagger"
}
```

그 다음 보호된 API를 호출하면 서버 세션의 `SessionVo`를 기준으로 권한을 검사합니다.

## 권한별 API

- `GET /api/common/me`: 인증된 사용자 전체
- `GET /api/user/profile`: `USER`, `MANAGER`, `ADMIN`
- `GET /api/manager/report`: `MANAGER`, `ADMIN`
- `GET /api/admin/dashboard`: `ADMIN`

## 중요 파일

- `src/main/java/com/example/sessionsecurity/security/SessionVo.java`: 세션 사용자 정보
- `src/main/java/com/example/sessionsecurity/security/SessionResolver.java`: 세션 또는 헤더에서 `SessionVo` 조회
- `src/main/java/com/example/sessionsecurity/security/SessionAuthenticationFilter.java`: `SessionVo` 검증 후 Spring Security 인증 생성
- `src/main/java/com/example/sessionsecurity/security/SecurityConfig.java`: URL별 접근 권한 설정
- `src/main/java/com/example/sessionsecurity/auth/AuthController.java`: 샘플 로그인/로그아웃
- `src/main/java/com/example/sessionsecurity/api/SampleApiController.java`: 권한 테스트용 API
- `src/main/java/com/example/sessionsecurity/swagger/SwaggerConfig.java`: Swagger 헤더 설정

## 테스트

```powershell
gradle test
```

테스트는 MockMvc로 Spring Security 필터와 세션 동작을 함께 검증합니다.
