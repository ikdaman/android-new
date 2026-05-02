# 모아북 디자인 리뷰 리포트

> 작성일: 2026-04-20
> 기준 버전: 1.0.17
> 리뷰 대상: 13개 화면 + 상태 변이
> 산출물: 기획자·개발자 공용

---

## 0. Executive Summary

**모아북의 가장 큰 문제는 "내 서점 vs 히스토리"의 정보 구조 신호가 부서져 있고, 레트로 컨셉을 지탱해야 할 타이포/컬러/네이밍 규칙이 화면마다 다르게 적용된다는 점입니다.** 사용자가 "독서 시작" 이후 책이 사라졌다고 느끼는 현상은 개별 버그가 아니라 이 문제의 표면 증상입니다.

### 핵심 발견 (영향도 순)

| # | 이슈 | 영향도 | 컨셉 | 공수 |
|---|------|:---:|:---:|:---:|
| 1 | **서점↔히스토리 전환 모델 붕괴** — 독서 시작 시 책이 서점에서 사라지고 "히스토리"에 편입. "히스토리"=다 읽음 연상 | 🔴 High | 🟡 Med | Quick Win |
| 2 | **상태 네이밍 4종 혼재** — "읽고 싶은 책/읽는 중/완독" vs "읽다만/읽는 중/완독" vs 날짜 기반 암시 | 🔴 High | 🔴 High | Quick Win |
| 3 | **날짜 포맷 3종 공존** — `260330` (히스토리) / `2026 - 03 - 30` (상세) / `2026.03.30` (홈) | 🔴 High | 🟡 Med | Quick Win |
| 4 | **다이얼로그 버튼 언어 혼재** — 삭제는 `NO/YES`, 탈퇴·수정은 `취소/저장` | 🟡 Med | 🔴 High | Quick Win |
| 5 | **히스토리 그리드 유령 셀** — 4권인데 12칸 기본 표시 → "뭔가 잘못됐나?" 혼란 | 🟡 Med | 🟠 Low | Medium |
| 6 | **HomeScreen `NO.5` 위치 기반 번호** — 정렬 바꿀 때마다 같은 책의 번호가 바뀜. 안정 식별자 아님 | 🟡 Med | 🔴 High | Quick Win |
| 7 | **영문 헤더/버튼 남용** — `START/FINISH/BOOK NAME`, `[+] ADD BOOK`, `SAVE/START/FINISH` — 전 연령대 타겟과 충돌 | 🟡 Med | 🔴 High | Quick Win |
| 8 | **책 검색·탐색 발견성 낮음** — 바코드 스캔은 검색창 안 작은 아이콘, 수동 입력은 에러 후에만 노출 | 🟡 Med | 🟡 Med | Medium |

### 스코어카드

| 축 | 점수 | 요약 |
|---|:---:|---|
| UX / IA (critique) | **C+** | IA 고정 전제를 감안해도 상태 전환 신호가 심하게 부족 |
| Typography (typeset) | **B−** | DungGeunMo와 Wanted Sans 혼용 규칙이 모호. 일관성 개선 여지 큼 |
| Layout (layout) | **B** | 픽셀 그림자 박스 시스템은 일관된 편. 스크롤·정렬·간격 세부에 리듬 부재 |
| Color (colorize) | **B−** | 파란 Primary 단일 악센트. 상태/의미 색이 없어 정보 밀도 낮음 |
| Polish | **C+** | 언어 혼재·빈 셀·Dialog 버튼 비대칭 등 마감 디테일 다수 누락 |
| Audit (접근성) | **C** | 최소 터치 영역 미달, `contentDescription` 일부 `null`, 색 대비 검증 필요 |

---

## 1. 리뷰 대상 및 캡처 메타데이터

| # | 화면 | 캡처 파일 | 소스 파일 | 비고 |
|---|------|----------|-----------|------|
| 01 | SplashScreen | (코드) | `SplashScreen.kt` | 실제 UI 없음 — `LaunchedEffect` 즉시 navigate |
| 02 | LoginScreen | `02_login.png` | `LoginScreen.kt` | 로그아웃 후 캡처 |
| 03 | SignupScreen | (코드) | `SignupScreen.kt` | 소셜 로그인 후 신규 계정일 때만 노출. 독립 진입 없음 |
| 10 | HomeScreen | `10_home_top.png`~`13`, `14_home_sort_dropdown.png` | `HomeScreen.kt` | 서점(=저장) 목록 |
| 20 | BookInfoScreen | `20_book_detail.png`, `21_book_detail_scroll.png` | `BookInfoScreen.kt` | 상세 |
| 30 | AddBookScreen (빈) | `30_add_book.png` | `AddBookScreen.kt` | 책 추가 탭 진입 직후 (검색창만) |
| 40 | HistoryScreen (LIST) | `40_history.png` | `HistoryScreen.kt` | 리스트 뷰 |
| 41 | HistoryScreen (GRID) | `41_history_grid.png` | 동 | 썸네일 뷰, 유령 셀 확인 |
| 50 | SettingScreen | `50_settings.png` | `SettingScreen.kt` | 기본 |
| 51 | SettingScreen 공지 disabled | `51_settings_notice.png` | 동 | 탭 무반응 |
| 52 | 회원탈퇴 다이얼로그 | `52_settings_withdraw_dialog.png` | 동 | `NO/YES` |
| 53 | 닉네임 편집 | `53_settings_nickname_edit.png` | 동 | `취소/저장` |
| 60 | 독서 시작 전 | `60_before_start.png` | `MainScreen.kt` → `ReadingStartBottomSheet` | |
| 61 | 독서 시작 후 | `61_after_start.png` | 동 | 홈에서 책 사라짐 |
| 70 | BarcodeScreen | `70_barcode_scan.png` | `BarcodeScreen.kt` | 카메라 프리뷰 + 가이드 |
| 71 | 바코드 결과 | `71_barcode_result.png` | `AddBookScreen.kt` | 프로젝트 헤일메리 |
| 72 | 바코드 결과 스크롤 | `72_barcode_result_scroll.png` | 동 | "알라딘에서 더보기" |
| — | SearchBookScreen | (코드) | `SearchBookScreen.kt` | — |
| — | MyBookSearchScreen | (코드) | `MyBookSearchScreen.kt` | — |
| — | ManualBookInputScreen | (코드) | `ManualBookInputScreen.kt` | — |

