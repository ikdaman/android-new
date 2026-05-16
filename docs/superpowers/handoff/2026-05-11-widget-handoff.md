# 모아북 위젯 Handoff

> 작성일: 2026-05-11
> 작업 기간: 2026-05-10 ~ 2026-05-11
> 대상 버전: 1.0.21 후보
> 진행 단계: **코드 구현 완료, 수동 테스트 + 폰트 등록 follow-up 대기**

이 문서는 다음 작업자(또는 다음 세션의 본인)가 위젯 작업을 이어받기 위한 단일 입구점입니다.

---

## 1. 한눈에 보는 현재 상태

| 항목 | 상태 |
|---|---|
| Brainstorm (UX/스펙 결정) | ✅ |
| Spec 문서 | ✅ `docs/superpowers/specs/2026-05-10-widget-design.md` |
| Implementation plan | ✅ `docs/superpowers/plans/2026-05-10-widget-implementation.md` |
| 코드 구현 (T1–T18) | ✅ 24 commits, 93 files, +6,452 LOC, master 직접 |
| 단위 테스트 | ✅ 31개 (widget 28 + ui 3) 모두 통과 |
| 빌드 | ✅ `:app:assembleDebug` 성공 |
| **수동 테스트 (T19)** | ⏳ 미진행 — 실기기/에뮬레이터 필요 |
| **폰트 자원 등록** | ⏳ 미진행 — Wanted Sans / DungGeunMo 미등록, 시스템 기본 폰트 fallback |
| Spec §14 미해결 항목 5개 | ⏳ 부분 — 아래 §6 참조 |

**Branch 정책**: 사용자가 master에 직접 커밋 명시 동의. 별도 feature branch 없음.

---

## 2. 무엇을 만들었나

홈 위젯 3종 (S/M/L) — 모아북 "내 서점"(읽고싶은 책 목록, `ShelfType.STORE`) 노출.

| 사이즈 | 셀 | 콘텐츠 | 인터랙션 |
|---|---|---|---|
| S (110×110) | 2×2 | 책아이콘 + 새로고침 + 책 제목 + 날짜 라벨 | 전체 탭 = 책 상세 / 새로고침 = 다른 책 |
| M (250×110) | 4×2 | 책아이콘 + 제목 + 이유 + YYYY.MM.DD + 5점 인디케이터 + ◀▶ | 본문 탭 = 책 상세 / 화살표 = 페이지 이동 (스와이프 X) |
| L (250×250) | 4×4 | "BOOK NAME" 헤더 + 9권 리스트 | 어디 탭해도 앱 홈 |

> dp 값은 Android Widget 셀 산정 공식 `70 × n − 30` 기준(2셀=110, 4셀=250). 초기 spec은 Figma 픽셀(158/338/354)이었으나 일부 런처에서 의도한 셀 수와 어긋나는 문제가 있어 2026-05-16에 정정 (§6.7 참고).

