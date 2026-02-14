# Member API

회원 정보 관련 API 엔드포인트. `MemberService`에서 정의.

## Base URL
자체 백엔드 서버 (`BASE_URL` in `key.properties`)

---

## GET /members/me

현재 로그인한 회원의 정보를 조회한다.

| 항목 | 값 |
|------|-----|
| 인증 | Bearer JWT (`@AuthRetrofit`) |
| Service | `MemberService.getMyInfo()` |
| Feature Doc | [member-get-info.md](../features/member-get-info.md) |

### Request
- 파라미터 없음

### Response
| 필드 | 타입 | 설명 |
|------|------|------|
| `nickname` | String | 회원 닉네임 |

### 호출 시점
- `MainViewModel.init` → `validateToken()`에서 로그인 상태 확인 시 호출

---

## PATCH /members/me

회원 닉네임을 변경한다.

| 항목 | 값 |
|------|-----|
| 인증 | Bearer JWT (`@AuthRetrofit`) |
| Service | `MemberService.updateNickname()` |
| Feature Doc | [member-update-nickname.md](../features/member-update-nickname.md) |

### Request
| 위치 | 파라미터 | 타입 | 필수 | 설명 |
|------|---------|------|------|------|
| Body | `nickname` | String | O | 새 닉네임 |

### Response
| 필드 | 타입 | 설명 |
|------|------|------|
| `nickname` | String | 변경된 닉네임 |

---

## DELETE /members/me

회원 탈퇴.

| 항목 | 값 |
|------|-----|
| 인증 | Bearer JWT (`@AuthRetrofit`) |
| Service | `MemberService.withdraw()` |
| Feature Doc | [member-withdraw.md](../features/member-withdraw.md) |

### Request
- 파라미터 없음

### Response
- `200 OK` (빈 응답)

---

## GET /members/check

닉네임 중복 확인.

| 항목 | 값 |
|------|-----|
| 인증 | 불필요 (`@DefaultRetrofit`) |
| Service | `MemberService.checkNickname()` |
| Feature Doc | [member-check-nickname.md](../features/member-check-nickname.md) |

### Request
| 위치 | 파라미터 | 타입 | 필수 | 설명 |
|------|---------|------|------|------|
| Query | `nickname` | String | O | 확인할 닉네임 |

### Response
| 필드 | 타입 | 설명 |
|------|------|------|
| `available` | Boolean | 사용 가능 여부 |
