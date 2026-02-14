# 내 책 통합 검색

## 개요
등록된 나의 책 목록에서 검색어로 책을 검색하는 기능. 페이지네이션 지원.

## Flow
1. 사용자가 검색어 입력
2. 서버 검색 API 호출 (검색어 + 페이지네이션)
3. 검색 결과 수신 (책 목록 + 페이지 정보)
4. UI에 검색 결과 표시

## API
- **Endpoint:** `GET /mybooks`
- **인증:** Bearer 토큰 필요
- **Query Parameters:**
  - `query` (String, 필수): 검색어
  - `page` (Int, 선택): 페이지 번호
  - `size` (Int, 선택): 페이지 크기
- **Response Body:**
```json
{
  "totalPages": 5,
  "nowPage": 0,
  "totalElements": 48,
  "books": [
    {
      "mybookId": 1,
      "readingStatus": "INPROGRESS",
      "createdDate": "2024-01-01",
      "startedDate": "2024-01-05",
      "finishedDate": null,
      "bookInfo": {
        "title": "책 제목",
        "author": ["저자1", "저자2"],
        "coverImage": "https://...",
        "description": "책 설명"
      }
    }
  ]
}
```

## 데이터 흐름
```
UI
  → SearchMyBooksUseCase(query, page?, size?)
    → MyBookRepository.searchMyBooks(query, page, size)
      → MyBookDataSource.searchMyBooks(query, page, size)
        → MyBookService.searchMyBooks(query, page, size) [GET /mybooks?query=xxx&page=0&size=10]
      → MyBookSearchEntity.toDomain() → MyBookSearch
  ← Flow<DataResource<MyBookSearch>> (Loading → Success | Error)
```

## 응답 데이터 상세
| 필드 | 타입 | 설명 |
|------|------|------|
| totalPages | Int | 전체 페이지 수 |
| nowPage | Int | 현재 페이지 |
| totalElements | Int | 전체 결과 수 |
| books[].mybookId | Int | 나의 책 ID |
| books[].readingStatus | String | 독서 상태 |
| books[].bookInfo.title | String | 책 제목 |
| books[].bookInfo.author | List<String> | 저자 목록 |
| books[].bookInfo.coverImage | String? | 표지 이미지 URL |
