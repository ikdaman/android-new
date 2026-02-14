# 닉네임 중복 확인

## 개요
회원가입 또는 닉네임 변경 전 닉네임 사용 가능 여부를 확인하는 기능

## Flow
1. 사용자가 닉네임 입력
2. 닉네임 중복 확인 API 호출
3. 사용 가능 여부 수신
4. UI에 결과 표시 (사용 가능/불가능)

## API
- **Endpoint:** `GET /members/check`
- **인증:** 불필요 (DefaultRetrofit 사용 가능하나 현재 AuthRetrofit 사용)
- **Query Parameters:**
  - `nickname`: 확인할 닉네임
- **Response Body:**
```json
{
  "available": true | false
}
```

## 데이터 흐름
```
UI
  → CheckNicknameUseCase(nickname)
    → MemberRepository.checkNickname(nickname)
      → MemberDataSource.checkNickname(nickname)
        → MemberService.checkNickname(nickname) [GET /members/check?nickname=xxx]
      → NicknameCheckEntity.toDomain() → NicknameCheck(available)
  ← Flow<DataResource<NicknameCheck>> (Loading → Success | Error)
```

## 응답 데이터
| 필드 | 타입 | 설명 |
|------|------|------|
| available | Boolean | true: 사용 가능, false: 이미 사용 중 |

## 참고
- 기존에 `TestApiService`에 있던 API를 `MemberService`로 이동
- 회원가입 flow와 닉네임 변경 flow에서 공통으로 사용