**제외 (계획 기준)**: TestScreen, 빈 상태, 위젯, 친구, 다크모드, 태블릿.

---

## 2. 크로스커팅 이슈 (여러 화면에 반복)

화면별 상세보다 먼저 다룹니다. 이 이슈를 해결해야 전체 컨셉 정합성이 올라갑니다.

### CC-1. 상태 네이밍 4중 혼재 🔴

같은 책의 "읽고 있는 상태"가 화면마다 다르게 표현됩니다.

| 위치 | 표현 | 소스 |
|------|------|------|
| BookInfo 태그 | `읽고 싶은 책 / 읽는 중 / 완독` | `BookInfoScreen.kt:230-235` |
| MyBookSearch 태그 | `읽다만 / 읽는 중 / 완독` | `MyBookSearchScreen.kt:167-171` |
| History 빈 상태 문구 | `읽고 있는 책을 추가해주세요.` | `HistoryScreen.kt:286` |
| History 리스트 | (라벨 없음, `START`/`FINISH` 날짜만) | 동 |

**문제**:
- `"읽다만"` = "읽다가 만"처럼 읽히지만 실제로는 `TODO` (=아직 읽기 시작 안 함). 의미 역전.
- 한 상태에 3개 이름이 공존. 사용자 메모리에 부담.

**개선안** (네이밍 통일, IA 유지):
```
TODO        → "읽고 싶은 책"  (🟦 감정: 기대)
INPROGRESS  → "읽는 중"        (🟡 감정: 현재)
DONE        → "완독"           (🟩 감정: 성취)
```
전 화면에서 이 3개만 사용. `"읽다만"` 제거.

---

### CC-2. 날짜 포맷 3종 공존 🔴

| 화면 | 포맷 | 예시 |
|------|------|------|
| HomeScreen 저장일 | `YYYY.MM.DD` | `2026.03.30` |
| HistoryScreen 리스트 | `YYMMDD` (6자리, 연 2자리, 구분자 X) | `260330` |
| BookInfoScreen 독서 이력 | `YYYY - MM - DD` (공백+하이픈) | `2026 - 03 - 30` |
| BookInfoScreen 출간일 | 동 | `2021 - 05 - 04` |

**근거**: `HistoryScreen.kt:316-317`은 `take(10).replace("-","").drop(2)`, `BookInfoScreen.kt:313`은 `replace("-"," - ")`, `HomeScreen.kt:171`은 `replace("-",".")`.

**문제**:
- 동일한 책의 시작일이 화면 이동마다 다른 모양으로 보임 → 다른 데이터처럼 보임
- `260330` 6자리는 가독성 최하. 첫 2자리 연도 정보 손실

**개선안** (한 가지로 통일):
- 데이터 밀도가 높은 히스토리 리스트: `YY.MM.DD` (`26.03.30`) — 점 구분자로 스캔 가능
- 상세·홈: 동일 `YY.MM.DD` 또는 `YYYY.MM.DD` 둘 중 하나로 통일
- `-` 문자에 공백을 넣는 스타일(`2026 - 03 - 30`)은 전면 제거

---

### CC-3. Dialog 버튼 언어·순서 혼재 🟡

| 다이얼로그 | 부정 | 긍정 | 소스 |
|-----------|------|------|------|
| 책 삭제 (Home/BookInfo) | `NO` | `YES` (왼쪽 NO → 오른쪽 YES) | `MainScreen.kt:142/155`, `BookInfoScreen.kt:138/151` |
| 회원 탈퇴 (Setting) | `NO` | `YES` | `52_settings_withdraw_dialog.png` |
| 닉네임 편집 | `취소` | `저장` | `53_settings_nickname_edit.png` |
| 책 편집 시트 | `취소` | `저장` (추정) | `BookEditBottomSheet` |

**문제**:
- 삭제·탈퇴는 영문, 수정은 한글. 사용자가 매번 읽는 방법을 전환해야 함
- 같은 픽셀 컨셉이라면 `NO/YES`는 레트로 어울리지만 `취소/저장`은 현대 앱 톤 → 혼재는 둘 다 약화시킴

**개선안**:
- 컨셉 유지 기조: **전 화면 `NO/YES`** 또는 **전 화면 `취소/확인·저장`** 중 택일
- 파괴적 액션(삭제·탈퇴)은 긍정 버튼을 **시각적으로 약하게** 하거나 **빨간 경고 색**으로 분리
- `X` 아이콘은 현재 **텍스트 대문자 X**(`MainScreen.kt:116`) — 실제 아이콘 또는 픽셀 글리프로 교체 권장

---

### CC-4. 영문 UPPER 헤더·버튼 남용 🟡 (컨셉 충돌)

