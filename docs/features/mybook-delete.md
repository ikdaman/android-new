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
