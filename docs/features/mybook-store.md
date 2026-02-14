# 내 서점 목록 조회

## 개요
"내 서점"에 등록된 책 목록을 조회하는 기능. Spring Page 형식의 페이지네이션 지원.

## Flow
1. 내 서점 화면 진입
2. 서버 API 호출 (키워드 필터 + 페이지네이션)
3. 서점 목록 수신
4. UI에 목록 표시

## API
- **Endpoint:** `GET /mybooks/store`
- **인증:** Bearer 토큰 필요
- **Query Parameters:**
  - `keyword` (String, 선택): 검색 키워드
  - `page` (Int, 선택): 페이지 번호
  - `size` (Int, 선택): 페이지 크기
- **Response Body (Spring Page 형식):**
```json
{
  "content": [
    {
      "mybookId": 1,
      "createdDate": "2024-01-01",
      "bookInfo": {
        "title": "책 제목",
        "author": ["저자"],
        "coverImage": "https://...",
        "description": "책 설명"
      }
    }
  ],
  "totalPages": 3,
  "totalElements": 25,
  "last": false,
  "first": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 10,
  "empty": false
}
```

## 데이터 흐름
```
UI
  → GetStoreBooksUseCase(keyword?, page?, size?)
    → MyBookRepository.getStoreBooks(keyword, page, size)
      → MyBookDataSource.getStoreBooks(keyword, page, size)
        → MyBookService.getStoreBooks(keyword, page, size) [GET /mybooks/store?keyword=xxx&page=0&size=10]
      → StoreBookEntity.toDomain() → StoreBook
  ← Flow<DataResource<StoreBook>> (Loading → Success | Error)
```

## 페이지네이션 정보
| 필드 | 타입 | 설명 |
|------|------|------|
| totalPages | Int | 전체 페이지 수 |
| totalElements | Int | 전체 항목 수 |
| number | Int | 현재 페이지 (0-indexed) |
| size | Int | 페이지 크기 |
| first | Boolean | 첫 페이지 여부 |
| last | Boolean | 마지막 페이지 여부 |
| empty | Boolean | 빈 결과 여부 |

## 화면 이동 플로우
```
MainScreen > Bottom Tab (예정)
  └─ 서점 탭 선택
      └─ StoreScreen (예정)
          ├─ 내 서점 목록 표시 (페이지네이션)
          │   └─ 책 아이템 클릭 → BookDetailScreen (예정)
          ├─ 검색 → MyBook 검색 API 호출
          └─ 빈 목록 → "서점에 책을 추가해보세요" 안내
```
※ 현재 API 레이어만 구현됨. 전용 서점 화면 구현 예정.
