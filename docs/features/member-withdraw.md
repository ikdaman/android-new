# 회원 탈퇴

## 개요
로그인한 사용자의 계정을 삭제(탈퇴)하는 기능

## Flow
1. 사용자가 회원 탈퇴 확인
2. 서버 회원 탈퇴 API 호출
3. 탈퇴 성공 시 로컬 인증 정보 삭제 필요 (클라이언트 측 처리)
4. 로그인 화면으로 이동

## API
- **Endpoint:** `DELETE /members/me`
- **인증:** Bearer 토큰 필요
- **Request:** 없음
- **Response:** 200 OK (body 없음)

## 데이터 흐름
```
UI
  → WithdrawUseCase()
    → MemberRepository.withdraw()
      → MemberDataSource.withdraw()
        → MemberService.withdraw() [DELETE /members/me]
  ← Flow<DataResource<Unit>> (Loading → Success | Error)
```

## 후처리 (클라이언트)
- 로컬 DataStore 토큰 삭제
- 소셜 계정 연결 해제 (선택)
- 로그인 화면으로 이동

## 에러 케이스
- 인증 만료 (401)
- 서버 오류 (500)

## 주의사항
- 탈퇴는 되돌릴 수 없으므로 UI에서 재확인 필요
- 탈퇴 후 같은 소셜 계정으로 재가입 가능 여부는 서버 정책에 따름
