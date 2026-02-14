# 나의 책 삭제

## 개요
등록된 나의 책을 삭제하는 기능

## Flow
1. 책 상세 또는 목록에서 삭제 요청
2. mybookId로 서버 삭제 API 호출
3. 삭제 성공 시 UI에서 해당 책 제거

## API
- **Endpoint:** `DELETE /mybooks/{mybookId}`
- **인증:** Bearer 토큰 필요
- **Path Parameter:** mybookId (Int)
- **Response:** 200 OK (body 없음)

## 데이터 흐름
```
UI
  → DeleteMyBookUseCase(mybookId)
    → MyBookRepository.deleteMyBook(mybookId)
      → MyBookDataSource.deleteMyBook(mybookId)
        → MyBookService.deleteMyBook(mybookId) [DELETE /mybooks/{mybookId}]
  ← Flow<DataResource<Unit>> (Loading → Success | Error)
```

## 화면 이동 플로우
```
BookDetailScreen (예정)
  └─ 삭제 버튼 클릭
      └─ 확인 다이얼로그
          ├─ 확인 → 삭제 API 호출
          │   ├─ 성공 → 이전 화면으로 복귀 (popBackStack)
          │   └─ 실패 → 에러 메시지 표시
          └─ 취소 → BookDetailScreen
```
※ 현재 API 레이어만 구현됨. UI 연동은 BookDetailScreen 구현 시 추가 예정.
