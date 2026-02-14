# 닉네임 수정

## 개요
로그인한 사용자의 닉네임을 변경하는 기능

## Flow
1. 사용자가 새 닉네임 입력
2. (권장) 닉네임 중복 확인 API 먼저 호출
3. 닉네임 수정 API 호출
4. 변경된 닉네임 수신
5. UI에 변경 결과 반영

## API
- **Endpoint:** `PATCH /members/me`
- **인증:** Bearer 토큰 필요
- **Request Body:**
```json
{
  "nickname": "새로운 닉네임"
}
```
- **Response Body:**
```json
{
  "nickname": "변경된 닉네임"
}
```

## 데이터 흐름
```
UI
  → UpdateNicknameUseCase(nickname)
    → MemberRepository.updateNickname(nickname)
      → MemberDataSource.updateNickname(nickname)
        → MemberService.updateNickname(NicknameUpdateRequest) [PATCH /members/me]
      → MemberEntity.toDomain() → Member(nickname)
  ← Flow<DataResource<Member>> (Loading → Success | Error)
```

## 에러 케이스
- 닉네임 중복 (400)
- 인증 만료 (401 → 자동 토큰 재발급 시도)
- 유효하지 않은 닉네임 형식
