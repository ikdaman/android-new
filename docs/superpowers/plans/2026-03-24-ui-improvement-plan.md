# UI 개선 체크리스트 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 작업목록에 정의된 7개 UI 개선 항목 + 추가 요구사항(독서 시작 팝업 책 제목, 스낵바)을 구현

**Architecture:** Jetpack Compose 기반 UI 변경. 기존 PixelShadowBox/Button 컴포넌트를 활용한 레트로 스타일 통일. ModalBottomSheet → Dialog 전환으로 팝업 가운데 표시.

**Tech Stack:** Kotlin, Jetpack Compose, Material3, Hilt, Coil

**Spec:** `docs/superpowers/specs/2026-03-24-ui-improvement-design.md`

---

## 파일 구조

### 수정 대상
| 파일 | 변경 요약 |
|------|----------|
| `ui/.../theme/Color.kt` | TagStore/TagHistory 제거 |
| `ui/.../screen/MyBookSearchScreen.kt` | 태그 3분기 + 검색창 PixelShadowBox |
| `ui/.../screen/SearchBookScreen.kt` | 검색창 PixelShadowBox |
| `ui/.../screen/HistoryScreen.kt` | 리스트/그리드 버튼 SVG 교체 |
| `ui/.../component/CalendarPicker.kt` | 날짜 선택 사각형 |
| `ui/.../component/CalendarDialog.kt` | 레트로 스타일 |
| `ui/.../component/CalendarBottomSheet.kt` | Dialog 전환 + 레트로 스타일 |
| `ui/.../component/BookRegisterBottomSheet.kt` | Dialog 전환 |
| `ui/.../component/BookEditBottomSheet.kt` | Dialog 전환 + scroll |
| `ui/.../component/ReadingStartBottomSheet.kt` | Dialog 전환 + bookTitle |
| `ui/.../screen/MainScreen.kt` | bookTitle 상태 + onStartReading 시그니처 |
| `ui/.../screen/HomeScreen.kt` | onStartReading 시그니처 변경 |
| `ui/.../screen/LoginScreen.kt` | CustomSnackbarHost |
| `ui/.../screen/SignupScreen.kt` | CustomSnackbarHost |
| `presentation/.../viewmodel/MainViewModel.kt` | 스낵바 메시지 교체 |

### 신규 생성
| 파일 | 용도 |
|------|------|
| `ui/src/main/res/drawable/ic_list_button.xml` | 리스트 버튼 (비선택) |
| `ui/src/main/res/drawable/ic_list_button_pressed.xml` | 리스트 버튼 (선택) |
| `ui/src/main/res/drawable/ic_grid_button.xml` | 그리드 버튼 (비선택) |
| `ui/src/main/res/drawable/ic_grid_button_pressed.xml` | 그리드 버튼 (선택) |

---

## Task 1: 내 책 검색 태그 세분화

**Files:**
- Modify: `ui/src/main/java/project/side/ui/screen/MyBookSearchScreen.kt:164-205`
- Modify: `ui/src/main/java/project/side/ui/theme/Color.kt:26-27`

- [ ] **Step 1: Color.kt에서 미사용 태그 색상 제거**

`Color.kt`에서 아래 2줄 제거:
```kotlin
// 제거
val TagStore = Color(0xFF4CAF50)
val TagHistory = Color(0xFF2196F3)
```

- [ ] **Step 2: MyBookSearchScreen.kt import 정리**

사용하지 않는 import 제거 및 추가:
```kotlin
// 제거
import project.side.ui.theme.TagHistory
import project.side.ui.theme.TagStore

// 추가 (이미 있으면 skip)
import project.side.ui.theme.Primary
import project.side.ui.theme.TextPrimary
import project.side.ui.theme.TextWhite
```

- [ ] **Step 3: MyBookSearchResultItem 태그 로직 변경**

`MyBookSearchScreen.kt`의 `MyBookSearchResultItem` (166-167행) 수정:

변경 전:
```kotlin
val isTodo = item.readingStatus == "TODO"
val tag = if (isTodo) "내 서점" else "히스토리"
```