| 위치 | 라벨 |
|------|------|
| HomeScreen | `[+] ADD BOOK` |
| HomeScreen 카드 | `NO.5 (2026.03.30)` |
| HistoryScreen 컬럼 헤더 | `START`, `FINISH`, `BOOK NAME` (letterSpacing 3.2sp) |
| BookInfoScreen 이력 | `SAVE`, `START`, `FINISH` |

**충돌**: 계획서상 타겟은 **전 연령대**. 중장년층은 영문+letterSpacing 대문자에 인지 부하 급증.
**레트로 컨셉 해석**: 80~90년대 영문 컴퓨터 UI 느낌은 좋음. 하지만 **한국어 타겟에게 80~90년대 한국 컴퓨터 경험**은 한글 도스 프롬프트·하이텔/천리안 등 — 한글+픽셀도 같은 컨셉.

**개선안** (중립 절충):
- 1차 라벨은 **한글**, 보조 스타일로 픽셀 폰트의 특징을 살리기
- 예: `START → 시작`, `FINISH → 끝`, `BOOK NAME → 책 이름`, `[+] ADD BOOK → [+] 책 추가`
- 바꾸고 싶지 않은 곳(예: 카드 번호 `NO.5`)은 유지하되 **중복 사용하지 않기**

---

### CC-5. 시각 어포던스: 읽기 전용 박스 ↔ 입력 필드 구분 실패 🟡

`AddBookScreen` (바코드/검색 결과)과 `ManualBookInputScreen` (수동 입력)은 같은 **PixelShadowBox + 흰 배경** 스타일을 씁니다.
- `AddBookScreen.kt:231-238`: `Text`만 담는 **읽기 전용** 박스
- `ManualBookInputScreen.kt:222-243`: `BasicTextField` 담는 **편집 가능** 박스

**문제**: 71_barcode_result.png에서 모든 필드가 "수정 가능해 보인다"고 판단했던 1차 관찰의 원인. 사용자도 동일하게 혼동할 수 있음.

**개선안**:
- **편집 가능**: 그림자 박스 + 우측 연필 아이콘, 포커스 시 테두리 강조
- **읽기 전용**: 그림자 박스 **없이** 배경 다르게 (`BackgroundGray`), 라벨만 강조
- 또는 최소한, 편집 시에는 placeholder 대신 hint 색 다르게

---

### CC-6. 타이포 혼용 규칙 부재 🟡

| 용도 | 폰트 | 예 |
|------|------|---|
| 화면 타이틀, 버튼, 라벨, 태그 | **DungGeunMo** | `지금 떠오르는 책이 있나요...!`, `읽고 싶은 책` |
| 본문, 값, 책 설명, 저자 | **Wanted Sans** | 책 제목, 저자, 책 소개, 저장 이유 |
| 약관 | **Wanted Sans Body Small** | `가입시 이용약관...` |

**현재 규칙 추정**: "고정 라벨/타이틀 = 픽셀, 사용자 데이터/본문 = 고딕" — 합리적. 하지만 **BookInfo의 `SAVE/START/FINISH`는 라벨이지만 영문 대문자**, **약관만 유일하게 Wanted Sans Body Small 사용** 등 예외가 많음.

**개선안**:
- 스타일 시스템 문서로 명시: `DungGeunMoHomeTitle / DungGeunMoHeader / DungGeunMoSubtitle / DungGeunMoBody / DungGeunMoTag / WantedSansBody / WantedSansBodySmall` 각각의 "언제 쓰는가"를 1줄 주석으로 정의
- 폰트 크기 단계 축소 (현재 DungGeunMo 계열만 7종 이상) — 타이틀/헤더/서브타이틀/본문/태그 **5단계로 정리**

---

### CC-7. 접근성 공통 이슈 🟡

- **최소 터치 영역**: HomeScreen 설정 버튼 30dp, 정렬 드롭다운 화살표 14dp. WCAG 권고 48x48dp 미달.
- **`contentDescription = null`**: `HomeScreen.kt:144/206`, `SearchBookScreen.kt:228` 등. 픽셀 아이콘들 중 일부는 의미 전달 (카메라=바코드)에도 스크린리더 설명 없음.
- **색 대비**: `TextPrimary.copy(alpha = 0.5f)` 등 낮은 알파 텍스트 다수 — WCAG AA 4.5:1 검증 필요.
- **`Text("X", style = DungGeunMoBody)`** (`MainScreen.kt:116`): 닫기 아이콘이 문자 `X` — `contentDescription` 없음, 스크린리더 미지원.

---

## 3. 화면별 상세 (개선 카드)

각 카드는 `Before 스크린샷 → 문제 → 근거 → 개선안 → 태그` 순으로 정리. 영향도 순으로 내림차.

---

### 🔴 High | HOME-1. 서점↔히스토리 전환 모델 붕괴

**화면**: HomeScreen → ReadingStartBottomSheet → HistoryScreen
**Before**: `60_before_start.png` → `61_after_start.png` → `40_history.png`

**문제**:
사용자는 홈("내 서점")에서 "독서 시작"을 누르면 **그 책이 서점에서 사라지고** 하단 탭의 "히스토리"에 편입된다. 사용자의 실제 보고:
> "책이 꼭 사라진 것만 같음. 히스토리가 책을 다 읽은 게 아니라 읽는 중인데도 뭔가 다 읽은것처럼 보임."

**근거**:
- `shelfType = STORE` → `HISTORY` 전환 구조. `MainScreen.kt:207-246` Home은 `storeBooks`, `HistoryScreen`은 별도 유즈케이스
- "히스토리" 일반적 의미 = **지난 것**. 영단어 History도 동일. "진행 중"과 양립 불가
- `BookInfoScreen.kt:315-321` 히스토리 화면에서도 `finishedDate`가 없으면 "읽는 중" 표시 — 즉 구조적으로 "읽는 중"이 "히스토리"에 들어있음이 확인됨

