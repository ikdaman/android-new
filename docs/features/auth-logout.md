# 로그아웃

## 개요
서버 로그아웃 및 소셜 SDK 로그아웃을 함께 수행

## Flow
1. 사용자가 SettingScreen에서 로그아웃 버튼 클릭
2. 저장된 provider 정보 조회 (GetProviderUseCase)
3. provider에 맞는 소셜 로그아웃 타입 결정
4. 서버 로그아웃 API 호출 (Bearer 토큰 사용)
5. 서버 로그아웃 성공 시 소셜 SDK 로그아웃 수행
6. 로컬 DataStore의 토큰 및 인증 정보 삭제
7. UI에 로그아웃 성공 상태 전달
8. LoginScreen으로 이동

## API
- **Endpoint:** `DELETE /auth/logout`
- **Headers:**
  - `Authorization`: Bearer {액세스 토큰} (AuthInterceptor가 자동 추가)
- **Request Body:** 없음
- **Response:** 200 OK (body 없음)

## 데이터 흐름
```
UI (SettingViewModel)
  → GetProviderUseCase()
    → AuthRepository.getProvider()
      → AuthDataStoreSource.getProvider() (저장된 provider 조회)
  → LogoutUseCase(authType)
    → AuthRepository.googleLogout() / naverLogout() / kakaoLogout()
      → AuthDataSource.logout()
        → AuthService.logout() [DELETE /auth/logout]
      → SocialAuthDataSource (소셜 SDK 로그아웃)
      → AuthDataStoreSource.clear() (로컬 토큰 삭제)
  ← Flow<LogoutState> (Loading → Success | Error)
```

## 화면 이동 플로우
```
SettingScreen (로그아웃 버튼 클릭)
  ├─ 로그아웃 성공 (SettingUIState.LogoutSuccess)
  │   └─ → LoginScreen (popUpTo MainScreen inclusive)
  └─ 로그아웃 실패 (SettingUIState.Error)
      └─ → SettingScreen (에러 메시지 표시)
```

## 상태 (SettingUIState)
| 상태 | 설명 |
|------|------|
| Init | 초기 상태 |
| Loading | 로그아웃 진행 중 |
| LogoutSuccess | 로그아웃 성공 |
| Error(message) | 로그아웃 실패 |

## 주의사항
- 서버 로그아웃이 성공해야 소셜 로그아웃을 진행
- 소셜 로그아웃 실패 시에도 서버 로그아웃은 이미 완료된 상태
- provider 정보가 없는 경우 에러 처리
