# 나의 책 상세 조회

## 개요
등록된 나의 책의 상세 정보를 조회하는 기능

## Flow
1. 책 목록에서 특정 책 선택
2. mybookId로 서버 API 호출
3. 책 정보, 독서 상태, 히스토리 정보 수신
4. UI에 상세 정보 표시

## API
- **Endpoint:** `GET /mybooks/{mybookId}`
- **인증:** Bearer 토큰 필요
- **Path Parameter:** mybookId (Int)
- **Response Body:**
```json
{
  "mybookId": "1",
  "readingStatus": "TODO" | "INPROGRESS" | "DONE",
  "shelfType": "STORE" | "HISTORY",
  "createdDate": "2024-01-01T00:00:00",
  "reason": "읽고 싶은 이유",
  "bookInfo": {
    "bookId": "1",
    "source": "ALADIN" | "MANUAL",
    "title": "책 제목",
    "author": "저자",
    "coverImage": "https://...",
    "publisher": "출판사",
    "totalPage": 300,
    "publishDate": "2024-01-01",
    "isbn": "9788966262281",
    "aladinId": "12345"
  },
  "historyInfo": {
    "startedDate": "2024-01-01",
    "finishedDate": "2024-02-01"
  }
}
```

## 데이터 흐름
```
UI
  → GetMyBookDetailUseCase(mybookId)
    → MyBookRepository.getMyBookDetail(mybookId)
      → MyBookDataSource.getMyBookDetail(mybookId)
        → MyBookService.getMyBookDetail(mybookId) [GET /mybooks/{mybookId}]
      → MyBookDetailEntity.toDomain() → MyBookDetail
  ← Flow<DataResource<MyBookDetail>> (Loading → Success | Error)
```

## 응답 데이터 상세
| 필드 | 타입 | 설명 |
|------|------|------|
| mybookId | String | 나의 책 고유 ID |
| readingStatus | String | 독서 상태: TODO(읽을 예정), INPROGRESS(읽는 중), DONE(완독) |
| shelfType | String | 서가 타입: STORE(내 서점), HISTORY(독서 기록) |
| createdDate | String | 등록일 |
| reason | String? | 읽고 싶은 이유/감상 |
| bookInfo | Object | 책 기본 정보 |
| historyInfo | Object | 독서 시작/완료 날짜 |

## 화면 이동 플로우
```
HistoryScreen 또는 StoreScreen (예정)
  └─ 책 아이템 클릭
      └─ → BookDetailScreen (예정, mybookId 전달)
          ├─ 책 정보 표시 (bookInfo, historyInfo, readingStatus 등)
          ├─ 수정 버튼 → 수정 화면
          ├─ 삭제 버튼 → 삭제 확인 다이얼로그
          └─ 뒤로 가기 → 이전 화면
```
※ 현재 API 레이어만 구현됨. 전용 상세 화면 구현 예정.
