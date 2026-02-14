# History API

독서 히스토리 관련 API 엔드포인트. `HistoryService`에서 정의.

## Base URL
자체 백엔드 서버 (`BASE_URL` in `key.properties`)

---

## GET /mybooks/history

독서 히스토리(읽은 책) 목록을 조회한다.

| 항목 | 값 |
|------|-----|
| 인증 | Bearer JWT (`@AuthRetrofit`) |
| Service | `HistoryService.getHistoryBooks()` |
| Feature Doc | [mybook-history.md](../features/mybook-history.md) |

### Request
| 위치 | 파라미터 | 타입 | 필수 | 설명 |
|------|---------|------|------|------|
| Query | `keyword` | String | X | 검색 키워드 |
| Query | `page` | Int | X | 페이지 번호 (0-indexed) |
| Query | `size` | Int | X | 페이지 크기 |

### Response
- 히스토리 책 목록 (`HistoryBookResponse`)
- Spring Page 형식의 페이지네이션

### 호출 시점
- 하단 탭 "히스토리" 선택 시 `HistoryViewModel`에서 호출
- 로그인 필요 (미로그인 시 로그인 화면으로 리다이렉트)
