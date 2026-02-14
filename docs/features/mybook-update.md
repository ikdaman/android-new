# 나의 책 정보 수정

## 개요
나의 책의 부가 정보(읽고 싶은 이유, 독서 날짜 등)를 수정하는 기능

## Flow
1. 사용자가 책 정보 수정 화면에서 내용 변경
2. 서버 API 호출 (mybookId + 수정할 필드)
3. 수정 완료된 mybookId 수신
4. UI에 변경 반영

## API
- **Endpoint:** `PATCH /mybooks/{mybookId}`
- **인증:** Bearer 토큰 필요
- **Path Parameter:** mybookId (Int)
- **Request Body:**
```json
{
  "reason": "수정된 이유/감상",
  "startedDate": "2024-01-01",
  "finishedDate": "2024-02-01"
}
```
- **Response Body:**
```json
{
  "mybookId": 1
}
```

## 데이터 흐름
```
UI
  → UpdateMyBookUseCase(mybookId, reason?, startedDate?, finishedDate?)
    → MyBookRepository.updateMyBook(mybookId, reason, startedDate, finishedDate)
      → MyBookDataSource.updateMyBook(mybookId, MyBookUpdateEntity)
        → MyBookService.updateMyBook(mybookId, MyBookUpdateRequest) [PATCH /mybooks/{id}]
  ← Flow<DataResource<Int>> (Loading → Success(mybookId) | Error)
```

## 수정 가능한 필드
| 필드 | 타입 | 설명 |
|------|------|------|
| reason | String? | 읽고 싶은 이유 또는 독서 감상 |
| startedDate | String? | 독서 시작일 (yyyy-MM-dd) |
| finishedDate | String? | 독서 완료일 (yyyy-MM-dd) |

## 참고
- 모든 필드가 nullable이므로 변경할 필드만 전달 가능
- 책의 기본 정보(제목, 저자 등)는 이 API로 수정 불가

## 화면 이동 플로우
```
BookDetailScreen (예정)
  └─ 수정 버튼 클릭
      └─ MyBookEditScreen (예정)
          ├─ 기존 정보 표시 (reason, historyInfo 등)
          ├─ 수정 → API 호출
          │   ├─ 성공 → BookDetailScreen (갱신된 정보)
          │   └─ 실패 → 에러 메시지 표시
          └─ 취소 → BookDetailScreen
```
※ 현재 API 레이어만 구현됨. 수정 전용 화면 구현 예정.
