# Auth API

인증 관련 API 엔드포인트. `AuthService`, `UserService`에서 정의.

## Base URL
자체 백엔드 서버 (`BASE_URL` in `key.properties`)

---

## POST /auth/login

소셜 로그인으로 서버 인증 토큰을 발급받는다.

| 항목 | 값 |
|------|-----|
| 인증 | 불필요 (`@DefaultRetrofit`) |
| Service | `AuthService.login()` |
| Feature Doc | [auth-login.md](../features/auth-login.md) |

### Request
| 위치 | 파라미터 | 타입 | 필수 | 설명 |
|------|---------|------|------|------|
| Header | `social-token` | String | O | 소셜 인증 토큰 |
| Body | `provider` | String | O | 소셜 제공자 (GOOGLE, NAVER, KAKAO) |
| Body | `providerId` | String | O | 소셜 제공자 고유 ID |

### Response
| 위치 | 필드 | 타입 | 설명 |
|------|------|------|------|
| Header | `Authorization` | String | JWT 액세스 토큰 |
| Header | `refresh-token` | String | 리프레시 토큰 |
| Body | `isRegistered` | Boolean | 기존 회원 여부 |

---

## POST /auth/signup

신규 회원가입 (닉네임 설정 포함).

| 항목 | 값 |
|------|-----|
| 인증 | 불필요 (`@DefaultRetrofit`) |
| Service | `AuthService.signup()` |
| Feature Doc | [auth-signup.md](../features/auth-signup.md) |

### Request
| 위치 | 파라미터 | 타입 | 필수 | 설명 |
|------|---------|------|------|------|
| Header | `social-token` | String | O | 소셜 인증 토큰 |
| Body | `provider` | String | O | 소셜 제공자 |
| Body | `providerId` | String | O | 소셜 제공자 고유 ID |
| Body | `nickname` | String | O | 닉네임 |

### Response
- `Authorization` + `refresh-token` 헤더 (login과 동일)

---

## DELETE /auth/logout

로그아웃. 서버 측 세션/토큰 무효화.

| 항목 | 값 |
|------|-----|
| 인증 | Bearer JWT (`@AuthRetrofit`) |
| Service | `AuthService.logout()` |
| Feature Doc | [auth-logout.md](../features/auth-logout.md) |

### Request
- 파라미터 없음

### Response
- `200 OK` (빈 응답)

---

## POST /auth/reissue

액세스 토큰 만료 시 리프레시 토큰으로 재발급.

| 항목 | 값 |
|------|-----|
| 인증 | 커스텀 헤더 (아래 참조) |
| Service | `UserService.reissue()` |
| Feature Doc | [auth-token-reissue.md](../features/auth-token-reissue.md) |

### Request
| 위치 | 파라미터 | 타입 | 필수 | 설명 |
|------|---------|------|------|------|
| Header | `Authorization` | String | O | 만료된 JWT 토큰 |
| Header | `refresh-token` | String | O | 리프레시 토큰 |

### Response
- `200 OK` + 새로운 `Authorization`, `refresh-token` 헤더
- 실패 시: DataStore 클리어 → 로그인 화면 강제 이동

### 호출 시점
- `TokenAuthenticator`에서 401 응답 수신 시 자동 호출
- 앱 코드에서 직접 호출하지 않음