**개선안** (IA 유지 전제, 네이밍·시각 신호만):
1. **탭 네이밍 변경**: `히스토리` → **`읽는 중`** (진행 중 + 완료 혼합이라면 `책 기록`도 가능. 단 "기록"은 딱딱함)
2. **홈에서 "독서 시작" 시 토스트**: `"읽기 시작! '읽는 중' 탭에서 볼 수 있어요"` — 첫 1회만 (`SharedPreferences` 플래그)
3. **전환 애니메이션**: 카드가 홈에서 "슈웅" 하고 탭 방향으로 날아가는 1회성 모션 (픽셀 컨셉: CRT 라인 스캔 같이)
4. **탭 아이콘 시각 신호**: 현재 "읽는 중" 책이 있으면 탭에 **작은 읽는 중 배지**(`N`권) 표시

**태그**: 🔴 High | 🟡 Med 컨셉 | Quick Win (1번만 해도 체감 큼)

---

### 🔴 High | HOME-2. `NO.5` 위치 기반 번호 — 정렬 바꾸면 같은 책의 번호 변함

**화면**: HomeScreen 카드
**Before**: `10_home_top.png` (`NO.5`), `14_home_sort_dropdown.png`

**문제**:
카드 좌상단의 `NO.5 (2026.03.30)` 중 "5"가 안정 식별자처럼 보이지만, 실제로는 **정렬 순서에 따라 매번 재계산**된다. 최신순일 때 최상위 카드는 `NO.N`, 오래된순으로 바꾸면 `NO.1`이 됨. 같은 책의 번호가 바뀜 → 식별자 의미 없음.

**근거**:
- `HomeScreen.kt:168`: `index = if (sortDescending) storeBooks.size - index else index + 1`
- 정렬 변경 세션 1차 분석 시 "같은 책 NO 변함" 직접 확인

**개선안** (목적에 맞게 분기):
- **의도가 "몇 번째 저장인지 표시"라면**: 서버에서 안정적인 `saveOrder` 필드를 받아 저장 시점 누적 번호 사용 (정렬과 무관)
- **의도가 "화면에서 순번 매기기"라면**: 그냥 제거 (저장 날짜만으로 충분)
- **의도가 "레트로 감성 꾸미기"라면**: `NO.` 표기는 유지하되 `#001`처럼 누적 고정 ID로 바꾸고 정렬과 무관하게 동일하게 유지

**태그**: 🔴 High | 🔴 High 컨셉(레트로 "NO" 표기 살릴 여지 많음) | Quick Win (서버 없으면 `mybookId` 기반으로 임시 표시 가능)

---

### 🔴 High | HOME-3. "[+] ADD BOOK" 발견성 낮고 컨셉 혼재

**화면**: HomeScreen 헤더
**Before**: `10_home_top.png` 우측 중단 박스

**문제**:
- 홈에서 책을 새로 추가하는 **유일한 경로**가 우측 작은 박스의 `[+] ADD BOOK`. 스크롤하면 사라짐.
- 영문 대문자 + 대괄호 `[+]` → 전 연령대에 친숙하지 않음.
- 하단 탭 `책 추가`와 라벨이 다름 → "둘이 같은 기능인가? 다른가?" 혼란 발생 가능.

**근거**:
- `HomeScreen.kt:227-238`, `MainScreen.kt:180-186` 하단 탭
- `30_add_book.png` 하단 탭: `내 서점 | 책 추가 | 히스토리`

**개선안**:
1. 홈 헤더 버튼 라벨을 **`[+] 책 추가`**로 변경 (하단 탭과 동일 용어)
2. 책이 적을 때는 더 큰 FAB 형태 버튼을 중앙에 1회 노출 (첫 사용자 온보딩)
3. 스크롤 시 하단 탭 `책 추가`에 시각적 강조 (상시 노출되니 기능적으론 대체 가능)

**태그**: 🔴 High | 🟡 Med | Quick Win

---

### 🔴 High | HISTORY-1. 그리드 뷰 유령 셀 (4권인데 12칸)

**화면**: HistoryScreen (GRID)
**Before**: `41_history_grid.png`

**문제**:
- 4권만 있는데 **12칸이 강제로 표시**됨. 나머지 8칸은 회색 `PixelShadowBox` 빈 박스.
- 일부 셀은 이미지 있고 일부는 비어 → 사용자: "이거 로딩 중인가? 아니면 내가 모르는 뭔가가 있나?"

**근거**:
```kotlin
// HistoryScreen.kt:189-193
val emptyCount = if (bookCount < 12) { 12 - bookCount }
                 else { (3 - bookCount % 3) % 3 }
```

**의도 추정**: 책장 "빈 칸"의 레트로 감성. 나쁘지 않음.

**문제 지점**:
1. 빈 칸이 "책을 채워야 하는 칸"인지 "UI 격자 장식"인지 불명확
2. 책 4권 < 12 조건 때문에 12칸 **고정** — 사용자가 책을 1권 더 추가하면 5/12, 11권 추가해야 12/12 채워짐. "목표 12권"이라는 엉뚱한 프레이밍 생성

**개선안** (감성 유지하면서 혼란 제거):
1. 빈 칸에 **`+ 책을 채워주세요`** 같은 **희미한 placeholder 텍스트** 1개 (맨 처음 빈 셀에만)
2. **첫 행만 고정 3칸**, 이후 행은 책 수에 맞게 `ceil(count/3) * 3`만 표시 — 3개 미만일 때만 단 하나의 빈 셀 플레이스홀더
3. 또는 빈 셀을 아예 없애고, 맨 아래에 **"더 읽어볼 책을 찾아볼까요?" CTA** 넣기 (탐색 유도)

