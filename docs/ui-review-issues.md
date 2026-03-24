# UI 점검 이슈 목록

**점검일: 2026-03-24**

## 높음

### SettingScreen - 공지사항 비활성 상태 미표시
- **파일**: `ui/src/main/java/project/side/ui/screen/SettingScreen.kt`
- **내용**: "공지사항" 메뉴 클릭 시 아무 반응 없음. disabled 스타일링(투명도 등)이 없어 사용자 혼란 유발
- **제안**: 클릭 비활성화 + 텍스트 alpha 0.3~0.5 적용, 또는 메뉴에서 제거

## 중간

### SettingScreen - 닉네임 빈값 placeholder
- **파일**: `ui/src/main/java/project/side/ui/screen/SettingScreen.kt` (line 204)
- **내용**: 닉네임이 비어있을 때 "OO님, 안녕하세요!"로 표시됨. 어색함
- **제안**: 앱 이름 또는 "회원"으로 변경

### BookEditBottomSheet - 하드코딩된 색상
- **파일**: `ui/src/main/java/project/side/ui/component/BookEditBottomSheet.kt`
- **내용**: `Color(0xFFE4E4E4)`, `Color(0xFFF5F5F5)`, `Color.Black`, `Color.Gray` 등 테마 색상 대신 직접 지정
- **제안**: `BackgroundGray`, `TextPrimary` 등 테마 색상으로 통일

### BookRegisterBottomSheet - 하드코딩된 색상
- **파일**: `ui/src/main/java/project/side/ui/component/BookRegisterBottomSheet.kt`
- **내용**: `Color(0xFFE4E4E4)`, `.copy(alpha = 0.6f)` 등 테마와 불일치
- **제안**: 테마 색상으로 통일

### SearchBookScreen - 에러 placeholder 색상
- **파일**: `ui/src/main/java/project/side/ui/screen/SearchBookScreen.kt` (line 230)
- **내용**: 에러 placeholder에 `Color.Red` 사용 - 레트로 테마와 어울리지 않음
- **제안**: `Primary` 또는 부드러운 회색으로 변경

## 낮음

### HomeBookItem - 미사용 파라미터
- **파일**: `ui/src/main/java/project/side/ui/component/HomeBookItem.kt` (line 37)
- **내용**: `author: String = ""` 파라미터가 선언되어 있지만 화면에 표시하지 않음
- **제안**: 파라미터 제거 또는 저자 정보 표시

### MainScreen - 중복 import
- **파일**: `ui/src/main/java/project/side/ui/screen/MainScreen.kt` (line 12, 22)
- **내용**: `androidx.hilt.navigation.compose.hiltViewModel` 중복 import
- **제안**: 하나 제거

### HistoryScreen - 아이콘 크기 변화
- **파일**: `ui/src/main/java/project/side/ui/screen/HistoryScreen.kt`
- **내용**: 그리드/리스트 전환 아이콘이 pressed 시 36dp→38dp로 변화하며 미세한 떨림 발생
- **제안**: 크기를 동일하게 유지하고 색상 변화만으로 pressed 상태 표현

### 공통 - 날짜 포맷 중복
- **파일**: BookInfoScreen, HistoryScreen, ReadingStartBottomSheet 등
- **내용**: `.take(10).replace("-", " - ")` 패턴이 여러 곳에 반복
- **제안**: 유틸 함수로 추출

### 공통 - 하드코딩된 한국어 문자열
- **파일**: 대부분의 Screen/Component
- **내용**: 한국어 텍스트가 코드에 직접 작성되어 있음. 다국어 지원 시 문제
- **제안**: `strings.xml` 리소스로 이동 (미국 서비스 대응 시 필수)
