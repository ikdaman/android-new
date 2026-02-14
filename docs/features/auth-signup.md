# 소셜 회원가입

## 개요
소셜 인증 정보와 닉네임을 함께 전달하여 신규 회원가입

## Flow
1. 사용자가 소셜 인증 완료 후 닉네임 입력
2. 닉네임 중복 확인 (선택)
3. 서버 회원가입 API 호출
4. 서버로부터 `Authorization` 토큰, `refresh-token`, `nickname` 수신
5. 토큰 및 인증 정보를 로컬 DataStore에 저장
6. UI에 회원가입 성공 상태 전달

## API
- **Endpoint:** `POST /auth/signup`
- **Headers:**
  - `social-token`: 소셜 액세스 토큰
- **Request Body:**
```json
{
  "provider": "GOOGLE" | "NAVER" | "KAKAO",
  "providerId": "소셜 고유 ID",
  "nickname": "희망 닉네임"
}
```
- **Response Headers:**
  - `Authorization`: JWT 액세스 토큰
  - `refresh-token`: 리프레시 토큰
- **Response Body:**
```json
{
  "nickname": "등록된 닉네임"
}
```

## 데이터 흐름
```
UI
  → SignupUseCase(socialToken, provider, providerId, nickname)
    → AuthRepository.signup(socialToken, provider, providerId, nickname)
      → AuthDataSource.signup(socialToken, provider, providerId, nickname)
        → AuthService.signup() [POST /auth/signup]
      → AuthDataStoreSource.saveAuthInfo() (토큰 로컬 저장)
  ← Flow<SignupState> (Loading → Success | Error)
```

## 화면 이동 플로우
```
LoginScreen (소셜 로그인 → 서버 404 응답)
  └─ → SignupScreen (socialToken/provider/providerId를 Navigation 인자로 전달)
        ├─ 닉네임 입력 → "중복 확인" 버튼 → CheckNicknameUseCase 호출
        │   ├─ 사용 가능 → "가입하기" 버튼 활성화
        │   └─ 사용 불가 → Snackbar "이미 사용 중인 닉네임입니다."
        ├─ "가입하기" 버튼 클릭 → SignupUseCase 호출
        │   ├─ 성공 (SignupState.Success) → MainScreen (popUpTo LoginScreen inclusive)
        │   └─ 실패 (SignupState.Error) → Snackbar 에러 메시지
        └─ 뒤로 가기 → LoginScreen
```

## 상태 (SignupState)
| 상태 | 설명 |
|------|------|
| Loading | 회원가입 진행 중 |
| Success | 회원가입 성공 |
| Error(message) | 회원가입 실패 |

## 에러 케이스
- 닉네임 중복
- 이미 가입된 소셜 계정
- 서버 오류
