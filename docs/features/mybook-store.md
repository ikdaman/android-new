# 내 서점 목록 조회

## 개요
"내 서점"에 등록된 책 목록을 조회하는 기능. Spring Page 형식의 페이지네이션 지원.
앱의 홈 화면(HomeScreen)이 내 서점 화면이며, 하단 탭 "내 서점"으로 접근.

## 구현 현황

| 계층 | 파일 | 상태 |
|------|------|------|
| API | `remote/.../api/MyBookService.kt` (`getStoreBooks`) | 구현 완료 |
| Response | `remote/.../model/mybook/StoreBookResponse.kt` | 구현 완료 |
| Domain Model | `domain/.../model/StoreBook.kt` | 구현 완료 |
| UseCase | `domain/.../usecase/mybook/GetStoreBooksUseCase.kt` | 구현 완료 |
| ViewModel | `presentation/.../viewmodel/MainViewModel.kt` | 구현 완료 |
| Screen | `ui/.../screen/HomeScreen.kt` | 구현 완료 |
| Test | `presentation/.../viewmodel/MainViewModelTest.kt` | 구현 완료 |

## Flow
1. 앱 실행 또는 하단 탭 "내 서점" 클릭
2. HomeScreen composable 진입 시 `LaunchedEffect`로 `refreshStoreBooks()` 호출
3. 서버 API 호출 (페이지네이션)
4. 서점 목록을 LazyColumn으로 표시
5. 스크롤 시 무한 스크롤로 다음 페이지 로드

## UI 구성
- **상단 헤더 (HomeHeader)**:
  - 설정 아이콘 (우측 상단)
  - 인사말 ("오늘 {닉네임}님의 눈에 꽂힌 책은 무엇이었나요?")
  - "읽고 싶은 책 적어두기" 버튼 → SearchBookScreen (알라딘 검색)
  - 정렬 정보 + 총 권수 + 검색 아이콘 → MyBookSearchScreen (내 책 검색)
- **책 목록**: HomeBookItem (표지, 날짜(연월일), 제목, 작가, 설명)
- **무한 스크롤**: 마지막 2개 아이템 도달 시 loadMore

## 탭 전환 시 새로고침
- `composable(HOME_ROUTE)` 진입 시 `LaunchedEffect(backStackEntry)`로 `refreshStoreBooks()` 호출
- 다른 탭에서 돌아올 때마다 최신 데이터 조회
- `MainViewModel.init`에서는 `fetchStoreBooks()`를 호출하지 않음 (중복 호출 방지)

## API
- **Endpoint:** `GET /mybooks/store`
- **인증:** Bearer 토큰 필요
- **Query Parameters:**
  - `keyword` (String, 선택): 검색 키워드
  - `page` (Int, 선택): 페이지 번호 (0-indexed)
  - `size` (Int, 선택): 페이지 크기 (기본 5)
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
HomeScreen
  → MainViewModel.refreshStoreBooks() (LaunchedEffect on composable entry)
    → GetStoreBooksUseCase(keyword?, page?, size?)
      → MyBookRepository.getStoreBooks(keyword, page, size)
        → MyBookDataSource.getStoreBooks(keyword, page, size)
          → MyBookService.getStoreBooks(keyword, page, size) [GET /mybooks/store?page=0&size=5]
        → StoreBookEntity.toDomain() → StoreBook
    ← Flow<DataResource<StoreBook>> (Loading → Success | Error)
  ← storeBooks: StateFlow<List<StoreBookItem>>
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
MainScreen > BottomNavBar "내 서점" 탭
  └─ HomeScreen (HOME_ROUTE)
      ├─ 책 아이템 클릭 → BookInfoScreen (BookInfo/{mybookId})
      ├─ 검색 아이콘 클릭 → MyBookSearchScreen (SEARCH_MY_BOOK_ROUTE)
      ├─ "읽고 싶은 책 적어두기" → SearchBookScreen (알라딘 검색)
      ├─ 설정 아이콘 → SettingScreen
      └─ 무한 스크롤 → loadMore()
```