변경 후:
```kotlin
val (tag, tagColor) = when (item.readingStatus) {
    "TODO" -> "읽고 싶은 책" to Primary
    "COMPLETED" -> "완독" to TextPrimary
    else -> "읽는 중" to TextPrimary
}
```

- [ ] **Step 4: 태그 UI를 컬러 박스로 변경**

`MyBookSearchResultItem` 내 태그 Text (201-205행) 수정:

변경 전:
```kotlin
Text(
    text = "[$tag]",
    style = DungGeunMoTag,
    color = if (isTodo) TagStore else TagHistory
)
```

변경 후:
```kotlin
Box(
    modifier = Modifier
        .background(tagColor)
        .padding(horizontal = 8.dp, vertical = 4.dp)
) {
    Text(
        text = tag,
        style = DungGeunMoTag,
        color = TextWhite
    )
}
```

- [ ] **Step 5: dateText 로직 조정**

`isTodo` 변수를 제거했으므로 dateText 로직 수정:

변경 전:
```kotlin
val dateText = if (isTodo) {
    item.createdDate.take(10)
} else {
```

변경 후:
```kotlin
val isTodo = item.readingStatus == "TODO"
val dateText = if (isTodo) {
    item.createdDate.take(10)
} else {
```

> `isTodo`는 dateText 계산에만 사용하도록 로컬 변수로 유지

- [ ] **Step 6: 빌드 확인**

