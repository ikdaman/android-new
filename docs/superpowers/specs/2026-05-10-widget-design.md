# 모아북 홈화면 위젯 설계 (S/M/L)

> 작성일: 2026-05-10
> 대상 버전: 1.0.21+
> 대체 문서: 기존 `docs/WIDGET_PLAN.md` (이 spec과 함께 삭제 예정)

## 1. 목적과 스코프

모아북 안드로이드 앱에 "내 서점"(읽고싶은 책 목록, `ShelfType.STORE`)을 노출하는 홈화면 위젯을 추가한다. 사이즈는 **S(158×158), M(338×158), L(338×354)** 세 가지. 모든 위젯이 같은 데이터 소스(`MyBookRepository.getStoreBooks(...)`)를 사용한다.

**스코프 외**:
- 잠금화면 위젯, 태블릿 전용 레이아웃
- 서버에 위젯 사용 통계 전송
- 위젯에서 책 추가/편집 (읽기 전용)

## 2. 사용자 합의 결정사항 요약

| 결정 항목 | 값 |
|---|---|
| 위젯 사이즈 | S, M, L 3종 |
| 데이터 출처 | `MyBookRepository.getStoreBooks(keyword=null, page=0, size=9, sort="createdDate,desc")` |
| 책 선정 | S=캐시 9권 중 랜덤 1권 / M=최근 5권 / L=최근 9권 |
| 색상 변형 | 흰색(#F6F9FF) + 파란색(#010196), 사용자가 위젯 추가 시 선택. **S/M/L 모두 2가지 색** |
| S 인터랙션 | 전체 탭 = 책 상세 / 우상단 새로고침 아이콘 = 책 교체 |
| M 인터랙션 | 본문 탭 = 책 상세 / 좌·우 화살표 = 페이지 이동 (스와이프 X, 자동 전환 X) |
| L 인터랙션 | 어디 탭해도 앱 홈 화면 이동 (책 항목별 구분 X) |
| L 헤더 텍스트 | "BOOK NAME" 고정 |
| 갱신 트리거 | 앱 내 책 추가/삭제/수정 이벤트 + S 새로고침 버튼 + 위젯 추가 시. **백그라운드 주기 갱신 없음** |
| S 새로고침 동작 | 캐시 갱신 + 서버 호출 모두 수행 |
| 미로그인 처리 | Empty state와 동일 ("읽고 싶은 책을 추가해 보세요!") — 탭하면 앱 열기 |
| 책 표지 | 미사용 (디자인이 책 아이콘만 사용) |
| 기술 스택 | Jetpack Glance 단일 (RemoteViews 미사용) |
| 모듈 위치 | 신규 `widget` 모듈 |

## 3. 위젯별 사양

### 3.1 S (Small) — 158dp × 158dp

```
┌─────────────────────────────┐
│ [📖]                  [↻]   │  헤더 28dp
│                             │
│ 책 제목 한 줄 ellipsize     │  본문
│                             │
│              오늘 저장      │  하단 우측
└─────────────────────────────┘
```

- **헤더**: 좌 책아이콘 16dp / 우 새로고침 아이콘 16dp
- **본문**: `StoreBookItem.title`, fontFamily=`Wanted Sans` Regular 14sp, 1줄 ellipsize
- **하단 라벨**: `dateLabel(createdDate)` 함수 결과, fontFamily=`DungGeunMo` 10sp, 색=#010196(흰배경) 또는 #FFFFFF(파란배경)
- **dateLabel 규칙**:
  - `daysSince == 0` → "오늘 저장"
  - `1 ≤ daysSince ≤ 100` → "{N}일 전 저장"
  - `daysSince ≥ 101` → "책에 먼지가 쌓였어요..."
- **인터랙션**:
  - 전체 탭 → `OpenBookAction(currentMybookId)` → MainActivity (Intent extras: `widget_target=book`, `mybook_id={id}`)
  - 새로고침 아이콘 탭 → `RefreshSmallAction`:
    1. `getStoreBooks` 호출 → 캐시 갱신 (서버 실패 시 stale 캐시 사용, 새로고침 동작 자체는 계속)
    2. 갱신된 캐시(최대 9권)에서 현재 표시 중인 책 제외하고 1권 랜덤 픽
    3. 캐시가 비어있으면 empty state
    4. 캐시에 책이 1권뿐(=현재 표시 중인 책)이면 그 책 그대로 유지 (변경 없음)
    5. Glance state 갱신

### 3.2 M (Medium) — 338dp × 158dp

```
┌──────────────────────────────────────────┐
│ [📖] 책 제목 ellipsize                   │
│ 읽고 싶은 이유 (3줄)                     │
│ 또는 회색 "읽고 싶은 이유를 추가해 주세요." │
│                            2026.03.03    │
│  ◀  ● ○ ○ ○ ○  ▶                         │
└──────────────────────────────────────────┘
```

- **헤더**: 책아이콘 + 제목 1줄 14sp
- **본문**: `reason` 12sp, 3줄 ellipsize. null/blank이면 회색(`#A7A7A7`) "읽고 싶은 이유를 추가해 주세요."
- **우하단 날짜**: `createdDate` → `YYYY.MM.DD` 포맷 12sp DungGeunMo, 색=#010196(흰)/#FFFFFF(파)
- **페이지 인디케이터**: 5개 점 + 좌·우 화살표 버튼 (디자인 보강). 현재 페이지 점은 `#747474`, 나머지는 `#D9D9D9`. 보유 책이 5권 미만이면 점 개수도 그만큼만
- **인터랙션**:
  - 본문 영역 탭 → `OpenBookAction(currentMybookId)`
  - 좌 화살표 → `PrevAction` (현재 인덱스 -1, 0에서 한번 더 누르면 마지막으로 wrap)
  - 우 화살표 → `NextAction` (현재 인덱스 +1, 끝에서 한번 더 누르면 0으로 wrap)
  - 화살표 액션은 모두 캐시 안에서 인덱스만 변경 (네트워크 호출 없음)

### 3.3 L (Large) — 338dp × 354dp

```
┌──────────────────────────────────────────┐
│ ▓▓▓▓▓▓▓▓▓ BOOK NAME ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓     │  헤더 44dp
├──────────────────────────────────────────┤
│ [📖] 책 제목 1                           │
│ [📖] 책 제목 2                           │
│ [📖] 책 제목 3                           │
│ [📖] 책 제목 4                           │
│ [📖] 책 제목 5                           │
│ [📖] 책 제목 6                           │
│ [📖] 책 제목 7                           │
│ [📖] 책 제목 8                           │
│ [📖] 책 제목 9                           │
└──────────────────────────────────────────┘
```

- **헤더**: 회색 바(`#D4D4D4`, 44dp), 텍스트 "BOOK NAME" DungGeunMo 20sp 중앙 정렬
- **본문**: 9권 항목, 각 행 높이 ≈ 30dp. 책아이콘 + 제목 1줄 ellipsize 14sp
- **데이터 부족 시**: 보유 책이 0권이면 헤더 + 1줄 placeholder("읽고 싶은 책을 추가해 보세요!"), 1~8권이면 있는 만큼만
- **인터랙션**: 위젯 어디를 탭해도 `OpenAppAction` → MainActivity (Intent extras: `widget_target=home`)

### 3.4 Empty / 미로그인 (S/M/L 공통 메시지)

- 메시지: "읽고 싶은 책을 추가해 보세요 !" (DungGeunMo, S에서는 14sp / M에서는 14sp / L에서는 12sp)
- 색상: 배경 변형에 따른 텍스트 색상 따라감
- 탭 → `OpenAppAction` → MainActivity 홈

## 4. 데이터 모델 & Repository

기존 도메인 모델 그대로 사용:

```kotlin
// domain/src/main/java/project/side/domain/model/StoreBook.kt
data class StoreBookItem(
    val mybookId: Int,
    val createdDate: String,    // ISO 8601 또는 서버 포맷
    val title: String,
    val author: List<String>,
    val coverImage: String?,    // 위젯에서 미사용
    val description: String?,   // 위젯에서 미사용
    val reason: String?         // M 위젯에서 사용
)

// domain/src/main/java/project/side/domain/repository/MyBookRepository.kt
fun getStoreBooks(keyword: String?, page: Int?, size: Int?, sort: String? = null): Flow<DataResource<StoreBook>>
```

위젯이 호출할 형식: `getStoreBooks(keyword = null, page = 0, size = 9, sort = "createdDate,desc")`

> 주의: `sort` 파라미터의 정확한 서버 키는 백엔드 명세에 의존. 구현 단계에서 확인하여 spec 갱신 가능.

## 5. 아키텍처 / 모듈 구조

```
moabook/
├── widget/                              ← 신규 모듈
│   ├── build.gradle.kts
│   ├── src/main/
│   │   ├── AndroidManifest.xml          (Receiver 3개 + Configuration Activity)
│   │   ├── kotlin/project/side/widget/
│   │   │   ├── di/
│   │   │   │   └── WidgetModule.kt
│   │   │   ├── glance/
│   │   │   │   ├── SmallWidget.kt
│   │   │   │   ├── MediumWidget.kt
│   │   │   │   ├── LargeWidget.kt
│   │   │   │   ├── components/
│   │   │   │   │   ├── BookHeartIcon.kt
│   │   │   │   │   ├── RefreshIcon.kt
│   │   │   │   │   ├── EmptyState.kt
│   │   │   │   │   └── PageIndicator.kt
│   │   │   │   └── theme/
│   │   │   │       └── ColorVariant.kt   (WHITE / BLUE)
│   │   │   ├── data/
│   │   │   │   ├── WidgetCache.kt        (DataStore + kotlinx-serialization)
│   │   │   │   ├── WidgetPreferences.kt  (appWidgetId → ColorVariant)
│   │   │   │   └── WidgetUpdater.kt      (외부 facade — 앱이 호출)
│   │   │   ├── domain/
│   │   │   │   └── DateLabel.kt          (오늘/N일 전/먼지 로직)
│   │   │   ├── action/
│   │   │   │   ├── OpenBookAction.kt
│   │   │   │   ├── OpenAppAction.kt
│   │   │   │   ├── RefreshSmallAction.kt
│   │   │   │   ├── PrevAction.kt
│   │   │   │   └── NextAction.kt
│   │   │   ├── receiver/
│   │   │   │   ├── SmallWidgetReceiver.kt
│   │   │   │   ├── MediumWidgetReceiver.kt
│   │   │   │   └── LargeWidgetReceiver.kt
│   │   │   └── ui/
│   │   │       └── WidgetConfigurationActivity.kt
│   │   └── res/
│   │       ├── drawable/
│   │       │   ├── ic_book_heart.xml      (Vector Drawable, tint 가능)
│   │       │   └── ic_refresh.xml         (Vector Drawable)
│   │       └── xml/
│   │           ├── widget_small_info.xml
│   │           ├── widget_medium_info.xml
│   │           └── widget_large_info.xml
└── app/
    └── src/main/AndroidManifest.xml      (위젯 receiver는 widget 모듈이 자체 manifest로 등록)
```

**의존성**:
- `widget` → `domain` (UseCase, model, repository interface)
- `widget` → `data` (이미 Hilt에서 `MyBookRepositoryImpl` 바인딩됨, widget은 인터페이스만 사용)
- `app`은 `widget`에 의존 (Hilt 진입점 + WidgetUpdater 호출)

## 6. 데이터 흐름 / 갱신 메커니즘

### 6.1 갱신 트리거 매트릭스

| 트리거 | 위치 | 동작 |
|---|---|---|
| 앱 내 책 저장 | `SaveManualBookInfoUseCase` 성공 콜백 | `WidgetUpdater.refreshAll()` |
| 앱 내 책 삭제 | `DeleteMyBookUseCase` 성공 콜백 | 동일 |
| 앱 내 책 수정 (제목/이유 등) | `UpdateMyBookUseCase` 성공 콜백 | 동일 |
| 읽기 상태 변경 (TODO ↔ INPROGRESS/DONE) | `UpdateReadingStatusUseCase` 성공 | 동일 |
| 위젯 추가/삭제 | Glance Receiver `onUpdate` / `onAppWidgetOptionsChanged` | 1회 로드 |
| S 새로고침 아이콘 탭 | `RefreshSmallAction` | 서버 호출 + 캐시 갱신 + 다른 책 1권 픽 |
| M 화살표 탭 | `PrevAction` / `NextAction` | 캐시 안 인덱스 변경만 (네트워크 X) |
| 시스템 정기 갱신 | **사용 안 함** (`updatePeriodMillis = 0`) | — |

### 6.2 캐시 (WidgetCache)

- 저장 위치: 앱 전용 DataStore (`widget_cache.preferences_pb`)
- 키: 단일 키 `recent_store_books_json` 안에 `List<StoreBookItem>` (최대 9개) 직렬화
- 추가 키: `last_fetched_at` (Long, epoch ms) — 디버깅용
- 정책: stale-while-revalidate
  1. Glance Composable이 시작 시 캐시를 즉시 표시
  2. 같은 composition에서 백그라운드 fetch 시도 → 성공 시 캐시 업데이트 + Glance update 트리거
  3. 네트워크 실패 시 캐시 유지 (위젯이 빈 화면이 되지 않음)

### 6.3 WidgetUpdater (외부 facade)

```kotlin
interface WidgetUpdater {
    suspend fun refreshAll()         // 서버 호출 + 캐시 갱신 + S/M/L 모두 update
    suspend fun refreshFromCache()   // 서버 호출 없이 캐시만 다시 그리기
}

@Singleton
class WidgetUpdaterImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: MyBookRepository,
    private val cache: WidgetCache,
) : WidgetUpdater {
    override suspend fun refreshAll() {
        repository.getStoreBooks(null, 0, 9, "createdDate,desc")
            .first { it !is DataResource.Loading }
            .let { result ->
                if (result is DataResource.Success) {
                    cache.put(result.data.content)
                }
                // 실패해도 stale 캐시 그대로 사용
            }
        SmallWidget().updateAll(context)
        MediumWidget().updateAll(context)
        LargeWidget().updateAll(context)
    }
}
```

호출자 예: `MainViewModel` 또는 UseCase의 `.onSuccess { widgetUpdater.refreshAll() }`

## 7. UI 사양 (색상 / 폰트 / 모서리)

| 요소 | 흰색 변형 | 파란색 변형 |
|---|---|---|
| 배경 | #F6F9FF | #010196 |
| 본문 텍스트 | #333333 | #FFFFFF |
| 날짜 라벨 | #010196 | #FFFFFF |
| 책 아이콘 | #010196 | #FFFFFF |
| 새로고침 아이콘 | #333333 60% | #FFFFFF 60% |
| 인디케이터 활성 | #747474 | #FFFFFF |
| 인디케이터 비활성 | #D9D9D9 | #FFFFFF 30% |
| 모서리 반경 | 22dp | 22dp |
| L 헤더 바 | #D4D4D4 | #FFFFFF 또는 #D4D4D4 (구현 시 디자이너 확인) |

폰트:
- 본문/제목: Wanted Sans Regular
- 날짜/라벨/L헤더: DungGeunMo Regular

> **확인 필요**: L 위젯 파란 변형의 헤더 바 색상이 Figma에 명시되어 있지 않음. 구현 시 디자이너 확인. 잠정값으로 `#FFFFFF` (파란 위에 흰 헤더) 사용 예정.

## 8. Configuration Activity

위젯 추가 시 1회 표시. 단일 액티비티가 S/M/L 모두 처리.

- 화면 구성: "배경 색을 선택해 주세요" + 흰색 카드 / 파란색 카드 + 완료 버튼
- 선택 결과 → `WidgetPreferences`에 `appWidgetId` 키로 `ColorVariant` 저장
- 완료 → `setResult(RESULT_OK, Intent().putExtra(EXTRA_APPWIDGET_ID, appWidgetId))`, `finish()`
- 기본값: 마지막 사용자 선택 (없으면 흰색)
- 추후 위젯 옵션 메뉴(시스템 제공)로 재진입 가능

## 9. 진입점 / Intent 처리

기존 앱에 딥링크 scheme 없음 (확인 완료). 위젯 → 앱은 PendingIntent + Intent extras 패턴.

`MainActivity`:
- `launchMode = "singleTask"` (이미 그렇지 않다면 변경) — 위젯에서 들어올 때 새 인스턴스 생성 방지
- `onNewIntent` / `onCreate`에서 처리:

```kotlin
private fun handleWidgetIntent(intent: Intent) {
    val target = intent.getStringExtra(EXTRA_WIDGET_TARGET) ?: return
    when (target) {
        TARGET_BOOK -> {
            val id = intent.getIntExtra(EXTRA_MYBOOK_ID, -1)
            if (id != -1) navController.navigate("BookInfo/$id")
        }
        TARGET_HOME -> navController.navigate(HOME_ROUTE)
    }
}

companion object {
    const val EXTRA_WIDGET_TARGET = "widget_target"
    const val EXTRA_MYBOOK_ID = "mybook_id"
    const val TARGET_BOOK = "book"
    const val TARGET_HOME = "home"
}
```

위젯 측 ActionRunCallback이 만드는 Intent:

```kotlin
Intent(context, MainActivity::class.java).apply {
    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
    putExtra(MainActivity.EXTRA_WIDGET_TARGET, MainActivity.TARGET_BOOK)
    putExtra(MainActivity.EXTRA_MYBOOK_ID, mybookId)
}
```

## 10. 의존성

`widget/build.gradle.kts`:

```kotlin
plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    id("kotlinx-serialization")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")
    implementation("androidx.work:work-runtime-ktx:2.10.0")     // 향후 확장 대비
    implementation("androidx.hilt:hilt-work:1.2.0")
    ksp("androidx.hilt:hilt-compiler:1.2.0")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
}
```

## 11. 테스트 전략

| 레이어 | 종류 | 도구 |
|---|---|---|
| `DateLabel.format(createdDate, today)` | unit (table-driven) | JUnit |
| `WidgetCache` 직렬화/역직렬화 round-trip | unit | kotlinx-serialization |
| `WidgetPreferences` save/load | instrumented | DataStore + Robolectric |
| `WidgetUpdaterImpl.refreshAll` (성공/실패) | unit | MockK + Turbine |
| `RefreshSmallAction` (현재 책 제외 랜덤 픽) | unit | seed 가능한 Random 주입 |
| Glance Composable 렌더링 | unit | `androidx.glance.appwidget.testing` 1.1+ |
| `MainActivity.handleWidgetIntent` 분기 | instrumented | Espresso + 가짜 Intent |
| Configuration Activity 흐름 | UI test | Espresso |

**수동 테스트 체크리스트**:
- 4개 런처(One UI, Pixel, Nova, MIUI) 위젯 추가 호환성
- S/M/L 색상 변형 각각 정상 표시
- 책 0권 / 1권 / 5권 / 9권+ 시나리오
- 미로그인 → 위젯 → 앱 진입
- 책 상세 진입 후 뒤로가기 → 원래 화면으로 복귀
- 책 추가/삭제 시 위젯 즉시 반영 확인 (수동 새로고침 없이)
- 다크 모드 영향 (위젯은 자체 색상 정의이므로 시스템 테마 무관해야 함)

## 12. 마이그레이션 / 진입 영향

- `docs/WIDGET_PLAN.md` (2026-04-14) 삭제
- `MainActivity` `launchMode = singleTask` 적용 시 기존 흐름 회귀 검증
- `app/build.gradle.kts`에 `:widget` 모듈 추가
- `settings.gradle.kts`에 `include(":widget")` 추가
- `MyBookRepository` 호출에 위젯이 추가되지만 격리된 호출이라 기존 흐름 영향 없음

## 13. 자산 — Figma SVG 추출본

추출일: 2026-05-10, Figma 채널 d4xg5sxw, 노드 ID는 향후 변경 가능.

### 13.1 책 아이콘 (Pixel/Solid/Book Heart, 노드 4528:284)

원본 22×22, fill=`#010196`. drawable 변환 시 `tint` 속성으로 색상 변형.

```xml
<!-- 원본 SVG -->
<svg width="22" height="22" viewBox="0 0 22 22" fill="none" xmlns="http://www.w3.org/2000/svg">
  <path d="M19.2487 15.5834V14.6667H20.1654V1.83341H19.2487V0.916748L3.66536 0.916748V1.83341H2.7487V2.75008H1.83203L1.83203 19.2501H2.7487V20.1667H3.66536V21.0834H19.2487V20.1667H20.1654V19.2501H19.2487V18.3334H18.332V15.5834H19.2487ZM16.4987 19.2501H5.4987V18.3334H4.58203V16.5001H5.4987V15.5834H16.4987V19.2501ZM16.4987 8.25008H15.582V9.16675H14.6654V10.0834H13.7487V11.0001H12.832V11.9167H11.9154V12.8334H10.9987V11.9167H10.082V11.0001H9.16536V10.0834H8.2487V9.16675H7.33203L7.33203 8.25008H6.41536V5.50008H7.33203V4.58341H10.082V5.50008H10.9987V6.41675H11.9154V5.50008H12.832V4.58341L15.582 4.58341V5.50008H16.4987V8.25008Z" fill="#010196"/>
</svg>
```

### 13.2 새로고침 아이콘 (Pixel/Solid/Refresh, 노드 4534:700)

원본 12×12, fill=`#333333` opacity 0.6. drawable 변환 시 `tint` + `alpha`로 변형.

```xml
<!-- 원본 SVG -->
<svg width="12" height="12" viewBox="0 0 12 12" fill="none" xmlns="http://www.w3.org/2000/svg">
  <g clip-path="url(#clip0_4534_700)">
    <path d="M11.5 7V7.5H11V8.5H10.5V9.5H10V10H9.5V10.5H8.5V11H7.5V11.5H4.5V11H3.5V10.5H2.5V10H1.5V10.5H1V11H0.5V7H4.5V7.5H4V8H3.5V9H4V9.5H5V10H7V9.5H8V9H8.5V8.5H9V7.5H9.5V7H11.5Z" fill="#333333" fill-opacity="0.6"/>
    <path d="M11.5 1V5H7.5V4.5H8V4H8.5V3H8V2.5H7V2H5V2.5H4V3H3.5V3.5H3V4.5H2.5V5H0.5V4.5H1V3.5H1.5V2.5H2V2H2.5V1.5H3.5V1H4.5V0.5H7.5V1H8.5V1.5H9.5V2H10.5V1.5H11V1H11.5Z" fill="#333333" fill-opacity="0.6"/>
  </g>
  <defs>
    <clipPath id="clip0_4534_700">
      <rect width="12" height="12" fill="white"/>
    </clipPath>
  </defs>
</svg>
```

> 두 SVG는 픽셀-아트 스타일이므로 Vector Drawable 변환 시 `viewportWidth/viewportHeight`만 일치시키면 그대로 사용 가능. Android Studio의 Asset Studio가 자동 변환.

## 14. 미해결/구현 시 확인할 항목

1. `MyBookRepository.getStoreBooks` `sort` 파라미터의 정확한 백엔드 키 (예: `createdDate,desc` vs `createdAt,desc`) 확인
2. L 위젯 파란 변형의 헤더 바 색상 — Figma에 명시 없으므로 디자이너 확인
3. `MainActivity` 현재 `launchMode` 확인. `singleTask` 아니라면 변경 영향 검토
4. `images/` 디렉토리(현재 untracked) 정리 — SVG 자산은 widget 모듈 res/drawable로 이동