**태그**: 🟡 Med | 🟠 Low 컨셉 | Medium

---

### 🔴 High | HISTORY-2. 컬럼 헤더 영문 대문자 + letterSpacing 3.2sp

**화면**: HistoryScreen (LIST)
**Before**: `40_history.png` 헤더 `START   FINISH   BOOK NAME`

**문제**:
- `START/FINISH/BOOK NAME` 영문 대문자 + letterSpacing 3.2sp → 픽셀 폰트 + 자간 늘림은 "터미널 프롬프트" 감성인데, **데이터 테이블 헤더**로 사용되어 **가독성 하락**
- 데이터 영역은 한글 책 제목인데 헤더만 영문 → 비대칭
- 날짜 `260330`은 `BOOK NAME` 대비 훨씬 짧아 컬럼 너비 1:1:2 비율이 과함

**개선안**:
1. 헤더 한글화: `시작 | 완독 | 책 이름`
2. letterSpacing 제거 또는 절반(1.5sp)으로 축소
3. 컬럼 비율 조정: 날짜 1:1:3 (책 이름 더 넓게) 또는 날짜 2열을 1열 통합 (`25.01.15 → 25.02.10`)

**태그**: 🔴 High | 🟡 Med | Quick Win

---

### 🟡 Med | BOOK-1. "SAVE START FINISH" 라벨 + 날짜 포맷 이상

**화면**: BookInfoScreen 독서 이력 섹션
**Before**: `20_book_detail.png` 중앙 `SAVE  2026 - 03 - 30 / START / FINISH`

**문제**:
- `SAVE`는 "저장한 날". `START`는 "읽기 시작한 날". `FINISH`는 "완독한 날". 셋 다 영문 대문자, letterSpacing 3.2sp (`BookInfoScreen.kt:452`)
- 날짜 포맷 `2026 - 03 - 30` (공백 + 하이픈) — 다른 화면과 다름 (`CC-2`)
- `FINISH` 값이 비었을 때 "읽는 중"이 표시되는데, **"FINISH 읽는 중"**이라는 조합이 어색 ("완독: 읽는 중"처럼 들림)

**개선안**:
1. 라벨: `저장 | 시작 | 완독` (한글)
2. 날짜: `26.03.30` 통일
3. "읽는 중"일 때 라벨을 **`상태 | 읽는 중`**으로 바꿔 FINISH 자리를 의미 있게 재사용 — 또는 FINISH 자리를 `-`로 두고 위쪽에 별도 상태 뱃지만 유지

**태그**: 🟡 Med | 🔴 High | Quick Win

---

### 🟡 Med | BOOK-2. "알라딘에서 더보기" 외부 브랜드 노출

**화면**: BookInfoScreen, AddBookScreen 하단
**Before**: `21_book_detail_scroll.png`, `72_barcode_result_scroll.png`

**문제**:
- 외부 API 출처인 "알라딘"을 버튼 라벨에 그대로 노출 → 사용자에게는 **알라딘 제휴 앱처럼 보일 위험**
- 픽셀 그림자 버튼 스타일은 있지만 로고·색상 식별 없음 (단순 텍스트)

**개선안**:
- `알라딘에서 더보기` → **`인터넷에서 더 알아보기`** 또는 **`책 정보 더보기`** (브랜드 중립)
- 유지하고 싶다면 `알라딘` 로고 이미지를 옆에 배치 + "도서 정보 제공: 알라딘" 미니 크레딧

**태그**: 🟡 Med | 🟡 Med | Quick Win

---

### 🟡 Med | SEARCH-1. 바코드 스캔 진입점 발견성

**화면**: SearchBookScreen, AddBookScreen
**Before**: `30_add_book.png` 검색창 안 카메라 아이콘

**문제**:
- 바코드 스캔 진입 = 검색창 오른쪽 **카메라 아이콘 하나**. 라벨 없음.
- 모아북의 **차별화 기능 중 하나**인데 시각적 비중 최소
- `SearchBookScreen.kt:158-167` 카메라 아이콘 `contentDescription = "barcode"` — 시각·청각 모두 약함

**개선안**:
1. 검색창 **하단 별도 버튼**: `📷 바코드로 책 찾기` (아이콘+한글 라벨)
2. 아이콘을 유지할 경우 크기 24→32dp, 주변 여백 증가
3. `contentDescription = "바코드 스캔"` 한글로

**태그**: 🟡 Med | 🟡 Med | Medium

---

### 🟡 Med | SEARCH-2. 수동 입력 경로는 에러 후에만 노출

**화면**: SearchBookScreen 에러 상태
**근거**: `SearchBookScreen.kt:200-207`

**문제**:
- 검색 결과가 없을 때만 `"책 직접 입력하기"` TextButton이 나타남
- 사용자가 처음부터 "내가 직접 입력하고 싶다"고 생각해도 진입 경로 없음
- 책 검색이 성공하면 이 버튼이 **사라지기 때문에** 검색 결과에서 "내가 원하는 책 없음 → 직접 입력"으로 가는 흐름이 끊김

**개선안**:
1. 검색창 하단에 **항상 보이는 보조 링크**: `직접 입력하기 >`
2. 에러 상태에서는 현재처럼 강조해서 노출하되, 성공 상태에서도 리스트 최하단에 유지

**태그**: 🟡 Med | 🟡 Med | Quick Win

---

