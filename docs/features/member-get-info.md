# 내 정보 조회

## 개요
로그인한 사용자의 닉네임 등 기본 정보를 조회하는 기능

## Flow
1. 마이페이지 등에서 내 정보 조회 요청
2. 서버 API 호출 (Bearer 토큰 자동 첨부)
3. 닉네임 정보 수신
4. UI에 회원 정보 표시

## API
- **Endpoint:** `GET /members/me`
- **인증:** Bearer 토큰 필요 (AuthInterceptor가 자동 추가)
- **Request:** 없음
- **Response Body:**
```json
{
  "nickname": "사용자 닉네임"
}
```

## 데이터 흐름
```
UI
  → GetMyInfoUseCase()
    → MemberRepository.getMyInfo()
      → MemberDataSource.getMyInfo()
        → MemberService.getMyInfo() [GET /members/me]
      → MemberEntity.toDomain() → Member(nickname)
  ← Flow<DataResource<Member>> (Loading → Success | Error)
```

## 응답 데이터
| 필드 | 타입 | 설명 |
|------|------|------|
| nickname | String | 사용자 닉네임 |