각 사이즈에 **흰색(#F6F9FF) / 파란색(#010196)** 두 변형. 위젯 추가 시 Configuration Activity에서 색상 선택.

**기술 스택**: Jetpack Glance 1.1.1 단일 (RemoteViews 미사용), Hilt, DataStore, kotlinx-serialization.

---

## 3. 모듈 구조

```
moabook/
├── widget/                          ← 신규 모듈 (이번 작업)
│   ├── build.gradle.kts             android-library + glance + datastore + serialization + hilt
│   ├── src/main/
│   │   ├── AndroidManifest.xml      3 receivers + Configuration Activity
│   │   ├── kotlin/project/side/widget/
│   │   │   ├── action/              5 ActionCallback (OpenBook/OpenApp/RefreshSmall/Prev/Next) + RefreshLogic
│   │   │   ├── data/                WidgetCache, WidgetPreferences, WidgetUpdater(Impl), GlanceWidgetUpdateNotifier, WidgetUiBook, StoreBookMapper
│   │   │   ├── di/                  WidgetModule (Hilt)
│   │   │   ├── domain/              DateLabel
│   │   │   ├── glance/              SmallWidget, MediumWidget, LargeWidget + components/ + theme/
│   │   │   ├── intent/              WidgetIntents (Intent 빌더 + extras 키)
│   │   │   ├── receiver/            3 GlanceAppWidgetReceiver
│   │   │   ├── state/               WidgetStateKeys (DataStore key 정의)
│   │   │   ├── theme/               ColorVariant
│   │   │   └── ui/                  WidgetConfigurationActivity (Compose)
│   │   └── res/                     drawable (책+새로고침 vector), values/colors.xml, xml/widget_*_info, layout/widget_loading
│   └── src/test/                    28개 단위 테스트
├── ui/
│   ├── src/main/AndroidManifest.xml MainActivity launchMode="singleTask" 추가
│   └── src/main/java/project/side/ui/
│       ├── MainActivity.kt          Intent extras 핸들러 + onNewIntent + sequence number
│       ├── WidgetTarget.kt          신규 — sealed class Book/Home
│       ├── WidgetIntentParser.kt    신규 — extract/setPending 로직 (현재는 미사용 중복, follow-up §6)
│       └── screen/MainScreen.kt     widgetTarget + widgetSeq + onWidgetTargetConsumed 파라미터 추가
├── presentation/
│   ├── build.gradle.kts             :widget 의존 추가
│   └── src/main/java/project/side/presentation/viewmodel/
│       ├── MainViewModel.kt         deleteBook + startReading 후 widgetUpdater.refreshAll()
│       ├── BookInfoViewModel.kt     updateMyBook + deleteBook 후 동일
│       ├── ManualInputViewModel.kt  saveManualBookInfo 후 동일
│       └── SearchBookViewModel.kt   saveSelectedBook 후 동일
└── app/
    └── build.gradle.kts             :widget 의존 추가
```

**의존성 그래프**: `widget → domain (only)`, `presentation → widget`, `app → widget`. 순환 없음.

---

## 4. 데이터 흐름 (정상 케이스)

```
사용자가 앱에서 책 추가
   ↓
ViewModel.saveManualBook → DataResource.Success
   ↓
widgetUpdater.refreshAll()                    ← T18 hook
   ↓
MyBookRepository.getStoreBooks(0, 9, "createdDate,desc")  ← Hilt singleton
   ↓
WidgetCache.put(books.map { toWidgetUiBook() })  ← DataStore 직렬화
   ↓
GlanceWidgetUpdateNotifier.notifyAllWidgets()
   ↓
SmallWidget().updateAll(context)              ← Glance가 provideGlance 재실행
MediumWidget().updateAll(context)
LargeWidget().updateAll(context)
   ↓
각 위젯 재구성 (cache.read() → variant 결정 → SmallContent/Medium/Large)
```

**갱신 트리거 (spec §6.1)**:
- 앱 내 책 변경 (save/delete/update/readingStatus) → 6곳에서 hook
- 위젯 추가/시스템 onUpdate → receiver의 refreshAll
- S 위젯 새로고침 버튼 → RefreshSmallAction (서버 호출 + cache 갱신 + 다른 책 픽)
- M 위젯 화살표 → Prev/NextAction (캐시 안 인덱스만)
- **백그라운드 주기 갱신 없음** (`updatePeriodMillis="0"`) — 사용자 결정

---

## 5. 수동 테스트 — 다음 작업자가 해야 할 것

### 5.1 빌드 & 설치

```bash
# 디바이스/에뮬레이터 연결 후
./gradlew :app:installDebug

# 또는 Android Studio에서 직접 Run
```

### 5.2 체크리스트 (plan §11 + spec §3)

| # | 시나리오 | 기대 결과 |
|---|---|---|
| 1 | 홈 → 위젯 추가 메뉴 | 모아북 S/M/L 카드 모두 노출 |
| 2 | S 위젯 추가 | Configuration 화면 → 색상 선택(흰/파) → 완료 → 위젯 표시 |
| 3 | 미로그인 상태로 위젯 추가 | "읽고 싶은 책을 추가해 보세요 !" empty state |
| 4 | 미로그인에서 empty state 탭 | 앱 열림 (로그인 화면으로 자연스럽게 흘러감) |
| 5 | 로그인 후 책 1권 추가 | 위젯 즉시 반영 (별도 새로고침 불필요) |
| 6 | S 위젯 우상단 새로고침 아이콘 탭 | 다른 책으로 교체 (현재 책 ≠ 새 책) |
| 7 | S 위젯 본문 탭 | 해당 책 상세 화면 진입 |
| 8 | 책 상세 → 뒤로 → 같은 S 위젯 다시 탭 | 다시 책 상세 진입 (sequence number 동작) |
| 9 | 5권 추가 후 M 위젯 추가 | 첫 책 노출, 5점 인디케이터 |
| 10 | M 위젯 ▶ 탭 | 다음 책 + 인디케이터 이동 |
| 11 | M 위젯 ◀ 탭 (첫 책일 때) | 마지막 책으로 wrap |
| 12 | M 본문 탭 | 책 상세 진입 |
| 13 | 이유 없는 책 → M 위젯 | 회색 "읽고 싶은 이유를 추가해 주세요." |
| 14 | 101일 이상 된 책 → S 위젯 | "책에 먼지가 쌓였어요..." |
| 15 | 9권 추가 후 L 위젯 | 9권 리스트 + "BOOK NAME" 헤더 |
| 16 | L 위젯 어디든 탭 | 앱 홈 화면 |
| 17 | 앱에서 책 삭제 | 모든 위젯 즉시 갱신 |
| 18 | 위젯 리사이즈 시도 | resizeMode=none이라 변경 불가 (정상) |
| 19 | 다크 모드 ↔ 라이트 모드 전환 | 위젯 색은 변경 없음 (자체 색상 정의) |

각 항목 결과를 기록하고, 실패 시 별도 fix task 생성.

---

## 6. Release 전 권장 follow-up

우선순위 높은 순서:

### 6.1 [DONE] 2026-05-16 폰트 자원 등록 + 적용

- **해결**: ui 모듈의 `wanted_sans_regular.ttf`, `wanted_sans_semibold.ttf`, `dunggeunmo.ttf`를 widget 모듈 `res/font/`로 복사. 모든 위젯 컨텐츠를 RemoteViews layout 으로 전환하여 `android:fontFamily="@font/..."` 로 직접 적용.
- **잔여**: Glance Composable 의 `EmptyState`는 여전히 system 폰트 (S/M empty state). 사용 빈도 낮아 후속 처리.

### 6.2 [MEDIUM] WidgetIntentParser 중복 정리
- **문제**: `ui/src/main/java/project/side/ui/WidgetIntentParser.kt`가 만들어졌지만 production에서는 MainActivity가 inline으로 같은 로직(`extractWidgetTarget`, `setPending`)을 가짐. 테스트만 parser를 검증 → drift 위험.
- **선택지 A**: MainActivity가 parser에 위임하도록 변경
- **선택지 B**: parser 파일 삭제하고 MainActivity 로직을 internal로 노출 후 직접 테스트

### 6.3 [MEDIUM] 미로그인 시 401 fetch 가드
- **문제**: 모든 receiver의 `onUpdate` → `refreshAll()`이 로그인 상태 확인 없이 매번 호출. 미로그인 상태에서 401 노이즈.
- **현황 (2026-05-16)**: WidgetUpdaterImpl 에 진단 로그 추가. `adb logcat -s WidgetUpdater` 로 fetch 시작 / 성공(N books) / 에러(message) / 종료를 추적 가능.
- **추가 fix (2026-05-16)**: 5개 receiver 모두 `goAsync()` 패턴 적용 — receiver의 `scope.launch { refreshAll() }` 가 `onUpdate` 반환 직후 process kill로 cancel될 가능성 차단.
- **수정안**: WidgetUpdaterImpl에 `isLoggedInUseCase` 또는 AuthTokenProvider 의존 추가 → 미로그인/토큰 없음 시 fetch 스킵, cache만 사용 (그러면 Empty state 자연스럽게).

### 6.4 [LOW] M 위젯 stale current index UX
- **문제**: 9권 → 3권으로 줄면 `MEDIUM_CURRENT_INDEX=7`이 stale. coerceIn으로 안전하지만 wrap 직후 사용자에게 점프처럼 보임.
- **수정 방향**: Prev/Next도 mybookId 기반 추적 (S 위젯과 같은 패턴), 또는 cache 갱신 시 인덱스 reset.

### 6.5 [LOW] Spec §14 미해결 항목들
- §14.1 `MyBookRepository.getStoreBooks` `sort` 파라미터 키 — **해결 (2026-05-16)**: 올바른 키는 `createdAt,desc` (앱 UI MainViewModel.kt:131 과 동일). 위젯이 `createdDate,desc` 를 보내고 있어 서버가 HTTP 500 던지는 게 "위젯에 책이 안 보임"의 root cause 였음. WidgetUpdaterImpl.kt 수정 완료.
- §14.2 L 위젯 파란 변형 헤더 색 — L 흰색 전용화로 N/A.
- §14.4 `images/` 디렉토리(untracked) 정리

### 6.7 [DONE] 2026-05-16 위젯 dp 산정 정정

- **문제**: 초기 spec의 dp 값(158/338/354)은 Figma 디자인 픽셀 사이즈를 그대로 옮긴 것으로, Android Widget 셀 산정 공식 `cells = round((dp + 30) / 70)` 과 어긋남. `targetCellWidth/Height` 는 API 31+ 에서만 우선 동작하므로 일부 런처(Android 11 이하 + 일부 Android 12+ 런처)에서 S 가 2×2 가 아닌 2×3 으로, M 이 4×2 가 아닌 4×3 으로 보이는 문제 발생.
- **정정**: `70 × n − 30` 공식 기준으로 모든 `widget_*_info.xml` (S/M/L + 새 white/blue 변형) dp 값을 110/250 으로 통일.
- **영향 파일**:
  - `widget/src/main/res/xml/widget_small_info.xml` → `minWidth/Height=110dp`
  - `widget/src/main/res/xml/widget_medium_info.xml` → `minWidth=250dp, minHeight=110dp`
  - `widget/src/main/res/xml/widget_large_info.xml` → `minWidth/Height=250dp`
  - `widget/src/main/res/xml/widget_small_white_info.xml` (untracked) → 동일
  - `widget/src/main/res/xml/widget_small_blue_info.xml` (untracked) → 동일
- **spec 갱신**: `docs/superpowers/specs/2026-05-10-widget-design.md` §1, §3, §3.1~3.3 모두 정정. 셀 산정 공식 메모 §3 시작에 추가.

### 6.8 [DONE] 2026-05-16 위젯 색상별 별도 위젯화 + L 흰색 전용

- S/M 위젯에 색상 선택 단계를 없애고 White/Blue 변형을 별도 위젯으로 분리. L 위젯은 흰색 전용으로 단순화.
- 위젯 추가 메뉴에서 "모아북 S (흰색)", "모아북 S (파란색)", "모아북 M (흰색)", "모아북 M (파란색)", "모아북 L" 5개가 보임.
- WidgetConfigurationActivity / WidgetPreferences.setColor / `widget_small_info.xml` / `widget_medium_info.xml` 모두 dead code 제거.
- 영향: spec §2 "색상 변형" 결정사항이 변경됨 — Configuration Activity → per-variant receiver로 전환.

### 6.9 [DONE] 2026-05-16 위젯 미리보기 layout 추가

- 모든 receiver xml info 에 `android:previewLayout` 속성 추가. Android 12+ (API 31+) 위젯 추가 메뉴에서 5개 위젯 각자 실제 모습 미리보기 노출.
- 새 리소스:
  - `res/layout/widget_preview_{small,medium}_{white,blue}.xml` + `widget_preview_large_white.xml` (5개)
  - `res/drawable/widget_bg_{white,blue,large_header}.xml` (3개)
- **Android 11 이하 fallback (`previewImage`)는 미구현**. 정적 PNG 또는 vector drawable 추가 시 옛 기기에서도 미리보기 보임. 디자이너 자산 준비되면 `android:previewImage="@drawable/widget_preview_..."` 형태로 추가.
- 폰트 미등록 상태(§6.1)이므로 미리보기 layout의 TextView도 시스템 기본 폰트 사용.

### 6.10 [DONE] 2026-05-16 위젯을 RemoteViews layout 으로 전환

- S/M/L 모든 위젯의 컨텐츠를 RemoteViews layout XML 으로 변환 (`widget_small_content.xml`, `widget_medium_content.xml`, `widget_large_content.xml`). Glance Composable 안에서 `AndroidRemoteViews` 로 host.
- 이유: Glance `Text` 의 `fontFamily(FontFamily(...))` 가 res/font 의 ttf 를 못 가리켜서 본문/날짜 폰트가 시스템 기본으로 fallback. Figma 디자인(Wanted Sans + DungGeunMo) 과 어긋남.
- variant(white/blue) 차이는 코드에서 RemoteViews 의 `setInt(setBackgroundResource)`, `setImageViewResource`, `setTextColor` 로 처리.
- M 위젯 점 인디케이터는 미리 5개 ImageView 를 정의하고 `setImageViewResource` 로 active/inactive drawable 교체.
- **잔여 액션 손실**:
  - S 위젯 새로고침 버튼: RemoteViews 안의 view 에 Glance `actionRunCallback` 못 박음 → 시각만 유지. PendingIntent + custom action receiver 로 복구 가능 (후속).
  - M 위젯 점 클릭 페이지 이동: 같은 이유. 현재 인덱스 첫 책만 표시 (그러나 prev/next action 호출되면 Glance state 변경되어 위젯 갱신은 동작 — 외부에서 호출 시).

### 6.11 [INFO] background revalidate 미구현
spec §6.2 stale-while-revalidate 두 번째 단계("같은 composition에서 백그라운드 fetch")는 미구현. 현재 fetch는 receiver `onUpdate` + 사용자 액션에서만. 사용자 결정 정책("백그라운드 주기 갱신 없음")과 부합하므로 의도된 trade-off로 봐도 OK.

---

## 7. 알려진 한계 / 주의사항

### 7.1 BookInfo 라우트 navigation
BookInfo는 root MainActivity NavHost가 아닌 **MainScreen 내부 sub-NavHost**에 있음. 위젯 → 책 상세 진입 시:
1. MainActivity가 root navController로 `MAIN_ROUTE`로 이동
2. MainScreen이 재구성되며 `widgetTarget` + `widgetSeq` 파라미터 받음
3. MainScreen의 `LaunchedEffect(widgetSeq)`가 sub-navController로 `BookInfo/{id}` 이동
4. `onWidgetTargetConsumed()` 호출로 pending 클리어

**주의**: ViewModel이 root NavHost 재진입 시 재생성될 수 있음. 현재 사용 패턴에서는 문제 없으나, 향후 ViewModel state 보존 필요 시 SavedStateHandle 또는 SharedFlow 기반 처리로 변경 검토.

### 7.2 RefreshSmallAction의 "현재 책 제외"
- 인덱스가 아닌 **mybookId**로 추적 (final review fix). cache 변경에 robust.
- 단, 캐시에 mybookId가 없는 경우(예: 외부에서 책 삭제 후) `pickNextByMybookId`가 첫 책을 반환. 정상 동작이지만 인지 필요.

### 7.3 LargeWidget 헤더 텍스트 색
- WHITE 변형: `#333333` / BLUE 변형: `#010196` (LargeWidget.kt:80)
- spec에 명시 없어 잠정 결정. §6.5 참조.

### 7.4 GlanceAppWidgetReceiver의 CoroutineScope
- 3개 receiver 모두 `private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)` 멤버로 보유
- Receiver 인스턴스가 재사용되는 경우 cancel 없이 누수 가능성. Glance receiver는 일반적으로 짧게 살고 `goAsync()` 처리하므로 실용적 영향 낮음.
- 더 robust하게 가려면 `onDisabled` 또는 `goAsync` 패턴으로 명시 cancel.

### 7.5 launchMode singleTask
- ui Manifest에서 MainActivity에 `android:launchMode="singleTask"` 추가됨
- 위젯 진입 시 새 Activity 인스턴스 생성 방지 + onNewIntent로 처리
- **기존 앱 흐름에 회귀 가능성**: SignupScreen / SearchBook 등에서 task 재사용 동작 변경 가능. 수동 테스트로 검증 필요.

---

## 8. 빌드/실행 명령

```bash
# 위젯 모듈만 빌드
./gradlew :widget:assembleDebug

# 전체 앱 빌드
./gradlew :app:assembleDebug

# 위젯 단위 테스트
./gradlew :widget:testDebugUnitTest

# ui 모듈 단위 테스트 (위젯 진입 처리 포함)
./gradlew :ui:testDebugUnitTest

# 디바이스 설치
./gradlew :app:installDebug

# 의존성 트리 (순환 검증)
./gradlew :widget:dependencies | grep "project :"
```

---

## 9. 핵심 commit 추적

| 카테고리 | Commit | 설명 |
|---|---|---|
| Spec | `7a30a55` | Add widget design spec |
| Plan | `d8736f8` | Add widget implementation plan (19 tasks) |
| 골격 | `79b2a44` `c226534` | widget 모듈 + deps |
| 데이터 레이어 | `3c2e918` `686133a` `b0afb01` `4590a16` `ad0e8d1` | TDD 5개 |
| 자산 + DI | `2f691cb` `e55b23c` | drawable + Hilt |
| Glance UI 기반 | `ca5d431` `9bcfffc` | components + actions |
| 위젯 본체 | `f703aab` `478a0c9` `881ac20` | S/M/L |
| Configuration | `e9c0d83` | 색상 선택 |
| 앱 통합 | `b8ae0e0` `b57780a` `e140302` | app deps + MainActivity |
| ViewModel hooks | `83c8fda` | 6개 mutation hook |
| Final fix | `8c76262` | mybookId 기반 추적 (cache robust) |

전체 git log:
```bash
git log --oneline 7a30a55..HEAD
```

---

## 10. 컨텍스트 — 사용자 결정 요약 (변경 시 spec 갱신 필요)

| 결정 | 값 | 근거 |
|---|---|---|
| 사이즈 | S/M/L 3종 | 사용자 명시 |
| 책 표지 사용 | 안 함 (책아이콘만) | Figma 디자인 |
| S 새로고침 | 서버도 함께 호출 | 사용자 선택 |
| M 슬라이드 | 자동 X, 좌/우 화살표 (스와이프 X) | 사용자 선택 |
| 색상 변형 | S/M/L 모두 흰+파 2가지 | 사용자 선택 |
| L 헤더 텍스트 | "BOOK NAME" 고정 | 사용자 명시 |
| L 탭 | 어디든 앱 홈 | 사용자 명시 |
| 책 선정 | S=캐시 9권 중 랜덤 1, M=최근 5, L=최근 9 | 사용자 결정 |
| 미로그인 | empty state와 동일 | 사용자 결정 |
| 갱신 트리거 | 앱 이벤트 + S 새로고침만 (백그라운드 주기 X) | 사용자 결정 |
| 기술 스택 | Glance 단일 | 사용자가 접근 A 선택 |
| 모듈 위치 | 신규 widget 모듈 | 사용자가 추천 안 선택 |
| Branch | master 직접 (위험 명시 동의) | 사용자 명시 |

---

## 11. 새 세션에서 이어받기

이 작업은 `superpowers:brainstorming` → `writing-plans` → `subagent-driven-development` 사이클로 진행됐습니다.

### 11.1 새 세션 시작 prompt 예시

세션 clear 후 다음 중 하나로 시작하세요:

**케이스 A: 수동 테스트 결과를 가지고 와서 회귀 fix**
```
위젯 작업 이어서 한다. docs/superpowers/handoff/2026-05-11-widget-handoff.md 먼저 읽어.
실기기 테스트 결과:
  - [성공/실패한 항목 나열]
  - 이슈: [구체적 증상]
이 이슈 fix해줘.
```

**케이스 B: 폰트 등록 follow-up**
```
위젯 폰트 등록 작업. docs/superpowers/handoff/2026-05-11-widget-handoff.md §6.1 참고.
ui 모듈에 Wanted Sans / DungGeunMo 폰트 자원이 있는지 먼저 확인하고,
없으면 어떻게 할지 물어봐.
```

**케이스 C: 다른 follow-up (WidgetIntentParser 정리, 401 가드 등)**
```
위젯 follow-up. docs/superpowers/handoff/2026-05-11-widget-handoff.md §6 읽고,
[6.2 / 6.3 / 6.4 중 선택] 항목 처리해줘.
```

**케이스 D: 단순히 컨텍스트만 로드하고 다음 단계를 같이 정하기**
```
위젯 작업 어디까지 됐는지 docs/superpowers/handoff/2026-05-11-widget-handoff.md 읽고 요약해줘.
```

### 11.2 자동 로드되는 컨텍스트

새 세션 시작 시 다음이 자동 로드됩니다 (별도 명령 불필요):
- `~/.claude/projects/-Users-kangmin-dev-moabook/memory/MEMORY.md` 인덱스
- `~/.claude/projects/-Users-kangmin-dev-moabook/memory/widget_brainstorm_in_progress.md` 진행 상태 요약
- `~/.claude/CLAUDE.md` (OMC orchestration 가이드)

따라서 사용자가 "위젯"이라고만 해도 어떤 작업인지 식별 가능. 다만 이 handoff 문서를 명시적으로 읽으라고 시키면 더 정확한 컨텍스트를 가집니다.

### 11.3 참고 자료 우선순위

1. **이 handoff 문서** — 전체 입구, 가장 먼저 읽기
2. `docs/superpowers/specs/2026-05-10-widget-design.md` — 사용자 결정 + UX 명세 (왜 이런 결정인지)
3. `docs/superpowers/plans/2026-05-10-widget-implementation.md` — 왜 이렇게 코드를 짰는지의 근거
4. 코드 자체 — `widget/`, `ui/src/main/.../MainActivity.kt`, `ui/src/main/.../MainScreen.kt`, `presentation/.../viewmodel/`
5. Git history — `git log --oneline 7a30a55..HEAD` (작업 흐름 추적)

### 11.4 같은 사이클을 이어가려면

- 수동 테스트 후 **작은 회귀**: 별도 spec/plan 없이 직접 fix → commit
- **큰 변경**(예: 폰트 등록 시스템): 작은 spec/plan을 새로 만들어 brainstorming → writing-plans → subagent-driven-development 다시 반복
- **새 위젯 사이즈 추가** 같은 큰 기능: 새 brainstorm 세션 권장 (이번 spec/plan을 reference로)

### 11.5 막힐 때

- spec §14 미해결 항목 + 이 문서 §6 follow-up 우선순위 먼저 확인
- 사용자 결정 변경이 필요하면 §10 표 갱신 후 spec/plan/handoff 모두 동기화
- Glance API 1.1.1 컴파일 오류는 §10 commit 흐름 또는 implementer 보고에서 보정 사례 검색 (`actionStartActivity`, `ColorProvider`, `updateAll` import 위치 등)

### 11.6 Branch 정책 재확인

이번 작업은 사용자 명시 동의로 master에 직접 커밋했습니다. **다음 작업은 기본적으로 새 branch + worktree를 권장합니다** — superpowers의 red flag(`Start implementation on main/master branch without explicit user consent`)에 따라 매번 사용자 동의 필요. handoff 문서를 봤다고 해서 자동 master 푸시 권한이 이어지지 않습니다.
