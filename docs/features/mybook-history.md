# 독서 히스토리 조회

## 개요
독서 기록(히스토리)에 있는 책 목록을 조회하는 기능

## Flow
1. 히스토리 화면 진입
2. 서버 API 호출 (키워드 필터 + 페이지네이션)
3. 히스토리 목록 수신
4. UI에 목록 표시

## API
- **Endpoint:** `GET /mybooks/history`
- **인증:** Bearer 토큰 필요
- **Query Parameters:**
  - `keyword` (String, 선택): 검색 키워드
  - `page` (Int, 선택): 페이지 번호
  - `size` (Int, 선택): 페이지 크기
- **Response Body:**
```json
{
  "totalPages": 3,
  "nowPage": 0,
  "books": [
    {
      "mybookId": 1,
      "bookInfo": {
        "title": "완독한 책",
        "coverImage": "https://..."
      },
      "historyInfo": {
        "startedDate": "2024-01-01",
        "finishedDate": "2024-02-01"
      }
    }
  ]
}
```

## 데이터 흐름
```
UI (HistoryViewModel)
  → GetHistoryBooksUseCase(keyword?, page?, size?)
    → HistoryRepository.getHistoryBooks(keyword, page, size)
      → HistoryDataSource.getHistoryBooks(keyword, page, size)
        → HistoryService.getHistoryBooks(keyword, page, size) [GET /mybooks/history?keyword=xxx&page=0&size=30]
      → HistoryBookResponse.toData() → HistoryBookEntity.toDomain() → HistoryBook
  ← Flow<DataResource<HistoryBook>> (Loading → Success | Error)
```

## 변경 이력
- 기존 파라미터: `page`, `limit`, `sort`
- 변경 후 파라미터: `keyword`, `page`, `size`

## 화면 이동 플로우
```
MainScreen > Bottom Tab "History"
  └─ HistoryScreen (구현 완료)
      ├─ 화면 진입 시 독서 기록 목록 자동 로딩
      ├─ 키워드 검색 → 필터링된 결과 표시
      ├─ 페이지네이션 (스크롤 시 추가 로딩)
      └─ 책 아이템 클릭 → BookDetailScreen (예정)
```
※ HistoryScreen UI는 구현 완료. 상세 화면 연동 예정.