### 🟡 Med | SETTING-1. 공지사항이 disabled인데 enabled처럼 보임

**화면**: SettingScreen
**Before**: `50_settings.png`, `51_settings_notice.png` (탭해도 변화 없음)

**문제**:
- `SettingScreen.kt:315`: `SettingMenuItem(text = "공지사항") { /* disabled */ }` — 빈 콜백
- 스타일은 다른 메뉴(`서비스 이용약관`, `개인정보 처리방침`)와 동일 → 시각적으로 **탭 가능한 것처럼 보임**
- 사용자: "안 되네? 버그?"

**개선안**:
1. **완전히 숨기기** (기능 미구현이면 메뉴에서 제거)
2. 또는 시각적 약화: 회색 텍스트 + `(준비 중)` 접미사 + 탭 시 스낵바 `"곧 공개됩니다"`

**태그**: 🟡 Med | 🟢 Low | Quick Win

---

### 🟠 Low | SETTING-2. 회원탈퇴 버튼 시각 위계

**화면**: SettingScreen
**Before**: `50_settings.png` 중앙 하단 `회원탈퇴` (회색 작은 텍스트)

**관찰**:
- 파괴적 액션인데 **시각적으로 매우 약함** (회색, 작은 텍스트). 이건 **의도된 패턴** — 실수 방지.
- 단 현재는 탭 영역도 텍스트 높이만큼 좁음 → 접근성은 떨어지고 *오히려 노약자가 더 실수로 누를* 가능성이 있음
- 로그아웃 버튼은 **파란색 풀폭 버튼**으로 강하게 표현 → 위계가 뒤집힘?

**개선안**:
1. 회원탈퇴는 **설정 최하단**, 회색 작은 텍스트 + 최소 터치 영역 48dp 유지 (현재 정책 유지하되 탭 영역 보강)
2. 로그아웃 버튼 색 강도 낮추기 (Primary 파랑 → 중립 회색 박스) — 주 액션이 아니므로
3. 탭 시 확인 다이얼로그 필수 (현재도 있음 `52_settings_withdraw_dialog.png`)

**태그**: 🟠 Low | 🟡 Med | Medium

---

### 🟠 Low | LOGIN-1. 로고 + 타이틀 중복, 카카오 순서

**화면**: LoginScreen
**Before**: `02_login.png`

**문제**:
- 픽셀 로고 이미지 + `"모아북"` 텍스트 둘 다 노출 → 브랜드 반복. 로고 자체에 "모아북"이 시각적으로 포함되지는 않으므로 논쟁 여지 있지만, 레트로 감성에서는 **로고만으로 충분**할 수 있음.
- 소셜 로그인 순서: **구글 → 네이버 → 카카오**. 한국 사용자 기본 선호도(카카오 >> 네이버 > 구글)와 역순.
- 약관 문구가 `Row`로 한 줄 강제: `가입시 이용약관 및 개인정보처리방침에 동의하게 됩니다.` — 좁은 폰에서 넘칠 위험 (`LoginScreen.kt:187-197`).

**개선안**:
1. 로고 이미지 + 텍스트 중 **하나만** 선택 (권장: 로고만 크게)
2. 소셜 순서: **카카오 → 네이버 → 구글**
3. 약관 문구를 `Column`으로 감싸거나 `FlowRow` 사용 — 줄바꿈 안전하게

**태그**: 🟠 Low | 🟡 Med | Quick Win

---

### 🟠 Low | SIGNUP-1. 닉네임 입력 화면 구성 빈약

**화면**: SignupScreen (코드 기반)
**근거**: `SignupScreen.kt`

**관찰**:
- 상단 "닉네임 입력" 타이틀 → 60dp spacer → "닉네임을 입력해주세요." 타이틀 → 60dp spacer → 입력 필드 하나 + 완료 버튼 (타이틀 바 우측)
- 닉네임 이외 **아무 정보 없음**. 예: "어떤 이름으로 보일까요?", "나중에 바꿀 수 있어요", "2~10자" 같은 가이드

**개선안**:
1. 입력 필드 하단에 **가이드 문구**: `예: 책사랑러버`, `2~10자`, `한글/영문/숫자 가능`
2. 닉네임 기본 제안 (`책 애호가 #1234`) 1-tap 채우기
3. "닉네임 입력"과 "닉네임을 입력해주세요." 타이틀 중복 → 하나만

**태그**: 🟠 Low | 🟠 Low | Medium

---

### 🟠 Low | BARCODE-1. 스캔 가이드 배치 + 권한 거부 처리 부재

**화면**: BarcodeScreen
**Before**: `70_barcode_scan.png`

**관찰**:
- 스캔 박스 상단에 가이드 텍스트 "책의 바코드 영역을 맞춰주세요." + 아래 화살표. 타이포 흰색 + 픽셀 폰트 → **컨셉 정합성 좋음**.
- 스캔 영역 좌표 고정 `scanLeft=100, scanTop=300, scanRight=900, scanBottom=600` (비율 기준) → 화면 상단부가 너무 넉넉하고 하단이 답답 (`BarcodeScreen.kt:195`).
- **권한 거부 시**: `isPermissionGranted==false` → 빈 화면 (UI 없음). 사용자가 다시 물어볼 방법 없음 (`BarcodeScreen.kt:162-171`).

**개선안**:
1. 스캔 박스 세로 중앙 정렬: `scanTop=400, scanBottom=700`
2. 권한 거부 상태 UI: `"카메라 권한이 필요해요"` + "설정에서 허용하기" 버튼 → `ACTION_APPLICATION_DETAILS_SETTINGS`
3. 가이드 텍스트 아래 스캔 박스 내부에 **샘플 바코드 플레이스홀더** 이미지 (한 번만, 성공 시 제거)

