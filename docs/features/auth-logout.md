# 로그아웃

## 개요
서버 로그아웃 및 소셜 SDK 로그아웃을 함께 수행

## Flow
1. 사용자가 로그아웃 버튼 클릭
2. 서버 로그아웃 API 호출 (Bearer 토큰 사용)
3. 서버 로그아웃 성공 시 소셜 SDK 로그아웃 수행
4. 로컬 DataStore의 토큰 및 인증 정보 삭제
5. UI에 로그아웃 성공 상태 전달

## API
- **Endpoint:** `DELETE /auth/logout`
- **Headers:**
  - `Authorization`: Bearer {액세스 토큰} (AuthInterceptor가 자동 추가)
- **Request Body:** 없음
- **Response:** 200 OK (body 없음)

## 데이터 흐름
```
UI (LoginViewModel)
  → LogoutUseCase(authType)
    → AuthRepository.googleLogout() / naverLogout() / kakaoLogout()
      → AuthDataSource.logout()
        → AuthService.logout() [DELETE /auth/logout]
      → SocialAuthDataSource (소셜 SDK 로그아웃)
      → AuthDataStoreSource.clear() (로컬 토큰 삭제)
  ← Flow<LogoutState> (Loading → Success | Error)
```

## 상태 (LogoutState)
| 상태 | 설명 |
|------|------|
| Loading | 로그아웃 진행 중 |
| Success | 로그아웃 성공 |
| Error(message) | 로그아웃 실패 |

## 주의사항
- 서버 로그아웃이 성공해야 소셜 로그아웃을 진행
- 소셜 로그아웃 실패 시에도 서버 로그아웃은 이미 완료된 상태
