# MyBook API

나의 책 관련 API 엔드포인트. `MyBookService`에서 정의.
모든 엔드포인트는 Bearer JWT 인증 필요 (`@AuthRetrofit`).

## Base URL
자체 백엔드 서버 (`BASE_URL` in `key.properties`)

---

## POST /mybooks

새로운 책을 나의 책 목록에 등록한다.

| 항목 | 값 |
|------|-----|
| Service | `MyBookService.saveMyBook()` |
| Feature Doc | [mybook-save.md](../features/mybook-save.md) |

### Request Body
```json
{
  "bookInfo": {
    "source": "ALADIN",
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

### Response
- `200 OK` (빈 응답)

---

## GET /mybooks

등록된 나의 책 목록에서 검색한다.

| 항목 | 값 |
|------|-----|
| Service | `MyBookService.searchMyBooks()` |
| Feature Doc | [mybook-search.md](../features/mybook-search.md) |

### Request
| 위치 | 파라미터 | 타입 | 필수 | 설명 |
|------|---------|------|------|------|
| Query | `query` | String | O | 검색어 |
| Query | `page` | Int | X | 페이지 번호 (0-indexed) |
| Query | `size` | Int | X | 페이지 크기 |

### Response
```json
{
  "totalPages": 5,
  "nowPage": 0,
  "totalElements": 48,
  "books": [
    {
      "mybookId": 1,
      "readingStatus": "TODO",
      "createdDate": "2024-01-01T00:00:00",
      "startedDate": null,
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

---

## GET /mybooks/store

내 서점(TODO 상태) 목록을 조회한다.

| 항목 | 값 |
|------|-----|
| Service | `MyBookService.getStoreBooks()` |
| Feature Doc | [mybook-store.md](../features/mybook-store.md) |

### Request
| 위치 | 파라미터 | 타입 | 필수 | 설명 |
|------|---------|------|------|------|
| Query | `keyword` | String | X | 검색 키워드 |
| Query | `page` | Int | X | 페이지 번호 (0-indexed) |
| Query | `size` | Int | X | 페이지 크기 |

### Response (Spring Page 형식)
```json
{
  "content": [
    {
      "mybookId": 1,
      "createdDate": "2024-01-01T00:00:00",
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

---

## GET /mybooks/{mybookId}

나의 책 상세 정보를 조회한다.

| 항목 | 값 |
|------|-----|
| Service | `MyBookService.getMyBookDetail()` |
| Feature Doc | [mybook-detail.md](../features/mybook-detail.md) |

### Request
| 위치 | 파라미터 | 타입 | 필수 | 설명 |
|------|---------|------|------|------|
| Path | `mybookId` | Int | O | 나의 책 ID |

### Response
- 책 상세 정보 (`MyBookDetailResponse`)

---

## DELETE /mybooks/{mybookId}

나의 책을 삭제한다.

| 항목 | 값 |
|------|-----|
| Service | `MyBookService.deleteMyBook()` |
| Feature Doc | [mybook-delete.md](../features/mybook-delete.md) |

### Request
| 위치 | 파라미터 | 타입 | 필수 | 설명 |
|------|---------|------|------|------|
| Path | `mybookId` | Int | O | 나의 책 ID |

### Response
- `200 OK` (빈 응답)

---

## PATCH /mybooks/{mybookId}

나의 책 정보를 수정한다.

| 항목 | 값 |
|------|-----|
| Service | `MyBookService.updateMyBook()` |
| Feature Doc | [mybook-update.md](../features/mybook-update.md) |

### Request
| 위치 | 파라미터 | 타입 | 필수 | 설명 |
|------|---------|------|------|------|
| Path | `mybookId` | Int | O | 나의 책 ID |
| Body | (MyBookUpdateRequest) | Object | O | 수정할 정보 |

### Response
| 필드 | 타입 | 설명 |
|------|------|------|
| `mybookId` | Int | 수정된 책 ID |

---

## PATCH /mybooks/{mybookId}/reading-status

나의 책의 독서 상태를 변경한다.

| 항목 | 값 |
|------|-----|
| Service | `MyBookService.updateReadingStatus()` |
| Feature Doc | [mybook-update-reading-status.md](../features/mybook-update-reading-status.md) |

### Request
| 위치 | 파라미터 | 타입 | 필수 | 설명 |
|------|---------|------|------|------|
| Path | `mybookId` | Int | O | 나의 책 ID |
| Body | `readingStatus` | String | O | 변경할 상태 (TODO, READING, READ 등) |

### Response
| 필드 | 타입 | 설명 |
|------|------|------|
| `mybookId` | Int | 변경된 책 ID |
