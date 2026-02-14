# Aladin API (외부)

알라딘 도서 검색 API. `AladinBookService`에서 정의.

## Base URL
`https://www.aladin.co.kr/`

## 인증
- TTB Key (`BuildConfig.TTB_KEY`) — 알라딘 API 키

---

## GET /ttb/api/ItemSearch.aspx

제목으로 도서를 검색한다.

| 항목 | 값 |
|------|-----|
| 인증 | TTB Key (Query Parameter) |
| Service | `AladinBookService.searchBookWithTitle()` |

### Request
| 파라미터 | 타입 | 기본값 | 필수 | 설명 |
|---------|------|--------|------|------|
| `ttbkey` | String | `BuildConfig.TTB_KEY` | O | API 키 |
| `query` | String | - | O | 검색어 (제목) |
| `queryType` | String | `"Title"` | X | 검색 타입 |
| `cover` | String | `"Big"` | X | 표지 크기 |
| `output` | String | `"js"` | X | 응답 형식 (JSON) |
| `version` | String | `"20131101"` | X | API 버전 |
| `maxResults` | Int | `50` | X | 최대 결과 수 |
| `start` | Int | `1` | X | 시작 페이지 (1-indexed) |

### Response
- `BookSearchResponse` — 검색 결과 목록
  - `totalResults`: 전체 결과 수
  - `item[]`: 책 목록 (title, author, publisher, isbn, cover, description, pubDate, subInfo 등)

### 호출 시점
- SearchBookScreen에서 제목 검색 시

---

## GET /ttb/api/ItemLookUp.aspx

ISBN으로 도서 상세 정보를 조회한다.

| 항목 | 값 |
|------|-----|
| 인증 | TTB Key (Query Parameter) |
| Service | `AladinBookService.searchBookWithIsbn()` |

### Request
| 파라미터 | 타입 | 기본값 | 필수 | 설명 |
|---------|------|--------|------|------|
| `ttbkey` | String | `BuildConfig.TTB_KEY` | O | API 키 |
| `ItemId` | String | - | O | ISBN13 코드 |
| `itemIdType` | String | `"ISBN13"` | X | ID 타입 |
| `cover` | String | `"Big"` | X | 표지 크기 |
| `output` | String | `"js"` | X | 응답 형식 (JSON) |
| `Version` | String | `"20131101"` | X | API 버전 |

### Response
- `BookSearchResponse` — 검색 결과 (단일 도서)

### 호출 시점
- BarcodeScreen에서 바코드 스캔 성공 시 ISBN으로 조회
