# UI 개선 체크리스트 설계 문서

## 개요
작업목록에 정의된 7개 UI 개선 항목 + 추가 요구사항에 대한 설계 문서

---

## 1. 내 책 검색 타입별 태그 세분화

**파일**: `ui/.../screen/MyBookSearchScreen.kt`, `ui/.../theme/Color.kt`

### 현재
- `[내 서점]` (TagStore, #4CAF50) / `[히스토리]` (TagHistory, #2196F3) 2가지 텍스트 태그

### 변경
ReadingStatus 기반 3가지 컬러 박스 태그:

| API readingStatus 값 | 태그 텍스트 | 배경색 | 텍스트색 |
|---|---|---|---|
| `"TODO"` | 읽고 싶은 책 | #010196 (Primary) | White |
| `"COMPLETED"` 외 나머지 | 읽는 중 | #333333 (TextPrimary) | White |
| `"COMPLETED"` | 완독 | #333333 (TextPrimary) | White |

> 참고: "읽는 중"과 "완독"은 동일 배경색(#333333)이며, 태그 텍스트로만 구분 (의도된 디자인)

### 구현 상세
- `MyBookSearchResultItem`에서 `isTodo` 분기를 `readingStatus` 3분기로 변경
  ```kotlin
  val (tag, tagColor) = when (item.readingStatus) {
      "TODO" -> "읽고 싶은 책" to Primary
      "COMPLETED" -> "완독" to TextPrimary
      else -> "읽는 중" to TextPrimary
  }
  ```
- 태그 UI: `Box(background=tagColor, padding=horizontal 8.dp, vertical 4.dp) { Text(tag, style=DungGeunMoTag, color=TextWhite) }`
- `Color.kt`의 기존 `TagStore`, `TagHistory`는 더 이상 사용하지 않음 (Primary, TextPrimary로 대체)
- 대괄호 `[]` 제거 → 박스 배경이 구분 역할

---

## 2. 검색 창 PixelShadowBox 그림자

**파일**: `ui/.../screen/MyBookSearchScreen.kt`, `ui/.../screen/SearchBookScreen.kt`

### 현재
- `Box + background(BackgroundWhite) + border(1.dp, BorderBlack)` (그림자 없음)

### 변경
- 검색 입력 Box를 `PixelShadowBox`로 래핑
- `shadowOffset = 2.dp` (프로젝트 표준)
- `backgroundColor = BackgroundWhite`
- 내부 Row(BasicTextField + 아이콘)는 그대로 유지
- 양쪽 화면(MyBookSearchScreen, SearchBookScreen) 모두 적용

---

## 3. 히스토리 리스트/그리드 버튼 SVG 교체

**파일**: `ui/.../screen/HistoryScreen.kt`, `ui/src/main/res/drawable/`

### 현재
- 2개의 Box(36dp) + border + 배경색 전환 + 기존 list_view.xml / dataset_view.xml 아이콘
- 구분선(1dp Box) 포함

### 변경
- **SVG 원본 위치**: `scripts/` 폴더에 4개 파일 존재
  - `list_button.svg`, `pressed_list_button.svg`
  - `grid_button.svg`, `pressed_grid_button.svg`
- SVG를 Android Vector Drawable(XML)로 변환하여 `res/drawable/`에 추가:
  - `ic_list_button.xml` (38x38, drop shadow, 비선택)
  - `ic_list_button_pressed.xml` (36x36, inner shadow, 선택)
  - `ic_grid_button.xml` (38x38, drop shadow, 비선택)
  - `ic_grid_button_pressed.xml` (36x36, inner shadow, 선택)
- 기존 border + Box 구조 제거
- 각 버튼을 독립적인 `Image(painterResource)` 으로 교체
- 선택 상태에 따라 normal/pressed 이미지 전환
- 두 버튼 사이 간격: `Spacer(4.dp)`

### SVG 특성
- 비선택(normal): 38x38dp, #D4D4D4 배경, drop shadow (우하단 1px)
- 선택(pressed): 36x36dp, #E4E4E4 배경, inner shadow (눌린 느낌)

### SVG → VectorDrawable 변환 가이드
- drop shadow / inner shadow는 VectorDrawable 표준에서 직접 지원하지 않음
- SVG의 filter 효과를 별도 path 레이어로 근사 구현 (그림자 색상 path + 메인 path)
- 또는 SVG를 PNG로 래스터화 후 `ImageBitmap`으로 사용하는 대안도 고려

---

## 4. 히스토리 화면 폰트 확인

**파일**: `ui/.../screen/HistoryScreen.kt`

### 확인 대상
- 테이블 헤더 영어 텍스트: "START", "FINISH", "BOOK NAME" (252-266행)
- 현재 `DungGeunMoBody.copy(letterSpacing = 3.2.sp)` → DungGeunMo(둥근모) 적용됨

### 결론
- 정상 확인됨, 변경 불필요
- 구현 시 다른 영어 텍스트도 둥근모인지 재확인

---

## 5. 팝업 가운데 변경 (ModalBottomSheet -> Dialog)

**대상 파일**:
- `ui/.../component/CalendarBottomSheet.kt`
- `ui/.../component/BookRegisterBottomSheet.kt` (내부 이미 레트로 스타일)
- `ui/.../component/BookEditBottomSheet.kt` (내부 이미 레트로 스타일)
- `ui/.../component/ReadingStartBottomSheet.kt` (내부 이미 레트로 스타일)

### 현재
- `ModalBottomSheet` (하단에서 올라오는 형태)

### 변경
- `ModalBottomSheet` → `Dialog(onDismissRequest = onDismiss)` 로 컨테이너 변경
- 내부 Content 컴포저블은 그대로 유지
- `ExperimentalMaterial3Api` 관련 import 정리
- `rememberModalBottomSheetState()` 제거
- `containerColor`, `dragHandle`, `shape` 등 BottomSheet 전용 파라미터 제거

### BookEditBottomSheet 스크롤 처리
- BookEditBottomSheet는 콘텐츠가 길 수 있음 (커스텀 책: 탭 + 날짜 + 이유 + 제목/작가/출판사/ISBN/페이지수 등)
- Dialog 전환 시 `Modifier.verticalScroll(rememberScrollState())` 추가
- Dialog 속성: `usePlatformDefaultWidth = false` + `Modifier.fillMaxWidth().padding(horizontal = 16.dp)` 로 적절한 너비 확보

### Dialog 중첩 케이스
- `ReadingStartBottomSheet` 내부 → `CalendarBottomSheet` (Dialog 위에 Dialog, 정상 동작)
- `BookEditBottomSheet` 내부 → `CalendarBottomSheet` x3 (시작일, 종료일, 출간일 - 동일하게 정상 동작)

---

## 6. 달력 팝업 UI 레트로 스타일 적용

### 6-1. CalendarDialog.kt + CalendarBottomSheet.kt 통일 레트로화

**현재**: 둘 다 `Card(RoundedCornerShape(12.dp))` + 플랫 NO/YES 버튼 (비레트로 스타일)

**변경 (CalendarDialog.kt)**:
- `Card` → `PixelShadowBox` (backgroundColor=BackgroundWhite, shadowOffset=3.dp)
- 기존 "X" 텍스트 닫기 → 레트로 타이틀바로 교체 (회색 바 + X 닫기 버튼, BookInfoScreen 삭제 다이얼로그 패턴)
- NO/YES 버튼: `Box + background` → `PixelShadowButton(backgroundColor=BackgroundGray)` 교체
- 버튼 간격: `Spacer(50.dp)` (다른 팝업과 통일)
- Preview 함수도 PixelShadowBox 기반으로 업데이트

**변경 (CalendarBottomSheet.kt)** → Dialog 전환 후 동일 레트로 스타일 적용:
- `Card` → `PixelShadowBox` (CalendarDialog와 동일)
- 레트로 타이틀바 추가
- NO/YES 버튼 → `PixelShadowButton` 교체

> CalendarDialog와 CalendarBottomSheet는 거의 동일한 구조이므로, 추후 하나로 통합하는 것도 고려 가능. 현 단계에서는 각각 레트로화만 진행.

### 6-2. CalendarPicker.kt - 날짜 선택 레트로화

**현재**: `CircleShape + Primary 배경` (파란 원)
**변경**:
- `CircleShape` 제거, `.clip(CircleShape)` 제거
- 선택된 날짜: `Box(background=Primary, border=1.dp BorderBlack)` 사각형
- 텍스트 색상: 선택 시 `TextWhite`, 미선택 시 `TextPrimary` (기존 로직 유지)

---

## 7. 빠진 팝업 UI / SnackBar 통일

### SnackBar 통일
**대상**:
- `ui/.../screen/LoginScreen.kt`: 기본 `SnackbarHost` → `CustomSnackbarHost` 변경
- `ui/.../screen/SignupScreen.kt`: 기본 `SnackbarHost` → `CustomSnackbarHost` 변경

**변경**: `SnackbarHost(snackbarHostState)` → `CustomSnackbarHost(snackbarHostState)` + import 추가

---

## 8. ReadingStartBottomSheet 책 제목 표시 + 스낵바

### 8-1. 책 제목 표시

**파일**: `ui/.../component/ReadingStartBottomSheet.kt`, `ui/.../screen/MainScreen.kt`, `ui/.../screen/HomeScreen.kt`

**UI 구조 변경**:
```
"책 시작하기" (DungGeunMoPopupTitle)
Spacer(12.dp)
"{책 제목}" (WantedSansBody, 16sp, TextPrimary)
Spacer(24.dp)
START: ...  (기존 UI)
```

**데이터 흐름 변경**:
1. `HomeBookItem`: `onStartReading: () -> Unit` → 변경 없음 (title은 상위에서 관리)
2. `HomeScreen`: `onStartReading: (Int) -> Unit` → `onStartReading: (Int, String) -> Unit`
   - 호출부: `onStartReading(book.mybookId, book.title)`
3. `MainScreen`: `readingStartBookTitle` 상태 추가
   - `onStartReading = { mybookId, title -> readingStartMybookId.intValue = mybookId; readingStartBookTitle.value = title; showReadingStartSheet.value = true }`
4. `ReadingStartBottomSheet`: `bookTitle: String` 파라미터 추가
   - `ReadingStartBottomSheetContent`에도 전달

### 8-2. 독서 시작 스낵바

**메시지**: "시작한 책은 히스토리에서 볼 수 있어요."
**방식**: `MainViewModel.startReading()` 내부의 기존 메시지 `"독서를 시작했어요"` 를 교체
**위치**: `presentation/.../viewmodel/MainViewModel.kt` 128행

```kotlin
// 변경 전
SnackbarManager.show("독서를 시작했어요")

// 변경 후
SnackbarManager.show("시작한 책은 히스토리에서 볼 수 있어요.")
```

> MainScreen.kt의 onConfirm에서는 SnackbarManager를 호출하지 않음 (ViewModel에서 처리)

---

## 영향 범위 요약

| 파일 | 변경 내용 |
|------|----------|
| `Color.kt` | TagStore, TagHistory 미사용 처리 (또는 제거) |
| `MyBookSearchScreen.kt` | 태그 3분기 + 컬러박스 + 검색창 PixelShadowBox |
| `SearchBookScreen.kt` | 검색창 PixelShadowBox |
| `HistoryScreen.kt` | 리스트/그리드 버튼 SVG 교체 + 간격 4.dp |
| `CalendarDialog.kt` | PixelShadowBox + 레트로 타이틀바 + PixelShadowButton + Preview 업데이트 |
| `CalendarPicker.kt` | 날짜 선택 사각형 박스화 |
| `CalendarBottomSheet.kt` | ModalBottomSheet → Dialog + 레트로 스타일 적용 |
| `BookRegisterBottomSheet.kt` | ModalBottomSheet → Dialog |
| `BookEditBottomSheet.kt` | ModalBottomSheet → Dialog + verticalScroll 추가 |
| `ReadingStartBottomSheet.kt` | ModalBottomSheet → Dialog + bookTitle 파라미터 |
| `MainScreen.kt` | readingStartBookTitle 상태 + onStartReading 시그니처 변경 |
| `MainViewModel.kt` | 스낵바 메시지 교체 ("독서를 시작했어요" → "시작한 책은 히스토리에서 볼 수 있어요.") |
| `HomeScreen.kt` | onStartReading 시그니처 (Int) → (Int, String) |
| `LoginScreen.kt` | CustomSnackbarHost 적용 |
| `SignupScreen.kt` | CustomSnackbarHost 적용 |
| `res/drawable/` | SVG → XML 벡터 드로어블 4개 추가 |