**태그**: 🟠 Low | 🟡 Med | Medium

---

### 🟠 Low | MYBOOK-SEARCH-1. 태그 네이밍·색 불일치

**화면**: MyBookSearchScreen
**근거**: `MyBookSearchScreen.kt:167-171`

**문제**:
- `TODO → "읽다만"` + **Primary 파랑** 배경
- `COMPLETED → "완독"` + **TextPrimary 검정** 배경
- `else → "읽는 중"` + **TextPrimary 검정** 배경
- "읽는 중"과 "완독" **같은 색** — 구분 의미 없음
- `"읽다만"`은 `CC-1` 이슈

**개선안**:
`CC-1` 해결과 함께:
- `읽고 싶은 책` = Primary(파랑), `읽는 중` = 주황/노랑, `완독` = 초록
- 또는 무채색 유지하되 테두리 패턴(dotted/solid/double)으로 구분 (레트로 컨셉 친화)

**태그**: 🟠 Low | 🟡 Med | Quick Win

---

## 4. 축별 평가 상세

### 4.1 /critique — UX & 정보구조 (C+)

**잘한 것**
- 레트로 픽셀 컨셉이 홈·상세 전반에서 인상적으로 전달됨
- `PixelShadowBox`·`PixelShadowButton` 컴포넌트로 시각 일관성 유지
- 삭제 같은 파괴적 액션에 확인 다이얼로그 필수

**개선 필요**
- **CC-1, CC-2, HOME-1, HISTORY-1, HOME-2** (위 참조). 특히 HOME-1은 사용자가 이미 인지한 문제.
- 탐색/진입 동선의 발견성 (SEARCH-1, SEARCH-2).

### 4.2 /typeset — 타이포그래피 (B−)

**잘한 것**
- DungGeunMo(픽셀) + Wanted Sans(고딕) 역할 분리 시도
- `DungGeunMoHomeTitle` 홈 타이틀 스케일 대비 효과적

**개선 필요**
- CC-6 (규칙 문서화), CC-4 (영문 대문자 남용)
- `DungGeunMoBody.copy(letterSpacing = 3.2.sp)` 같은 즉석 스타일 변형이 여러 곳에 흩어져 있음 (`HistoryScreen.kt:255`, `BookInfoScreen.kt:452`) — 테마로 올릴 것

### 4.3 /layout — 레이아웃 (B)

**잘한 것**
- PixelShadow 시스템으로 카드/박스 일관성
- 하단 탭 항상 `내 서점 | 책 추가 | 히스토리` 고정

**개선 필요**
- 간격 토큰 부재: `Spacer(Modifier.height(XXdp))`가 화면마다 6/8/12/16/20/24/30/40dp 뒤섞임
- HistoryScreen 리스트 컬럼 비율 (HISTORY-2)
- LoginScreen 약관 줄바꿈 위험 (LOGIN-1)

**권장 토큰**:
```
space-xs = 4dp
space-sm = 8dp
space-md = 16dp
space-lg = 24dp
space-xl = 40dp
```

### 4.4 /colorize — 컬러 (B−)

