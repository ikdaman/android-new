# 독서 상태 변경

## 개요
나의 책의 독서 상태를 변경하는 기능. 독서 시작일/완료일을 함께 전달.

## Flow
1. 사용자가 독서 시작/완료 등 상태 변경
2. 서버 API 호출 (mybookId + 시작일/완료일)
3. 변경된 mybookId 수신
4. UI에 상태 변경 반영

## API
- **Endpoint:** `PATCH /mybooks/{mybookId}/reading-status`
- **인증:** Bearer 토큰 필요
- **Path Parameter:** mybookId (Int)
- **Request Body:**
```json
{
  "startedDate": "2024-01-01",
  "finishedDate": null
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
  → UpdateReadingStatusUseCase(mybookId, startedDate?, finishedDate?)
    → MyBookRepository.updateReadingStatus(mybookId, startedDate, finishedDate)
      → MyBookDataSource.updateReadingStatus(mybookId, startedDate, finishedDate)
        → MyBookService.updateReadingStatus(mybookId, ReadingStatusRequest) [PATCH /mybooks/{id}/reading-status]
  ← Flow<DataResource<Int>> (Loading → Success(mybookId) | Error)
```

## 독서 상태 전이
| 현재 상태 | 변경 가능 상태 | 설정 값 |
|----------|-------------|---------|
| TODO | INPROGRESS | startedDate 설정 |
| INPROGRESS | DONE | finishedDate 설정 |

## 날짜 형식
- ISO 8601: `yyyy-MM-dd` (예: "2024-01-15")
- nullable: 설정하지 않을 경우 null

## 화면 이동 플로우
```
BookDetailScreen (예정)
  └─ 독서 상태 변경 버튼 클릭
      └─ 상태 선택 (TODO → INPROGRESS → DONE)
          ├─ INPROGRESS 선택 → 시작일 입력
          ├─ DONE 선택 → 완료일 입력
          └─ API 호출
              ├─ 성공 → BookDetailScreen (갱신된 상태 표시)
              └─ 실패 → 에러 메시지 표시
```
※ 현재 API 레이어만 구현됨. UI 연동은 BookDetailScreen 구현 시 추가 예정.
