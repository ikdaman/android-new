# 소셜 로그인

## 개요
소셜 계정(Google, Naver, Kakao)을 이용한 로그인 기능

## Flow
1. 사용자가 소셜 로그인 버튼 클릭
2. 소셜 SDK를 통해 소셜 인증 수행 (Google/Naver/Kakao)
3. 소셜 인증 성공 시 `socialAccessToken`, `provider`, `providerId` 획득
4. 서버 로그인 API 호출
5. 서버로부터 `Authorization` 토큰, `refresh-token`, `nickname` 수신
6. 토큰 및 인증 정보를 로컬 DataStore에 저장
7. UI에 로그인 성공 상태 전달

## API
- **Endpoint:** `POST /auth/login`
- **Headers:**
  - `social-token`: 소셜 액세스 토큰
- **Request Body:**
```json
{
  "provider": "GOOGLE" | "NAVER" | "KAKAO",
  "providerId": "소셜 고유 ID"
}
```
- **Response Headers:**
  - `Authorization`: JWT 액세스 토큰
  - `refresh-token`: 리프레시 토큰
- **Response Body:**
```json
{
  "nickname": "사용자 닉네임"
}
```

## 데이터 흐름
```
UI (LoginViewModel)
  → LoginUseCase(authType)
    → AuthRepository.googleLogin() / naverLogin() / kakaoLogin()
      → SocialAuthDataSource (소셜 SDK 인증)
      → AuthDataSource.login(token, provider, providerId)
        → AuthService.login() [POST /auth/login]
      → AuthDataStoreSource.saveAuthInfo() (토큰 로컬 저장)
  ← Flow<LoginState> (Loading → Success | Error)
```

## 화면 이동 플로우
```
LoginScreen
  ├─ 로그인 성공 (LoginState.Success)
  │   └─ → MainScreen (popUpTo LoginScreen inclusive)
  ├─ 신규 사용자 (LoginState.SignupRequired, HTTP 404)
  │   └─ → SignupScreen (socialToken/provider/providerId 전달)
  └─ 로그인 실패 (LoginState.Error)
      └─ → LoginScreen (Snackbar 에러 메시지 표시)

인증 만료 시:
  DomainAuthEvent.LOGIN_REQUIRED
    └─ 앱 어디서든 → LoginScreen (popUpTo MainScreen inclusive)
```

## 상태 (LoginState)
| 상태 | 설명 |
|------|------|
| Loading | 로그인 진행 중 |
| Success | 로그인 성공 |
| SignupRequired(socialToken, provider, providerId) | 신규 사용자 - 회원가입 필요 |
| Error(message) | 로그인 실패 (에러 메시지 포함) |

## 에러 케이스
- 소셜 인증 실패: 사용자 취소 또는 소셜 SDK 오류
- 서버 응답 실패: HTTP 400/401/403/500 등
- 토큰 누락: 서버 응답에 Authorization 또는 refresh-token 없음
- 네트워크 오류: 연결 실패
