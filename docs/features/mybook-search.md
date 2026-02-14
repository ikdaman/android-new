# 내 책 통합 검색

## 개요
등록된 나의 책 목록에서 검색어로 책을 검색하는 기능. 페이지네이션 지원.
홈 화면(내 서점)의 검색 아이콘을 누르면 내 책 검색 화면(MyBookSearchScreen)으로 이동.

## 구현 현황

| 계층 | 파일 | 상태 |
|------|------|------|
| API | `remote/.../api/MyBookService.kt` | 구현 완료 |
| Response | `remote/.../model/mybook/MyBookSearchResponse.kt` | 구현 완료 |
| Domain Model | `domain/.../model/MyBookSearch.kt` | 구현 완료 |
| UseCase | `domain/.../usecase/mybook/SearchMyBooksUseCase.kt` | 구현 완료 |
| Repository | `data/.../repository/MyBookRepositoryImpl.kt` | 구현 완료 |
| ViewModel | `presentation/.../viewmodel/MyBookSearchViewModel.kt` | 구현 완료 |
| Screen | `ui/.../screen/MyBookSearchScreen.kt` | 구현 완료 |
| Test | `presentation/.../viewmodel/MyBookSearchViewModelTest.kt` | 구현 완료 |

## Flow
1. 홈 화면에서 검색 아이콘 클릭
2. MyBookSearchScreen으로 이동
3. 사용자가 검색어 입력 후 검색 실행
4. 서버 검색 API 호출 (검색어 + 페이지네이션)
5. 검색 결과를 리스트로 표시
6. 무한 스크롤로 다음 페이지 자동 로드

## UI 구성
- **상단**: TitleBar ("내 책 검색" 타이틀 + 뒤로가기 버튼)
- **검색 입력**: 텍스트 필드 + 검색 버튼 (IME Search 액션 지원)
- **검색 결과**: LazyColumn으로 표시
  - 각 아이템 구성 (Row):
    - 좌측: 책 표지 썸네일 (AsyncImage, 80x112dp)
    - 우측 Column:
      1. `[내 서점]` 또는 `[히스토리]` 태그 (readingStatus 기반)
      2. 날짜 정보 (연월일까지만 표시)
      3. 책 제목 (최대 2줄)
      4. 작가명

### readingStatus별 표시 기준
| readingStatus | 태그 | 날짜 표시 |
|---------------|------|-----------|
| `TODO` | `[내 서점]` (초록색) | `createdDate` |
| 그 외 (`READING`, `READ` 등) | `[히스토리]` (파란색) | `startedDate ~ finishedDate` |

## 페이지네이션
- 페이지 크기: 10
- 무한 스크롤: LazyColumn의 마지막 2개 아이템 도달 시 `loadMore()` 호출
- 결과가 10개 미만이면 마지막 페이지로 판단하여 loadMore 중지

## API
- **Endpoint:** `GET /mybooks`
- **인증:** Bearer 토큰 필요
- **Query Parameters:**
  - `query` (String, 필수): 검색어
  - `page` (Int, 선택): 페이지 번호 (0-indexed)
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
MyBookSearchScreen
  → MyBookSearchViewModel.search(query)
    → SearchMyBooksUseCase(query, page, size)
      → MyBookRepository.searchMyBooks(query, page, size)
        → MyBookDataSource.searchMyBooks(query, page, size)
          → MyBookService.searchMyBooks(query, page, size) [GET /mybooks?query=xxx&page=0&size=10]
        → MyBookSearchEntity.toDomain() → MyBookSearch
    ← Flow<DataResource<MyBookSearch>> (Loading → Success | Error)
  ← searchResults: StateFlow<List<MyBookSearchItem>>
```

## 응답 데이터 상세
| 필드 | 타입 | 설명 |
|------|------|------|
| totalPages | Int | 전체 페이지 수 |
| nowPage | Int | 현재 페이지 |
| totalElements | Int | 전체 결과 수 |
| books[].mybookId | Int | 나의 책 ID |
| books[].readingStatus | String | 독서 상태 (TODO, READING, READ 등) |
| books[].createdDate | String | 등록일 |
| books[].startedDate | String? | 독서 시작일 |
| books[].finishedDate | String? | 독서 종료일 |
| books[].bookInfo.title | String | 책 제목 |
| books[].bookInfo.author | List<String> | 저자 목록 |
| books[].bookInfo.coverImage | String? | 표지 이미지 URL |
| books[].bookInfo.description | String? | 책 설명 |

## 화면 이동 플로우
```
HomeScreen (내 서점)
  └─ 검색 아이콘 클릭 → MyBookSearchScreen (SEARCH_MY_BOOK_ROUTE)
      ├─ 검색어 입력 → 검색 실행
      ├─ 검색 결과 표시 (무한 스크롤 페이지네이션)
      │   └─ 책 아이템 클릭 → BookInfoScreen (BookInfo/{mybookId})
      ├─ 검색 결과 없음 → 빈 리스트 표시
      └─ 뒤로가기 → HomeScreen
```

## 테스트 (MyBookSearchViewModelTest)
- 검색 성공 시 결과 반환 확인
- 빈 검색어 입력 시 API 호출하지 않음
- 검색 에러 시 로딩 상태 해제
- loadMore로 결과 누적 확인
- 마지막 페이지 도달 시 loadMore 중지
- 새 검색 시 이전 결과 초기화
