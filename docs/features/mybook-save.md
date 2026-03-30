# 나의 책 추가

## 개요
새로운 책을 나의 책 목록에 등록하는 기능. 알라딘 검색 결과 또는 직접 입력으로 추가.

## Flow
1. 알라딘 검색으로 책 선택 또는 직접 입력
2. 추가 정보 입력 (읽고 싶은 이유, 독서 날짜 등)
3. 서버 API 호출
4. 등록 성공 시 UI에 반영

## API
- **Endpoint:** `POST /mybooks`
- **인증:** Bearer 토큰 필요
- **Request Body:**
```json
{
  "bookInfo": {
    "source": "ALADIN" | "MANUAL",
    "aladinId": 12345,
    "isbn": "9788966262281",
    "title": "책 제목",
    "author": "저자",
    "publisher": "출판사",
    "description": "책 설명",
    "totalPage": 300,
    "publishDate": "2024-01-01",
    "coverImage": "https://..."
  },
  "historyInfo": {
    "startedDate": "2024-01-01",
    "finishedDate": null
  },
  "reason": "읽고 싶은 이유"
}
```
- **Response:** 200 OK

## 데이터 흐름
```
UI
  → SaveMyBookUseCase (기존 SaveManualBookInfoUseCase 또는 신규)
    → BackendRepository / MyBookRepository
      → BackendDataSource / MyBookDataSource
        → BackendApiService.saveMyBook(SaveMyBookRequest) [POST /mybooks]
  ← Flow<DataResource<Boolean>> (Loading → Success | Error)
```

## bookInfo 필드 상세
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| source | String | O | 출처: ALADIN(검색), MANUAL(직접입력) |
| aladinId | Int? | X | 알라딘 도서 ID (ALADIN일 때) |
| isbn | String? | X | ISBN |
| title | String | O | 책 제목 |
| author | String | O | 저자 |
| publisher | String? | X | 출판사 |
| description | String? | X | 책 설명 |
| totalPage | Int? | X | 총 페이지 수 |
| publishDate | String? | X | 출판일 |
| coverImage | String? | X | 표지 이미지 URL |

## 참고
- 기존 `DataManualBookInfo` 형식에서 새 `SaveMyBookRequest` 형식으로 변경
- bookInfo와 historyInfo를 분리하여 구조화

## 비로그인 저장 플로우
비로그인 유저도 책 검색/추가 화면에 접근 가능. 저장 시 로그인 필요.

```
AddBookScreen / ManualBookInputScreen
  └─ "저장" 버튼 클릭
      ├─ 로그인 상태 → BookRegisterBottomSheet → 저장 API 호출
      └─ 비로그인 상태 → pendingSave 플래그 설정 → LoginScreen 이동
          ├─ Snackbar: "로그인 후 사용이 가능합니다"
          ├─ 뒤로가기 → AddBookScreen 복귀
          ├─ 로그인 성공 → popBack → AddBookScreen 복귀 → BottomSheet 자동 표시
          └─ 회원가입 → SignupScreen → 완료 → popBack(Login inclusive) → AddBookScreen 복귀
```

## 화면 이동 플로우
```
HomeScreen
  └─ [+] ADD BOOK 버튼 → SearchBookScreen (알라딘 도서 검색)
      ├─ 검색 결과에서 책 선택
      │   └─ → AddBookScreen (선택한 책 정보 전달)
      │       ├─ "저장" 버튼 → BookRegisterBottomSheet
      │       │   ├─ 내 서점 탭: reason만 입력 (historyInfo = null)
      │       │   └─ 히스토리 탭: reason + startDate/endDate 입력
      │       ├─ 확인 → SaveMyBook API 호출
      │       │   ├─ 성공 → MainScreen (popBackStack) + "책을 저장했어요" 스낵바
      │       │   └─ 실패 → 에러 메시지 표시
      │       └─ 뒤로 가기 → SearchBookScreen
      └─ "직접 입력" 버튼
          └─ → ManualBookInputScreen (직접 입력)
              ├─ 책 정보 수동 입력
              ├─ "저장" 버튼 → BookRegisterBottomSheet → SaveMyBook API 호출
              └─ 뒤로 가기 → SearchBookScreen

바코드 스캔:
  SearchBookScreen → BarcodeScreen
    └─ 바코드 인식 성공 → AddBookScreen (ISBN으로 검색된 책 정보)
```
