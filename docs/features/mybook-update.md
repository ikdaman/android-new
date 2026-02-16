# 나의 책 정보 수정

## 개요
나의 책의 부가 정보(읽고 싶은 이유, 독서 날짜, CUSTOM 책의 bookInfo 등)를 수정하는 기능

## Flow
1. BookInfoScreen에서 수정 버튼 클릭
2. BookEditBottomSheet 표시 (현재 상태에 맞는 탭 기본 선택)
3. 사용자가 내용 수정 후 저장 클릭
4. 서버 API 호출 (mybookId + 수정할 필드)
5. 수정 완료 시 스낵바 "책 정보를 수정했어요" + 책 정보 다시 로드

## API
- **Endpoint:** `PATCH /mybooks/{mybookId}`
- **인증:** Bearer 토큰 필요
- **Path Parameter:** mybookId (Int)
- **Request Body:**
```json
{
  "reason": "읽고싶은 이유",
  "historyInfo": {
    "startedDate": "2025-03-01",
    "endedDate": "2025-04-01"
  },
  "bookInfo": {
    "title": "책제목",
    "author": "작가",
    "publisher": "출판사",
    "publishDate": "yyyy-MM-dd",
    "ISBN": "9123456783111",
    "totalPage": 500
  }
}
```
- ALADIN 책: `bookInfo` 필드는 null (수정 불가)
- CUSTOM 책: `bookInfo` 필드로 책 기본 정보 수정 가능
- `endedDate`: "읽는 중"인 경우 null
- **Response Body:**
```json
{
  "mybookId": 1
}
```

## 데이터 흐름
```
UI (BookEditBottomSheet)
  → BookInfoViewModel.updateMyBook(reason?, startedDate?, finishedDate?, bookInfo...)
    → UpdateMyBookUseCase(mybookId, reason?, startedDate?, finishedDate?, bookInfo...)
      → MyBookRepository.updateMyBook(mybookId, MyBookUpdateEntity)
        → MyBookDataSource.updateMyBook(mybookId, MyBookUpdateEntity)
          → MyBookService.updateMyBook(mybookId, MyBookUpdateRequest) [PATCH /mybooks/{id}]
  ← Flow<DataResource<Int>> (Loading → Success(mybookId) | Error)
```

## 수정 가능한 필드
| 필드 | 타입 | 설명 |
|------|------|------|
| reason | String? | 읽고 싶은 이유 또는 독서 감상 |
| historyInfo.startedDate | String? | 독서 시작일 (yyyy-MM-dd) |
| historyInfo.endedDate | String? | 독서 완료일 (yyyy-MM-dd), "읽는 중"이면 null |
| bookInfo.title | String? | 책 제목 (CUSTOM 전용) |
| bookInfo.author | String? | 작가 (CUSTOM 전용) |
| bookInfo.publisher | String? | 출판사 (CUSTOM 전용) |
| bookInfo.publishDate | String? | 출간일 (CUSTOM 전용) |
| bookInfo.ISBN | String? | ISBN 13자리 (CUSTOM 전용) |
| bookInfo.totalPage | Int? | 총 페이지 수 (CUSTOM 전용) |

## 상태별 UI 분기
| 조건 | 탭 전환 | reason | historyInfo | bookInfo 수정 |
|------|---------|--------|-------------|--------------|
| STORE + ALADIN | 내 서점/히스토리 탭 가능 | O | 히스토리 선택 시 날짜 입력 | X |
| STORE + CUSTOM | 내 서점/히스토리 탭 가능 | O | 히스토리 선택 시 날짜 입력 | O |
| HISTORY + ALADIN | 내 서점/히스토리 탭 가능 | O | O (시작일/종료일) | X |
| HISTORY + CUSTOM | 내 서점/히스토리 탭 가능 | O | O (시작일/종료일) | O |

## 화면 이동 플로우
```
BookInfoScreen
  └─ 수정 버튼 클릭
      └─ BookEditBottomSheet (ModalBottomSheet)
          ├─ 현재 상태(STORE/HISTORY)에 맞는 탭 기본 선택
          ├─ 기존 정보 표시 (reason, historyInfo, bookInfo)
          ├─ 저장 → API 호출
          │   ├─ 성공 → Sheet 닫기 + 책 정보 다시 로드 + 스낵바
          │   └─ 실패 → 스낵바 에러 메시지
          └─ 닫기 → BookInfoScreen
```