Run: `./gradlew :ui:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 7: 커밋**

```bash
git add ui/src/main/java/project/side/ui/screen/MyBookSearchScreen.kt ui/src/main/java/project/side/ui/theme/Color.kt
git commit -m "feat: 내 책 검색 태그를 ReadingStatus 기반 3가지 컬러박스로 변경"
```

---

## Task 2: 검색 창 PixelShadowBox 그림자

**Files:**
- Modify: `ui/src/main/java/project/side/ui/screen/MyBookSearchScreen.kt:96-139`
- Modify: `ui/src/main/java/project/side/ui/screen/SearchBookScreen.kt:109-169`

- [ ] **Step 1: MyBookSearchScreen 검색창 PixelShadowBox 적용**

`MyBookSearchScreen.kt`에 import 추가:
```kotlin
import project.side.ui.component.PixelShadowBox
```

검색 입력 Box (96-103행) 수정:

변경 전:
```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
        .height(48.dp)
        .background(BackgroundWhite)
        .border(1.dp, BorderBlack),
    contentAlignment = Alignment.CenterStart
) {
```

변경 후:
```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp)
) {
    PixelShadowBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        backgroundColor = BackgroundWhite,
        shadowOffset = 2.dp,
        contentAlignment = Alignment.CenterStart
    ) {
```

> 닫는 괄호도 하나 추가 필요 (기존 Row 블록 닫힌 후 PixelShadowBox 닫기)

- [ ] **Step 2: SearchBookScreen 검색창 PixelShadowBox 적용**

`SearchBookScreen.kt`에 import 추가:
```kotlin
import project.side.ui.component.PixelShadowBox
```

검색 입력 Box (109-114행) 수정:

변경 전:
```kotlin
Box(
    Modifier
        .fillMaxWidth()
        .height(48.dp)
        .background(BackgroundWhite)
        .border(1.dp, BorderBlack),
    contentAlignment = Alignment.CenterStart
) {
```

변경 후:
```kotlin
PixelShadowBox(
    modifier = Modifier
        .fillMaxWidth()
        .height(48.dp),
    backgroundColor = BackgroundWhite,
    shadowOffset = 2.dp,
    contentAlignment = Alignment.CenterStart
) {
```

- [ ] **Step 3: 빌드 확인**

Run: `./gradlew :ui:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: 커밋**

```bash
git add ui/src/main/java/project/side/ui/screen/MyBookSearchScreen.kt ui/src/main/java/project/side/ui/screen/SearchBookScreen.kt
git commit -m "feat: 검색 창에 PixelShadowBox 그림자 적용"
```

---

## Task 3: 히스토리 리스트/그리드 버튼 SVG 교체

**Files:**
- Create: `ui/src/main/res/drawable/ic_list_button.xml`
- Create: `ui/src/main/res/drawable/ic_list_button_pressed.xml`
- Create: `ui/src/main/res/drawable/ic_grid_button.xml`
- Create: `ui/src/main/res/drawable/ic_grid_button_pressed.xml`
- Modify: `ui/src/main/java/project/side/ui/screen/HistoryScreen.kt:115-160`

- [ ] **Step 1: SVG를 VectorDrawable로 변환**

`scripts/` 폴더의 4개 SVG 파일을 Android Studio 또는 `vd-tool` 로 변환.

SVG의 `<filter>` (drop shadow / inner shadow)는 VectorDrawable에서 지원하지 않으므로, 그림자 효과를 별도 path 레이어로 근사 구현:
- **비선택(normal)**: 메인 rect(#D4D4D4) + 우하단 1px 그림자 path(#333333) + 좌상단 1px 하이라이트 path(#F6F6F6)
- **선택(pressed)**: 메인 rect(#E4E4E4) + 좌상단 1px 그림자 path(#333333, inner) + 우하단 1px 하이라이트 path(#F6F6F6, inner)

각 파일에 아이콘 path도 포함 (list_button: 3줄 목록, grid_button: 4칸 격자)

4개 XML 파일을 `ui/src/main/res/drawable/`에 생성

- [ ] **Step 2: HistoryScreen 버튼 교체**

`HistoryScreen.kt` 124-160행 (View toggle buttons Row) 수정:

변경 전:
```kotlin
Row(
    modifier = Modifier
        .border(1.dp, BorderBlack)
) {
    Box(
        modifier = Modifier.size(36.dp)
            .background(if (isListView) Color(0xFFE4E4E4) else Color(0xFFD4D4D4))
            ...
    ) { Image(modifier = Modifier.size(21.dp), painter = painterResource(R.drawable.list_view), ...) }
    Box(modifier = Modifier.width(1.dp).height(36.dp).background(BorderBlack))
    Box(
        modifier = Modifier.size(36.dp)
            .background(if (!isListView) Color(0xFFE4E4E4) else Color(0xFFD4D4D4))
            ...
    ) { Image(modifier = Modifier.size(20.dp), painter = painterResource(R.drawable.dataset_view), ...) }
}
```

변경 후:
```kotlin
Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
    Image(
        painter = painterResource(
            if (isListView) R.drawable.ic_list_button_pressed
            else R.drawable.ic_list_button
        ),
        contentDescription = "리스트 뷰",
        modifier = Modifier
            .size(if (isListView) 36.dp else 38.dp)
            .then(if (!isListView) Modifier.clickable { onViewTypeChanged() } else Modifier)
    )
    Image(
        painter = painterResource(
            if (!isListView) R.drawable.ic_grid_button_pressed
            else R.drawable.ic_grid_button
        ),
        contentDescription = "썸네일 뷰",
        modifier = Modifier
            .size(if (!isListView) 36.dp else 38.dp)
            .then(if (isListView) Modifier.clickable { onViewTypeChanged() } else Modifier)
    )
}
```

> `Arrangement.spacedBy` import 추가 (이미 있으면 skip)

- [ ] **Step 3: 미사용 import 정리**

`HistoryScreen.kt`에서 더 이상 사용하지 않는 import 제거:
```kotlin
import androidx.compose.foundation.border  // border 사용처 확인 후 (리스트 테이블에서 사용하면 유지)
```

- [ ] **Step 4: 빌드 확인**

Run: `./gradlew :ui:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: 커밋**

```bash
git add ui/src/main/res/drawable/ic_list_button.xml ui/src/main/res/drawable/ic_list_button_pressed.xml ui/src/main/res/drawable/ic_grid_button.xml ui/src/main/res/drawable/ic_grid_button_pressed.xml ui/src/main/java/project/side/ui/screen/HistoryScreen.kt
git commit -m "feat: 히스토리 리스트/그리드 버튼을 Figma SVG 아이콘으로 교체"
```

---

## Task 4: 달력 날짜 선택 레트로화 (CalendarPicker)

**Files:**
- Modify: `ui/src/main/java/project/side/ui/component/CalendarPicker.kt:119-136`

- [ ] **Step 1: CircleShape 관련 코드 제거 및 사각형 박스로 변경**

`CalendarPicker.kt` 119-136행 수정:

변경 전:
```kotlin
Box(
    modifier = Modifier
        .size(40.dp)
        .clip(CircleShape)
        .then(
            if (isSelected) Modifier.background(Primary, CircleShape)
            else Modifier
        )
        .clickable { onDateSelected(date) },
    contentAlignment = Alignment.Center
) {
```

변경 후:
```kotlin
Box(
    modifier = Modifier
        .size(40.dp)
        .then(
            if (isSelected) Modifier
                .background(Primary)
                .border(1.dp, BorderBlack)
            else Modifier
        )
        .clickable { onDateSelected(date) },
    contentAlignment = Alignment.Center
) {
```

- [ ] **Step 2: import 정리**

```kotlin
// 제거
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

// 추가
import androidx.compose.foundation.border
import project.side.ui.theme.BorderBlack
```

- [ ] **Step 3: 빌드 확인**

Run: `./gradlew :ui:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: 커밋**

```bash
git add ui/src/main/java/project/side/ui/component/CalendarPicker.kt
git commit -m "feat: 달력 날짜 선택을 레트로 사각형 박스 스타일로 변경"
```

---

## Task 5: CalendarDialog 레트로 스타일 적용

**Files:**
- Modify: `ui/src/main/java/project/side/ui/component/CalendarDialog.kt:35-110`

- [ ] **Step 1: import 업데이트**

```kotlin
// 제거
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.clickable

// 추가
import androidx.compose.foundation.border
import project.side.ui.component.PixelShadowBox
import project.side.ui.component.PixelShadowButton
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BackgroundGray
import project.side.ui.theme.BorderBlack
import project.side.ui.util.noEffectClick
```

- [ ] **Step 2: Dialog 내부를 PixelShadowBox 기반으로 교체**

`CalendarDialog.kt`의 Dialog 내부 (47-93행) 전체 교체:

```kotlin
Dialog(onDismissRequest = onDismiss) {
    PixelShadowBox(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = BackgroundWhite,
        shadowOffset = 3.dp,
        contentAlignment = Alignment.TopStart
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 레트로 타이틀바
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .background(BackgroundGray)
                        .border(1.dp, BorderBlack)
                )
                Box(
                    modifier = Modifier
                        .width(29.dp)
                        .height(28.dp)
                        .background(BackgroundGray)
                        .border(1.dp, BorderBlack)
                        .noEffectClick { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("X", style = DungGeunMoBody, color = TextPrimary)
                }
            }

            // 본문
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundDefault)
                    .padding(16.dp)
            ) {
                CalendarPicker(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    PixelShadowButton(
                        onClick = { onDismiss() },
                        backgroundColor = BackgroundGray
                    ) {
                        Text(
                            "NO", style = DungGeunMoBody, color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.width(50.dp))
                    PixelShadowButton(
                        onClick = { onDateConfirmed(selectedDate) },
                        backgroundColor = BackgroundGray
                    ) {
                        Text(
                            "YES", style = DungGeunMoBody, color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 3: Preview 함수 업데이트**

```kotlin
@Preview(showBackground = true)
@Composable
fun CalendarDialogPreview() {
    IkdamanTheme {
        PixelShadowBox(
            modifier = Modifier.padding(16.dp),
            backgroundColor = BackgroundWhite,
            shadowOffset = 3.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                CalendarPicker(selectedDate = LocalDate.now())
            }
        }
    }
}
```

- [ ] **Step 4: 빌드 확인**

Run: `./gradlew :ui:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: 커밋**

```bash
git add ui/src/main/java/project/side/ui/component/CalendarDialog.kt
git commit -m "feat: CalendarDialog 레트로 스타일 적용 (PixelShadowBox + 타이틀바)"
```

---

## Task 6: ModalBottomSheet → Dialog 전환 (4개 파일)

**Files:**
- Modify: `ui/src/main/java/project/side/ui/component/CalendarBottomSheet.kt`
- Modify: `ui/src/main/java/project/side/ui/component/BookRegisterBottomSheet.kt`
- Modify: `ui/src/main/java/project/side/ui/component/BookEditBottomSheet.kt`
- Modify: `ui/src/main/java/project/side/ui/component/ReadingStartBottomSheet.kt`

### 6-1. CalendarBottomSheet → Dialog + 레트로 스타일

- [ ] **Step 1: CalendarBottomSheet.kt 컨테이너 변경 + 레트로화**

import 변경:
```kotlin
// 제거
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.RoundedCornerShape

// 추가
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.width
import project.side.ui.component.PixelShadowBox
import project.side.ui.component.PixelShadowButton
import project.side.ui.theme.BackgroundDefault
import project.side.ui.theme.BorderBlack
```

파일 상단의 `@file:OptIn(ExperimentalMaterial3Api::class)` 제거.

`ModalBottomSheet(...)` 블록(50-100행)을 `Dialog` + `PixelShadowBox` 구조로 교체:

```kotlin
Dialog(onDismissRequest = onDismiss) {
    PixelShadowBox(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        backgroundColor = BackgroundWhite,
        shadowOffset = 3.dp,
        contentAlignment = Alignment.TopStart
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 레트로 타이틀바
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .background(BackgroundGray)
                        .border(1.dp, BorderBlack)
                )
                Box(
                    modifier = Modifier
                        .width(29.dp)
                        .height(28.dp)
                        .background(BackgroundGray)
                        .border(1.dp, BorderBlack)
                        .noEffectClick { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("X", style = DungGeunMoBody, color = TextPrimary)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundDefault)
                    .padding(16.dp)
            ) {
                CalendarPicker(
                    selectedDate = selectedDate,
                    onDateSelected = { date ->
                        if (allowDeselect && date == selectedDate) {
                            selectedDate = null
                        } else {
                            selectedDate = date
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    PixelShadowButton(
                        onClick = { onDismiss() },
                        backgroundColor = BackgroundGray
                    ) {
                        Text(
                            "NO", style = DungGeunMoBody, color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.width(50.dp))
                    PixelShadowButton(
                        onClick = { onDateConfirmed(selectedDate) },
                        backgroundColor = BackgroundGray
                    ) {
                        Text(
                            "YES", style = DungGeunMoBody, color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
```

- [ ] **Step 2: 빌드 확인**

Run: `./gradlew :ui:compileDebugKotlin`

- [ ] **Step 3: 커밋**

```bash
git add ui/src/main/java/project/side/ui/component/CalendarBottomSheet.kt
git commit -m "feat: CalendarBottomSheet → Dialog 전환 + 레트로 스타일 적용"
```

### 6-2. BookRegisterBottomSheet → Dialog

- [ ] **Step 4: BookRegisterBottomSheet.kt 컨테이너 변경**

import 변경:
```kotlin
// 제거
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState

// 추가
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
```

`@file:OptIn(ExperimentalMaterial3Api::class)` 제거.

`ModalBottomSheet(...)` 래퍼를 아래로 교체:
```kotlin
Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
) {
```
내부 Content 컴포저블은 그대로 유지.

- [ ] **Step 5: 빌드 확인 + 커밋**

```bash
./gradlew :ui:compileDebugKotlin
git add ui/src/main/java/project/side/ui/component/BookRegisterBottomSheet.kt
git commit -m "feat: BookRegisterBottomSheet → Dialog 전환"
```

### 6-3. BookEditBottomSheet → Dialog + scroll

- [ ] **Step 6: BookEditBottomSheet.kt 컨테이너 변경 + 스크롤 추가**

import 변경:
```kotlin
// 제거
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState

// 추가
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
```

`@file:OptIn(ExperimentalMaterial3Api::class)` 제거.

`ModalBottomSheet(...)` 래퍼를 아래로 교체:
```kotlin
Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // 기존 내부 Content 그대로
    }
}
```

- [ ] **Step 7: 빌드 확인 + 커밋**

```bash
./gradlew :ui:compileDebugKotlin
git add ui/src/main/java/project/side/ui/component/BookEditBottomSheet.kt
git commit -m "feat: BookEditBottomSheet → Dialog 전환 + verticalScroll 추가"
```

### 6-4. ReadingStartBottomSheet → Dialog

- [ ] **Step 8: ReadingStartBottomSheet.kt 컨테이너 변경**

import 변경:
```kotlin
// 제거
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState

// 추가
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
```

`@file:OptIn(ExperimentalMaterial3Api::class)` 제거.

`ModalBottomSheet(...)` 래퍼를 아래로 교체:
```kotlin
Dialog(
    onDismissRequest = { onDismiss() },
    properties = DialogProperties(usePlatformDefaultWidth = false)
) {
```
내부 `ReadingStartBottomSheetContent`는 그대로 유지.

- [ ] **Step 9: 빌드 확인 + 커밋**

```bash
./gradlew :ui:compileDebugKotlin
git add ui/src/main/java/project/side/ui/component/ReadingStartBottomSheet.kt
git commit -m "feat: ReadingStartBottomSheet → Dialog 전환"
```

---

## Task 7: ReadingStartBottomSheet 책 제목 + 스낵바

**Files:**
- Modify: `ui/src/main/java/project/side/ui/component/ReadingStartBottomSheet.kt:45-67`
- Modify: `ui/src/main/java/project/side/ui/screen/HomeScreen.kt:53,140`
- Modify: `ui/src/main/java/project/side/ui/screen/MainScreen.kt:64-74,117-119`
- Modify: `presentation/src/main/java/project/side/presentation/viewmodel/MainViewModel.kt:128`

- [ ] **Step 1: ReadingStartBottomSheet에 bookTitle 파라미터 추가**

`ReadingStartBottomSheet.kt` 수정:

```kotlin
@Composable
fun ReadingStartBottomSheet(
    show: Boolean,
    bookTitle: String = "",
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (!show) return

    Dialog(onDismissRequest = { onDismiss() }) {
        ReadingStartBottomSheetContent(
            bookTitle = bookTitle,
            onDismiss = onDismiss,
            onConfirm = onConfirm
        )
    }
}

@Composable
internal fun ReadingStartBottomSheetContent(
    bookTitle: String = "",
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
```

- [ ] **Step 2: ReadingStartBottomSheetContent 내부에 책 제목 추가**

import 추가:
```kotlin
import project.side.ui.theme.WantedSansBody
```

"책 시작하기" Text (124-128행) 뒤에 책 제목 추가:

```kotlin
Text(
    "책 시작하기",
    style = DungGeunMoPopupTitle,
    color = TextPrimary
)
Spacer(Modifier.height(12.dp))
Text(
    bookTitle,
    style = WantedSansBody,
    color = TextPrimary
)
Spacer(Modifier.height(24.dp))
```

> 기존 `Spacer(Modifier.height(32.dp))`(129행)을 제거하고 위 코드로 교체

- [ ] **Step 3: HomeScreen onStartReading 시그니처 변경**

`HomeScreen.kt` 수정:

```kotlin
// 53행: 시그니처 변경
onStartReading: (Int, String) -> Unit = { _, _ -> },

// 140행: 호출부 변경
onStartReading = { onStartReading(book.mybookId, book.title) },
```

- [ ] **Step 4: MainScreen 상태 추가 + 시그니처 연동**

`MainScreen.kt` 수정:

```kotlin
// 64행 뒤에 추가
val readingStartBookTitle = remember { mutableStateOf("") }

// 67-74행: ReadingStartBottomSheet 호출 수정
ReadingStartBottomSheet(
    show = showReadingStartSheet.value,
    bookTitle = readingStartBookTitle.value,
    onDismiss = { showReadingStartSheet.value = false },
    onConfirm = {
        showReadingStartSheet.value = false
        mainViewModel.startReading(readingStartMybookId.intValue)
    }
)

// 117-119행: onStartReading 콜백 수정
onStartReading = { mybookId, title ->
    readingStartMybookId.intValue = mybookId
    readingStartBookTitle.value = title
    showReadingStartSheet.value = true
},
```

import 추가:
```kotlin
import androidx.compose.runtime.mutableStateOf
```

- [ ] **Step 5: MainViewModel 스낵바 메시지 교체**

`MainViewModel.kt` 128행 수정:

변경 전:
```kotlin
SnackbarManager.show("독서를 시작했어요")
```

변경 후:
```kotlin
SnackbarManager.show("시작한 책은 히스토리에서 볼 수 있어요.")
```

- [ ] **Step 6: 빌드 확인**

Run: `./gradlew :ui:compileDebugKotlin && ./gradlew :presentation:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 7: 커밋**

```bash
git add ui/src/main/java/project/side/ui/component/ReadingStartBottomSheet.kt ui/src/main/java/project/side/ui/screen/HomeScreen.kt ui/src/main/java/project/side/ui/screen/MainScreen.kt presentation/src/main/java/project/side/presentation/viewmodel/MainViewModel.kt
git commit -m "feat: 독서 시작 팝업에 책 제목 표시 + 스낵바 메시지 변경"
```

---

## Task 8: SnackBar 스타일 통일 (Login, Signup)

**Files:**
- Modify: `ui/src/main/java/project/side/ui/screen/LoginScreen.kt:81`
- Modify: `ui/src/main/java/project/side/ui/screen/SignupScreen.kt:56`

- [ ] **Step 1: LoginScreen CustomSnackbarHost 적용**

`LoginScreen.kt`에 import 추가:
```kotlin
import project.side.ui.component.CustomSnackbarHost
```

81행 수정:
```kotlin
// 변경 전
snackbarHost = { SnackbarHost(snackbarHostState) }

// 변경 후
snackbarHost = { CustomSnackbarHost(snackbarHostState) }
```

기존 `SnackbarHost` import 제거 (다른 곳에서 사용하지 않으면):
```kotlin
// 제거
import androidx.compose.material3.SnackbarHost
```

- [ ] **Step 2: SignupScreen CustomSnackbarHost 적용**

`SignupScreen.kt`에 import 추가:
```kotlin
import project.side.ui.component.CustomSnackbarHost
```

56행 수정:
```kotlin
// 변경 전
snackbarHost = { SnackbarHost(snackbarHostState) }

// 변경 후
snackbarHost = { CustomSnackbarHost(snackbarHostState) }
```

기존 `SnackbarHost` import 제거.

- [ ] **Step 3: 빌드 확인**

Run: `./gradlew :ui:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: 커밋**

```bash
git add ui/src/main/java/project/side/ui/screen/LoginScreen.kt ui/src/main/java/project/side/ui/screen/SignupScreen.kt
git commit -m "feat: Login/Signup 화면 SnackBar를 CustomSnackbarHost로 통일"
```

---

## Task 9: 최종 빌드 검증

- [ ] **Step 1: 전체 빌드**

Run: `./gradlew assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: 작업목록 체크리스트 업데이트**

`docs/작업목록.md` 의 체크박스를 완료 처리:
```markdown
- [x] 내 책 검색 화면에서 타입별로 책 이름 위에 다른 태그들 나오도록 하기
- [x] 검색 창 UI 그림자 넣기
- [x] 히스토리 리스트/그리드 버튼들 사이 여백 및 음영 넣기
- [x] 히스토리 화면 폰트 다시 확인해보기
- [x] 팝업 위치 전부 가운데에서 뜨게 하기
- [x] 달력 팝업 UI 좀더 잘 반영하기
- [x] 빠진 팝업 UI 없는지 확인하기
```

- [ ] **Step 3: 최종 커밋**

```bash
git add docs/작업목록.md
git commit -m "docs: UI 개선 체크리스트 완료 처리"
```