**관찰된 팔레트**
- `BackgroundDefault` (연한 그레이블루), `BackgroundGray`, `BackgroundWhite`
- `TextPrimary` (검정), `TextHint`, `TextWhite`
- `BorderBlack` (픽셀 테두리)
- `Primary` (파랑 #4A49FF 추정)

**개선 필요**
- **상태 색 부재**: 읽고 싶은 책/읽는 중/완독이 모두 동일 회색 카드 → 정보 밀도 낮음
- **단일 악센트**: Primary 파랑 하나로 저장 버튼, 탭 선택, 태그 배지, 링크 등 모두 처리. 위계 구분 어려움
- **실수 경고 색 없음**: 삭제 다이얼로그의 "YES"가 일반 회색 버튼 → 파괴적 액션 시각 신호 부족

**추천 확장 팔레트** (픽셀 컨셉 친화):
```
StatusWish    = #E8F0FF (파랑 tint)
StatusReading = #FFF3D6 (주황 tint)
StatusDone    = #D6FAE8 (초록 tint)
DangerAccent  = #E24646 (레트로 CRT 적색)
```
모두 0xFF 불투명 + 어둠 테두리로 픽셀 감성 유지.

### 4.5 /polish — 마감 디테일 (C+)

- CC-3 (다이얼로그 언어 혼재)
- CC-5 (어포던스)
- HISTORY-1 (유령 셀)
- SETTING-1 (disabled 공지)
- 날짜 포맷 (CC-2)

### 4.6 /audit — 접근성 (C)

- 터치 영역 미달 다수
- `contentDescription = null` 다수
- 색 대비 미검증
- 로그인 다이얼로그 `X` 문자 버튼
- (양호) 시스템 다크모드 자체 미지원이므로 해당 대비 이슈는 범위 밖

---

## 5. 우선순위 액션 리스트

### Quick Win (1일 이하, 높은 체감)

| # | 액션 | 담당 | 영향 |
|---|------|:---:|:---:|
| 1 | 상태 네이밍 3종 통일 (`읽고 싶은 책 / 읽는 중 / 완독`) — 전 화면 | 기획+개발 | 🔴 |
| 2 | 날짜 포맷 `YY.MM.DD` 1종 통일 | 개발 | 🔴 |
| 3 | 다이얼로그 버튼 언어 통일 (`취소/확인` 권장) + X 문자 교체 | 기획+개발 | 🟡 |
| 4 | HistoryScreen 컬럼 헤더 한글화 + letterSpacing 축소 | 개발 | 🟡 |
| 5 | HomeScreen `[+] ADD BOOK` → `[+] 책 추가` | 개발 | 🟡 |
| 6 | BookInfoScreen `SAVE/START/FINISH` → `저장/시작/완독` | 개발 | 🟡 |
| 7 | SettingScreen 공지사항 숨김 또는 `(준비 중)` | 기획+개발 | 🟡 |
| 8 | 로그인: 소셜 순서 카카오→네이버→구글, 약관 `FlowRow` | 개발 | 🟠 |
| 9 | SearchBookScreen `직접 입력하기` 상시 링크 | 개발 | 🟡 |
| 10 | HomeScreen `NO.N` 위치 기반 제거 또는 고정 ID | 기획+개발 | 🔴 |

### Medium (1~3일)

| # | 액션 | 담당 | 영향 |
|---|------|:---:|:---:|
| 11 | 탭 네이밍 `히스토리` → `읽는 중` + 독서 시작 토스트 + 탭 배지 | 기획+개발 | 🔴 |
| 12 | HistoryScreen 그리드 유령 셀 로직 개선 | 기획+개발 | 🟡 |
| 13 | AddBookScreen 읽기 전용 vs 입력 어포던스 분리 | 디자인+개발 | 🟡 |
| 14 | 바코드 진입점: 라벨 추가 + 크기 확대 | 디자인+개발 | 🟡 |
| 15 | 카메라 권한 거부 처리 UI | 개발 | 🟠 |
| 16 | 상태 색 팔레트 도입 (tint 3색 + 경고색) | 디자인+개발 | 🟡 |
| 17 | 타이포 + 간격 디자인 토큰 문서화 | 디자인 | 🟡 |

### Deep Work (4일+)

| # | 액션 | 담당 | 영향 |
|---|------|:---:|:---:|
| 18 | 접근성 전반 점검: 터치 영역, `contentDescription`, 대비 검증 | 디자인+개발 | 🟠 |
| 19 | 독서 시작 모션 (레트로 CRT 라인 스캔) | 디자인+개발 | 🟠 |

---

## 6. 위젯 작업과의 연결

계획서에서 이번 리뷰는 **위젯 개발 전 사전 정리** 목적이었습니다. 위젯 기획(`docs/WIDGET_PLAN.md`)의 "모아북 서재" 컨셉은 **본 앱의 `읽고 싶은 책` 목록 기반**입니다.

리뷰 결과 위젯 구현 전 선결해야 할 항목:
- **CC-1 상태 네이밍 통일** — 위젯에서도 `"저장 이유"`를 표시하려면 `readingStatus` 라벨이 안정적이어야 함
- **HOME-2 안정 식별자** — 위젯에서 "최근 저장한 책 3~4권"을 보여줄 때, 홈의 `NO.N` 번호와 위젯의 인덱스가 어긋나면 혼란
- **CC-4 영문 라벨 정책** — 위젯은 좁은 공간이라 영문 한 줄 선호이지만, 앱과 다른 정책이면 상시 노출 시 이질감

**권장 순서**: Quick Win 1~10 선반영 → 위젯 Phase 1~3 착수.

---

## 7. 부록

### 7.1 주요 소스 파일 ↔ 이슈 맵

```
ui/src/main/java/project/side/ui/screen/
├── HomeScreen.kt            ← HOME-1, HOME-2, HOME-3, CC-2, CC-4
├── HistoryScreen.kt         ← HISTORY-1, HISTORY-2, CC-1, CC-2, CC-4
├── BookInfoScreen.kt        ← BOOK-1, BOOK-2, CC-2, CC-3, CC-4
├── AddBookScreen.kt         ← CC-5, BOOK-2
├── ManualBookInputScreen.kt ← CC-5
├── SearchBookScreen.kt      ← SEARCH-1, SEARCH-2
├── MyBookSearchScreen.kt    ← MYBOOK-SEARCH-1, CC-1
├── SettingScreen.kt         ← SETTING-1, SETTING-2
├── LoginScreen.kt           ← LOGIN-1, CC-6
├── SignupScreen.kt          ← SIGNUP-1
├── BarcodeScreen.kt         ← BARCODE-1
├── MainScreen.kt            ← CC-3 (삭제 다이얼로그)
└── SplashScreen.kt          ← (UI 없음)
```

### 7.2 캡처 인벤토리

`images/review/` 전체 20개 이미지.
- 00~02: 스플래시/로그인
- 10~14: 홈 다양한 상태 + 정렬
- 20~21: 책 상세
- 30: 책 추가 빈 화면
- 40~41: 히스토리 (리스트/그리드)
- 50~53: 설정 (기본/공지/탈퇴/닉네임)
- 60~61: 독서 시작 전/후
- 70~72: 바코드 (스캔/결과/스크롤)

### 7.3 리뷰 컨텍스트 (요약)

- 타겟: 전 연령대 독서 애호가
- 컨셉: 80~90년대 컴퓨터 / 픽셀·도트
- 변경 불가: 앱 로고, DungGeunMo 폰트, 탭 구조/IA
- 변경 가능: 컬러 팔레트, 간격·레이아웃, 인터랙션·애니메이션, 네이밍·라벨, 일러스트·아이콘
- 범위 제외: 다크모드, 태블릿/폴더블, 빈 상태, 위젯(별도), 친구(미구현)

---

**이 리포트는 기획자·개발자 공용 실행 문서입니다. Quick Win 10건부터 착수 권장. 추가 질문·이견은 이 파일에 코멘트로 반영하거나 별도 이슈로 분리하세요.**
