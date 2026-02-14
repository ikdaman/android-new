# 토큰 재발급

## 개요
액세스 토큰 만료 시 리프레시 토큰을 사용하여 자동으로 토큰을 갱신하는 기능.
OkHttp Authenticator를 통해 자동으로 처리됨.

## Flow
1. API 호출 시 401 Unauthorized 응답 수신
2. OkHttp의 TokenAuthenticator가 자동으로 개입
3. 로컬 DataStore에서 현재 Authorization과 refresh-token 조회
4. 토큰 재발급 API 호출
5. 새로운 토큰을 로컬 DataStore에 저장
6. AuthTokenProvider 캐시 갱신
7. 원래 요청을 새 토큰으로 재시도

## API
- **Endpoint:** `POST /auth/reissue`
- **Headers:**
  - `Authorization`: Bearer {만료된 액세스 토큰}
  - `refresh-token`: 리프레시 토큰
- **Request Body:** 없음
- **Response Headers:**
  - `Authorization`: 새 JWT 액세스 토큰
  - `refresh-token`: 새 리프레시 토큰
- **Response Body:** 없음

## 데이터 흐름
```
[자동 처리 - 사용자 개입 없음]

OkHttp 요청 → 401 응답
  → TokenAuthenticator.authenticate()
    → AuthDataStoreSource에서 토큰 조회
    → UserService.reissue() [POST /auth/reissue]
    → AuthDataStoreSource.saveToken() (새 토큰 저장)
    → AuthTokenProvider.updateToken() (캐시 갱신)
  → 원래 요청 새 토큰으로 재시도
```

## 실패 시 처리
- 리프레시 토큰도 만료된 경우:
  - 로컬 토큰 및 캐시 삭제
  - `AuthEvent.notify(LOGIN_REQUIRED)` 이벤트 발생
  - UI에서 로그인 화면으로 이동

## 인증 아키텍처
| 컴포넌트 | 역할 |
|---------|------|
| AuthInterceptor | 모든 인증 API 요청에 Bearer 토큰 자동 추가 |
| TokenAuthenticator | 401 응답 시 자동 토큰 재발급 |
| AuthTokenProvider | 메모리 캐시로 토큰 관리 (성능 최적화) |
| AuthDataStoreSource | 영속적 토큰 저장소 (DataStore) |
