# 모아북 위젯 구현 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Spec(`docs/superpowers/specs/2026-05-10-widget-design.md`)대로 모아북 홈화면 위젯 S/M/L 3종을 신규 `widget` 모듈로 구현한다.

**Architecture:** Jetpack Glance 단일 스택. `widget` 모듈은 `domain`만 의존. `RepositoryImpl` 바인딩은 `:data`에 있으며 `app` 모듈의 SingletonComponent에서 이미 처리됨. 위젯→앱 진입은 PendingIntent + Intent extras + ui 모듈 `MainActivity` 핸들러. 데이터는 `MyBookRepository.getStoreBooks()` → DataStore 기반 `WidgetCache` (stale-while-revalidate). 갱신은 앱 내 데이터 변경 이벤트 + S 위젯 수동 새로고침만 (백그라운드 주기 갱신 X).

**Tech Stack:** Jetpack Glance 1.1+, Kotlin 2.2, Hilt 2.57, kotlinx.serialization, AndroidX DataStore Preferences 1.1, Compose 기반 Configuration Activity.

---

## File Structure

### 신규 (widget 모듈)

| 경로 | 책임 |
|---|---|
| `widget/build.gradle.kts` | 모듈 빌드 설정 (Glance, DataStore, Hilt, serialization) |
| `widget/src/main/AndroidManifest.xml` | 3개 receiver + Configuration Activity 등록 |
| `widget/src/main/res/drawable/ic_book_heart.xml` | 책 하트 아이콘 (Vector Drawable, tint 가능) |
| `widget/src/main/res/drawable/ic_widget_refresh.xml` | 새로고침 아이콘 (Vector Drawable) |
| `widget/src/main/res/values/colors.xml` | `widget_white_bg`, `widget_blue_bg`, 텍스트/액센트 색 |
| `widget/src/main/res/xml/widget_small_info.xml` | S 위젯 메타 (사이즈, configuration, updatePeriodMillis=0) |
| `widget/src/main/res/xml/widget_medium_info.xml` | M 위젯 메타 |
| `widget/src/main/res/xml/widget_large_info.xml` | L 위젯 메타 |
| `widget/src/main/kotlin/project/side/widget/theme/ColorVariant.kt` | enum WHITE / BLUE |
| `widget/src/main/kotlin/project/side/widget/data/WidgetUiBook.kt` | 위젯 표시용 직렬화 데이터 |
| `widget/src/main/kotlin/project/side/widget/data/WidgetCache.kt` | DataStore 기반 책 캐시 |
| `widget/src/main/kotlin/project/side/widget/data/WidgetPreferences.kt` | appWidgetId → ColorVariant 저장 |
| `widget/src/main/kotlin/project/side/widget/domain/DateLabel.kt` | 오늘/N일 전/먼지 라벨 로직 |
| `widget/src/main/kotlin/project/side/widget/data/WidgetUpdater.kt` | 외부 facade interface |
| `widget/src/main/kotlin/project/side/widget/data/WidgetUpdaterImpl.kt` | facade 구현 (Repository → cache → updateAll) |
| `widget/src/main/kotlin/project/side/widget/glance/components/BookHeartIcon.kt` | 공통 책 아이콘 |
| `widget/src/main/kotlin/project/side/widget/glance/components/RefreshIcon.kt` | 새로고침 아이콘 (S용) |
| `widget/src/main/kotlin/project/side/widget/glance/components/EmptyState.kt` | 책 0권 / 미로그인 표시 |
| `widget/src/main/kotlin/project/side/widget/glance/components/PageIndicator.kt` | M의 5점 + 화살표 |
| `widget/src/main/kotlin/project/side/widget/glance/SmallWidget.kt` | S Composable + GlanceAppWidget |
| `widget/src/main/kotlin/project/side/widget/glance/MediumWidget.kt` | M Composable + GlanceAppWidget |
| `widget/src/main/kotlin/project/side/widget/glance/LargeWidget.kt` | L Composable + GlanceAppWidget |
| `widget/src/main/kotlin/project/side/widget/receiver/SmallWidgetReceiver.kt` | GlanceAppWidgetReceiver + Hilt 진입점 |
| `widget/src/main/kotlin/project/side/widget/receiver/MediumWidgetReceiver.kt` | 동일 |
| `widget/src/main/kotlin/project/side/widget/receiver/LargeWidgetReceiver.kt` | 동일 |
| `widget/src/main/kotlin/project/side/widget/action/OpenBookAction.kt` | 책 상세 진입 |
| `widget/src/main/kotlin/project/side/widget/action/OpenAppAction.kt` | 앱 홈 진입 |
| `widget/src/main/kotlin/project/side/widget/action/RefreshSmallAction.kt` | S 새로고침 |
| `widget/src/main/kotlin/project/side/widget/action/PrevAction.kt` / `NextAction.kt` | M 페이지 이동 |
| `widget/src/main/kotlin/project/side/widget/ui/WidgetConfigurationActivity.kt` | 색상 선택 액티비티 |
| `widget/src/main/kotlin/project/side/widget/di/WidgetModule.kt` | Hilt binding |
| `widget/src/main/kotlin/project/side/widget/intent/WidgetIntents.kt` | Intent extras 키 + 빌더 |

### 신규 (테스트)

| 경로 | 대상 |
|---|---|
| `widget/src/test/kotlin/.../DateLabelTest.kt` | DateLabel 테이블 테스트 |
| `widget/src/test/kotlin/.../WidgetCacheTest.kt` | DataStore round-trip |
| `widget/src/test/kotlin/.../WidgetPreferencesTest.kt` | appWidgetId 저장/로드 |
| `widget/src/test/kotlin/.../WidgetUpdaterImplTest.kt` | mock repo로 refreshAll 동작 |
| `widget/src/test/kotlin/.../RefreshSmallActionLogicTest.kt` | 다른 책 픽 로직 |

### 수정

| 경로 | 변경 |
|---|---|
| `settings.gradle.kts` | `include(":widget")` 추가 |
| `gradle/libs.versions.toml` | glance, hilt-work 미사용, datastore-prefs(이미 있음 — 버전 확인), kotlinx-serialization plugin/lib 추가 |
| `app/build.gradle.kts` | `implementation(project(":widget"))` 추가 |
| `ui/src/main/java/project/side/ui/MainActivity.kt` | Intent extras 핸들러 + onNewIntent + NavController 트리거 |
| `ui/src/main/AndroidManifest.xml` | MainActivity `launchMode="singleTask"` 추가 |
| `domain/src/main/java/project/side/domain/usecase/mybook/*` | (선택) UseCase 성공 후 WidgetUpdater hook (Task 20에서 처리) |

---

## Task 1: widget 모듈 골격 + Gradle 등록

**Goal:** 빈 widget Android library 모듈을 만들어 빌드가 통과되는지 확인.

**Files:**
- Create: `widget/build.gradle.kts`
- Create: `widget/src/main/AndroidManifest.xml`
- Create: `widget/consumer-rules.pro`
- Create: `widget/proguard-rules.pro`
- Modify: `settings.gradle.kts`

- [ ] **Step 1: settings.gradle.kts에 모듈 추가**

`settings.gradle.kts` 마지막에 추가:
```kotlin
include(":widget")
```

- [ ] **Step 2: widget 모듈 골격 생성**

`widget/build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "project.side.widget"

    defaultConfig {
        compileSdk = 36
        minSdk = 29
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
    sourceSets["main"].kotlin.srcDir("src/main/kotlin")
    sourceSets["test"].kotlin.srcDir("src/test/kotlin")
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
}
```

`widget/src/main/AndroidManifest.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android" />
```

`widget/consumer-rules.pro`: 빈 파일.
`widget/proguard-rules.pro`: 빈 파일.

- [ ] **Step 3: 빌드 통과 확인**

Run: `./gradlew :widget:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: 커밋**
```bash
git add settings.gradle.kts widget/
git commit -m "Scaffold widget Android library module"
```

---

## Task 2: 신규 의존성 + version catalog 갱신

**Goal:** Glance, kotlinx-serialization, DataStore를 catalog에 등록하고 widget 모듈에서 사용 가능하게 한다.

**Files:**
- Modify: `gradle/libs.versions.toml`
- Modify: `widget/build.gradle.kts`

- [ ] **Step 1: catalog [versions]에 추가**

`gradle/libs.versions.toml` `[versions]` 블록에 추가:
```toml
glance = "1.1.1"
kotlinxSerialization = "1.7.3"
```

`datastorePreferences = "1.1.4"`는 이미 있음 — 그대로 사용.

- [ ] **Step 2: catalog [libraries]에 추가**

```toml
androidx-glance-appwidget = { module = "androidx.glance:glance-appwidget", version.ref = "glance" }
androidx-glance-material3 = { module = "androidx.glance:glance-material3", version.ref = "glance" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinxSerialization" }
```

- [ ] **Step 3: catalog [plugins]에 추가**

```toml
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

- [ ] **Step 4: widget/build.gradle.kts plugins/dependencies 갱신**

`plugins` 블록 마지막에:
```kotlin
alias(libs.plugins.kotlin.serialization)
```

`dependencies` 블록에 추가:
```kotlin
implementation(libs.androidx.glance.appwidget)
implementation(libs.androidx.glance.material3)
implementation(libs.androidx.datastore.preferences)
implementation(libs.kotlinx.serialization.json)
implementation(libs.kotlinx.coroutines.core)
```

- [ ] **Step 5: 빌드 확인**

Run: `./gradlew :widget:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: 커밋**
```bash
git add gradle/libs.versions.toml widget/build.gradle.kts
git commit -m "Add Glance/serialization/datastore deps for widget module"
```

---

## Task 3: ColorVariant + WidgetUiBook 데이터 모델

**Goal:** 위젯 표시용 직렬화 가능 모델과 색 변형 enum을 정의.

**Files:**
- Create: `widget/src/main/kotlin/project/side/widget/theme/ColorVariant.kt`
- Create: `widget/src/main/kotlin/project/side/widget/data/WidgetUiBook.kt`
- Test: `widget/src/test/kotlin/project/side/widget/data/WidgetUiBookTest.kt`

- [ ] **Step 1: 실패 테스트 작성**

`widget/src/test/kotlin/project/side/widget/data/WidgetUiBookTest.kt`:
```kotlin
package project.side.widget.data

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetUiBookTest {
    @Test
    fun `serialize and deserialize round-trip preserves all fields`() {
        val original = WidgetUiBook(
            mybookId = 42,
            title = "자본주의 시대에서 살아남기",
            reason = "경제 유튜브 슈카에서 추천",
            createdDate = "2026-03-03"
        )
        val json = Json.encodeToString(WidgetUiBook.serializer(), original)
        val decoded = Json.decodeFromString(WidgetUiBook.serializer(), json)
        assertEquals(original, decoded)
    }

    @Test
    fun `nullable reason serializes correctly when null`() {
        val original = WidgetUiBook(mybookId = 1, title = "t", reason = null, createdDate = "2026-01-01")
        val json = Json.encodeToString(WidgetUiBook.serializer(), original)
        val decoded = Json.decodeFromString(WidgetUiBook.serializer(), json)
        assertEquals(original, decoded)
    }
}
```

- [ ] **Step 2: 테스트 실패 확인**

Run: `./gradlew :widget:testDebugUnitTest --tests "*WidgetUiBookTest"`
Expected: COMPILATION FAILED — WidgetUiBook not defined.

- [ ] **Step 3: ColorVariant.kt 작성**

`widget/src/main/kotlin/project/side/widget/theme/ColorVariant.kt`:
```kotlin
package project.side.widget.theme

enum class ColorVariant {
    WHITE, BLUE;

    companion object {
        fun fromName(name: String?): ColorVariant =
            values().firstOrNull { it.name == name } ?: WHITE
    }
}
```

- [ ] **Step 4: WidgetUiBook.kt 작성**

`widget/src/main/kotlin/project/side/widget/data/WidgetUiBook.kt`:
```kotlin
package project.side.widget.data

import kotlinx.serialization.Serializable

@Serializable
data class WidgetUiBook(
    val mybookId: Int,
    val title: String,
    val reason: String?,
    val createdDate: String  // YYYY-MM-DD or server ISO format
)
```

- [ ] **Step 5: 테스트 통과 확인**

Run: `./gradlew :widget:testDebugUnitTest --tests "*WidgetUiBookTest"`
Expected: BUILD SUCCESSFUL, 2 tests passed.

- [ ] **Step 6: 커밋**
```bash
git add widget/src/main/kotlin/project/side/widget/theme/ColorVariant.kt widget/src/main/kotlin/project/side/widget/data/WidgetUiBook.kt widget/src/test/kotlin/project/side/widget/data/WidgetUiBookTest.kt
git commit -m "Add ColorVariant enum and WidgetUiBook serializable model"
```

---

## Task 4: DateLabel 로직 (TDD)

**Goal:** spec §3.1 기준 날짜→라벨 변환 함수 구현. 순수 함수.

**Files:**
- Create: `widget/src/main/kotlin/project/side/widget/domain/DateLabel.kt`
- Test: `widget/src/test/kotlin/project/side/widget/domain/DateLabelTest.kt`

- [ ] **Step 1: 실패 테스트 작성**

`widget/src/test/kotlin/project/side/widget/domain/DateLabelTest.kt`:
```kotlin
package project.side.widget.domain

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class DateLabelTest {
    private val today = LocalDate.of(2026, 5, 10)

    @Test fun `today returns 오늘 저장`() =
        assertEquals("오늘 저장", DateLabel.format("2026-05-10", today))

    @Test fun `1 day ago returns 1일 전 저장`() =
        assertEquals("1일 전 저장", DateLabel.format("2026-05-09", today))

    @Test fun `100 days ago returns 100일 전 저장`() =
        assertEquals("100일 전 저장", DateLabel.format("2026-01-30", today))

    @Test fun `101 days ago returns dust message`() =
        assertEquals("책에 먼지가 쌓였어요...", DateLabel.format("2026-01-29", today))

    @Test fun `365 days ago returns dust message`() =
        assertEquals("책에 먼지가 쌓였어요...", DateLabel.format("2025-05-10", today))

    @Test fun `future date returns 오늘 저장 (defensive)`() =
        assertEquals("오늘 저장", DateLabel.format("2026-05-11", today))

    @Test fun `ISO datetime input is parsed`() =
        assertEquals("오늘 저장", DateLabel.format("2026-05-10T12:34:56", today))

    @Test fun `malformed input returns 오늘 저장 (defensive)`() =
        assertEquals("오늘 저장", DateLabel.format("not-a-date", today))

    @Test fun `formatDisplay produces YYYY dot MM dot DD`() =
        assertEquals("2026.03.03", DateLabel.formatDisplay("2026-03-03"))

    @Test fun `formatDisplay handles ISO datetime`() =
        assertEquals("2026.03.03", DateLabel.formatDisplay("2026-03-03T10:00:00"))

    @Test fun `formatDisplay returns empty on malformed`() =
        assertEquals("", DateLabel.formatDisplay("garbage"))
}
```

- [ ] **Step 2: 테스트 실패 확인**

Run: `./gradlew :widget:testDebugUnitTest --tests "*DateLabelTest"`
Expected: COMPILATION FAILED.

- [ ] **Step 3: DateLabel.kt 구현**

`widget/src/main/kotlin/project/side/widget/domain/DateLabel.kt`:
```kotlin
package project.side.widget.domain

import java.time.LocalDate
import java.time.temporal.ChronoUnit

object DateLabel {
    private val DUST_THRESHOLD_DAYS = 100L
    private const val DUST_MESSAGE = "책에 먼지가 쌓였어요..."
    private const val TODAY_LABEL = "오늘 저장"

    fun format(createdDate: String, today: LocalDate = LocalDate.now()): String {
        val parsed = parseLocalDate(createdDate) ?: return TODAY_LABEL
        val daysSince = ChronoUnit.DAYS.between(parsed, today)
        return when {
            daysSince <= 0L -> TODAY_LABEL
            daysSince <= DUST_THRESHOLD_DAYS -> "${daysSince}일 전 저장"
            else -> DUST_MESSAGE
        }
    }

    fun formatDisplay(createdDate: String): String {
        val parsed = parseLocalDate(createdDate) ?: return ""
        return "%04d.%02d.%02d".format(parsed.year, parsed.monthValue, parsed.dayOfMonth)
    }

    private fun parseLocalDate(input: String): LocalDate? {
        val datePart = input.substringBefore('T').trim()
        return runCatching { LocalDate.parse(datePart) }.getOrNull()
    }
}
```

- [ ] **Step 4: 테스트 통과 확인**

Run: `./gradlew :widget:testDebugUnitTest --tests "*DateLabelTest"`
Expected: BUILD SUCCESSFUL, 11 tests passed.

- [ ] **Step 5: 커밋**
```bash
git add widget/src/main/kotlin/project/side/widget/domain/DateLabel.kt widget/src/test/kotlin/project/side/widget/domain/DateLabelTest.kt
git commit -m "Add DateLabel for widget date formatting"
```

---

## Task 5: WidgetCache (DataStore + serialization, TDD)

**Goal:** 최근 9권을 DataStore에 직렬화 저장/로드. stale-while-revalidate 캐시.

**Files:**
- Create: `widget/src/main/kotlin/project/side/widget/data/WidgetCache.kt`
- Test: `widget/src/test/kotlin/project/side/widget/data/WidgetCacheTest.kt`

- [ ] **Step 1: 테스트 작성**

`widget/src/test/kotlin/project/side/widget/data/WidgetCacheTest.kt`:
```kotlin
package project.side.widget.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.io.path.createTempDirectory

class WidgetCacheTest {
    private lateinit var tempDir: File
    private lateinit var cache: WidgetCache

    @Before fun setup() {
        tempDir = createTempDirectory("widget-cache-test").toFile()
        val store = PreferenceDataStoreFactory.create(
            produceFile = { File(tempDir, "test.preferences_pb") }
        )
        cache = WidgetCache(store)
    }

    @After fun teardown() { tempDir.deleteRecursively() }

    @Test
    fun `empty cache returns empty list`() = runTest {
        assertTrue(cache.read().isEmpty())
    }

    @Test
    fun `put then read round-trip`() = runTest {
        val books = listOf(
            WidgetUiBook(1, "title1", "reason1", "2026-05-10"),
            WidgetUiBook(2, "title2", null, "2026-05-09"),
        )
        cache.put(books)
        assertEquals(books, cache.read())
    }

    @Test
    fun `put truncates beyond 9 entries`() = runTest {
        val books = (1..15).map { WidgetUiBook(it, "t$it", null, "2026-05-10") }
        cache.put(books)
        assertEquals(9, cache.read().size)
        assertEquals(1, cache.read().first().mybookId)
    }

    @Test
    fun `lastFetchedAt updates on put`() = runTest {
        assertEquals(0L, cache.lastFetchedAt())
        cache.put(listOf(WidgetUiBook(1, "t", null, "2026-05-10")))
        assertTrue(cache.lastFetchedAt() > 0L)
    }
}
```

- [ ] **Step 2: 테스트 실패 확인**

Run: `./gradlew :widget:testDebugUnitTest --tests "*WidgetCacheTest"`
Expected: COMPILATION FAILED.

- [ ] **Step 3: WidgetCache.kt 구현**

`widget/src/main/kotlin/project/side/widget/data/WidgetCache.kt`:
```kotlin
package project.side.widget.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class WidgetCache(private val store: DataStore<Preferences>) {

    suspend fun read(): List<WidgetUiBook> {
        val raw = store.data.first()[KEY_BOOKS_JSON] ?: return emptyList()
        return runCatching { json.decodeFromString(LIST_SERIALIZER, raw) }.getOrElse { emptyList() }
    }

    suspend fun put(books: List<WidgetUiBook>) {
        val truncated = books.take(MAX_ENTRIES)
        val raw = json.encodeToString(LIST_SERIALIZER, truncated)
        store.edit { prefs ->
            prefs[KEY_BOOKS_JSON] = raw
            prefs[KEY_LAST_FETCHED_AT] = System.currentTimeMillis()
        }
    }

    suspend fun lastFetchedAt(): Long = store.data.first()[KEY_LAST_FETCHED_AT] ?: 0L

    companion object {
        const val MAX_ENTRIES = 9
        private val KEY_BOOKS_JSON = stringPreferencesKey("recent_store_books_json")
        private val KEY_LAST_FETCHED_AT = longPreferencesKey("last_fetched_at")
        private val LIST_SERIALIZER = ListSerializer(WidgetUiBook.serializer())
        private val json = Json { ignoreUnknownKeys = true }
    }
}
```

- [ ] **Step 4: 테스트 통과 확인**

Run: `./gradlew :widget:testDebugUnitTest --tests "*WidgetCacheTest"`
Expected: 4 tests passed.

- [ ] **Step 5: 커밋**
```bash
git add widget/src/main/kotlin/project/side/widget/data/WidgetCache.kt widget/src/test/kotlin/project/side/widget/data/WidgetCacheTest.kt
git commit -m "Add WidgetCache with DataStore-backed serialization"
```

---

## Task 6: WidgetPreferences (appWidgetId → ColorVariant)

**Goal:** 위젯 인스턴스별 색상 선택 저장/조회 + 마지막 선택값 기본화.

**Files:**
- Create: `widget/src/main/kotlin/project/side/widget/data/WidgetPreferences.kt`
- Test: `widget/src/test/kotlin/project/side/widget/data/WidgetPreferencesTest.kt`

- [ ] **Step 1: 테스트 작성**

`widget/src/test/kotlin/project/side/widget/data/WidgetPreferencesTest.kt`:
```kotlin
package project.side.widget.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import project.side.widget.theme.ColorVariant
import java.io.File
import kotlin.io.path.createTempDirectory

class WidgetPreferencesTest {
    private lateinit var tempDir: File
    private lateinit var prefs: WidgetPreferences

    @Before fun setup() {
        tempDir = createTempDirectory("widget-prefs-test").toFile()
        val store = PreferenceDataStoreFactory.create(
            produceFile = { File(tempDir, "test.preferences_pb") }
        )
        prefs = WidgetPreferences(store)
    }

    @After fun teardown() { tempDir.deleteRecursively() }

    @Test
    fun `unset id falls back to last default which is WHITE initially`() = runTest {
        assertEquals(ColorVariant.WHITE, prefs.colorFor(101))
    }

    @Test
    fun `set then read returns saved value`() = runTest {
        prefs.setColor(42, ColorVariant.BLUE)
        assertEquals(ColorVariant.BLUE, prefs.colorFor(42))
    }

    @Test
    fun `last set value becomes default for new ids`() = runTest {
        prefs.setColor(1, ColorVariant.BLUE)
        assertEquals(ColorVariant.BLUE, prefs.colorFor(999))
    }

    @Test
    fun `clear removes id mapping`() = runTest {
        prefs.setColor(7, ColorVariant.BLUE)
        prefs.clear(7)
        // last default still BLUE because clear doesn't reset default
        assertEquals(ColorVariant.BLUE, prefs.colorFor(7))
    }
}
```

- [ ] **Step 2: 테스트 실패 확인**

Run: `./gradlew :widget:testDebugUnitTest --tests "*WidgetPreferencesTest"`
Expected: COMPILATION FAILED.

- [ ] **Step 3: WidgetPreferences.kt 구현**

`widget/src/main/kotlin/project/side/widget/data/WidgetPreferences.kt`:
```kotlin
package project.side.widget.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import project.side.widget.theme.ColorVariant

class WidgetPreferences(private val store: DataStore<Preferences>) {

    suspend fun colorFor(appWidgetId: Int): ColorVariant {
        val data = store.data.first()
        val key = colorKey(appWidgetId)
        val saved = data[key]
        if (saved != null) return ColorVariant.fromName(saved)
        val default = data[KEY_LAST_DEFAULT]
        return ColorVariant.fromName(default)
    }

    suspend fun setColor(appWidgetId: Int, variant: ColorVariant) {
        store.edit { prefs ->
            prefs[colorKey(appWidgetId)] = variant.name
            prefs[KEY_LAST_DEFAULT] = variant.name
        }
    }

    suspend fun clear(appWidgetId: Int) {
        store.edit { prefs ->
            prefs.remove(colorKey(appWidgetId))
        }
    }

    private fun colorKey(appWidgetId: Int) =
        stringPreferencesKey("widget_color_$appWidgetId")

    companion object {
        private val KEY_LAST_DEFAULT = stringPreferencesKey("widget_color_last_default")
    }
}
```

- [ ] **Step 4: 테스트 통과 확인**

Run: `./gradlew :widget:testDebugUnitTest --tests "*WidgetPreferencesTest"`
Expected: 4 tests passed.

- [ ] **Step 5: 커밋**
```bash
git add widget/src/main/kotlin/project/side/widget/data/WidgetPreferences.kt widget/src/test/kotlin/project/side/widget/data/WidgetPreferencesTest.kt
git commit -m "Add WidgetPreferences for per-id ColorVariant storage"
```

---

## Task 7: WidgetUpdater facade + impl (TDD with mocked repository)

**Goal:** 외부에서 호출하는 단일 진입점. Repository 호출 → cache 갱신 → Glance update.

**Files:**
- Create: `widget/src/main/kotlin/project/side/widget/data/WidgetUpdater.kt`
- Create: `widget/src/main/kotlin/project/side/widget/data/WidgetUpdaterImpl.kt`
- Create: `widget/src/main/kotlin/project/side/widget/data/StoreBookMapper.kt`
- Test: `widget/src/test/kotlin/project/side/widget/data/StoreBookMapperTest.kt`
- Test: `widget/src/test/kotlin/project/side/widget/data/WidgetUpdaterImplTest.kt`

- [ ] **Step 1: 매퍼 테스트 작성**

`widget/src/test/kotlin/project/side/widget/data/StoreBookMapperTest.kt`:
```kotlin
package project.side.widget.data

import org.junit.Assert.assertEquals
import org.junit.Test
import project.side.domain.model.StoreBookItem

class StoreBookMapperTest {
    @Test fun `maps StoreBookItem to WidgetUiBook with all fields`() {
        val item = StoreBookItem(
            mybookId = 99,
            createdDate = "2026-04-01",
            title = "title",
            author = listOf("a"),
            coverImage = "url",
            description = "d",
            reason = "r"
        )
        val expected = WidgetUiBook(99, "title", "r", "2026-04-01")
        assertEquals(expected, item.toWidgetUiBook())
    }

    @Test fun `null reason maps to null`() {
        val item = StoreBookItem(1, "2026-04-01", "t", emptyList(), null, null, null)
        assertEquals(null, item.toWidgetUiBook().reason)
    }
}
```

- [ ] **Step 2: 매퍼 구현**

`widget/src/main/kotlin/project/side/widget/data/StoreBookMapper.kt`:
```kotlin
package project.side.widget.data

import project.side.domain.model.StoreBookItem

fun StoreBookItem.toWidgetUiBook(): WidgetUiBook = WidgetUiBook(
    mybookId = mybookId,
    title = title,
    reason = reason,
    createdDate = createdDate
)
```

- [ ] **Step 3: 매퍼 테스트 통과 확인**

Run: `./gradlew :widget:testDebugUnitTest --tests "*StoreBookMapperTest"`
Expected: 2 tests passed.

- [ ] **Step 4: WidgetUpdater interface 작성**

`widget/src/main/kotlin/project/side/widget/data/WidgetUpdater.kt`:
```kotlin
package project.side.widget.data

interface WidgetUpdater {
    suspend fun refreshAll()
}
```

- [ ] **Step 5: WidgetUpdaterImpl 테스트 작성**

`widget/src/test/kotlin/project/side/widget/data/WidgetUpdaterImplTest.kt`:
```kotlin
package project.side.widget.data

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import project.side.domain.DataResource
import project.side.domain.model.StoreBook
import project.side.domain.model.StoreBookItem
import project.side.domain.repository.MyBookRepository

class WidgetUpdaterImplTest {

    @Test
    fun `refreshAll caches mapped books on success`() = runTest {
        val repo = mockk<MyBookRepository>()
        val cache = mockk<WidgetCache>(relaxed = true)
        val notifier = mockk<WidgetUpdateNotifier>(relaxed = true)
        val items = listOf(
            StoreBookItem(1, "2026-05-10", "t1", listOf("a"), null, null, "r1"),
            StoreBookItem(2, "2026-05-09", "t2", listOf("a"), null, null, null),
        )
        coEvery { repo.getStoreBooks(null, 0, 9, "createdDate,desc") } returns
            flowOf(DataResource.Success(StoreBook(items, 1, 2, true, true, 9, 0, 2, false)))

        val captured = slot<List<WidgetUiBook>>()
        coEvery { cache.put(capture(captured)) } returns Unit

        WidgetUpdaterImpl(repo, cache, notifier).refreshAll()

        assertEquals(2, captured.captured.size)
        assertEquals("t1", captured.captured[0].title)
        coVerify { notifier.notifyAllWidgets() }
    }

    @Test
    fun `refreshAll on Error keeps stale cache and still notifies`() = runTest {
        val repo = mockk<MyBookRepository>()
        val cache = mockk<WidgetCache>(relaxed = true)
        val notifier = mockk<WidgetUpdateNotifier>(relaxed = true)
        coEvery { repo.getStoreBooks(any(), any(), any(), any()) } returns
            flowOf(DataResource.Error("boom", 500))

        WidgetUpdaterImpl(repo, cache, notifier).refreshAll()

        coVerify(exactly = 0) { cache.put(any()) }
        coVerify { notifier.notifyAllWidgets() }
    }
}
```

- [ ] **Step 6: WidgetUpdaterImpl + Notifier interface 구현**

`widget/src/main/kotlin/project/side/widget/data/WidgetUpdater.kt` (interface 추가):
```kotlin
package project.side.widget.data

interface WidgetUpdater {
    suspend fun refreshAll()
}

interface WidgetUpdateNotifier {
    suspend fun notifyAllWidgets()
}
```

`widget/src/main/kotlin/project/side/widget/data/WidgetUpdaterImpl.kt`:
```kotlin
package project.side.widget.data

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first
import project.side.domain.DataResource
import project.side.domain.repository.MyBookRepository

@Singleton
class WidgetUpdaterImpl @Inject constructor(
    private val repository: MyBookRepository,
    private val cache: WidgetCache,
    private val notifier: WidgetUpdateNotifier,
) : WidgetUpdater {

    override suspend fun refreshAll() {
        try {
            val terminal = repository.getStoreBooks(null, 0, 9, "createdDate,desc")
                .first { it !is DataResource.Loading }
            if (terminal is DataResource.Success) {
                cache.put(terminal.data.content.map { it.toWidgetUiBook() })
            }
            // Error → keep stale cache (do not throw)
        } finally {
            notifier.notifyAllWidgets()
        }
    }
}
```

- [ ] **Step 7: 테스트 통과 확인**

Run: `./gradlew :widget:testDebugUnitTest --tests "*WidgetUpdaterImplTest"`
Expected: 2 tests passed.

- [ ] **Step 8: 커밋**
```bash
git add widget/src/main/kotlin/project/side/widget/data/ widget/src/test/kotlin/project/side/widget/data/
git commit -m "Add WidgetUpdater facade with stale-while-revalidate semantics"
```

---

## Task 8: Vector drawables + colors.xml + widget_info xml

**Goal:** 위젯 메타데이터 XML과 자산 추가. spec §13의 SVG를 Vector Drawable로 변환.

**Files:**
- Create: `widget/src/main/res/drawable/ic_book_heart.xml`
- Create: `widget/src/main/res/drawable/ic_widget_refresh.xml`
- Create: `widget/src/main/res/values/colors.xml`
- Create: `widget/src/main/res/xml/widget_small_info.xml`
- Create: `widget/src/main/res/xml/widget_medium_info.xml`
- Create: `widget/src/main/res/xml/widget_large_info.xml`

- [ ] **Step 1: colors.xml 작성**

`widget/src/main/res/values/colors.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="widget_white_bg">#F6F9FF</color>
    <color name="widget_blue_bg">#010196</color>
    <color name="widget_text_dark">#333333</color>
    <color name="widget_text_light">#FFFFFF</color>
    <color name="widget_accent_blue">#010196</color>
    <color name="widget_dummy_text">#A7A7A7</color>
    <color name="widget_indicator_active_dark">#747474</color>
    <color name="widget_indicator_inactive_dark">#D9D9D9</color>
    <color name="widget_indicator_active_light">#FFFFFF</color>
    <color name="widget_indicator_inactive_light">#80FFFFFF</color>
    <color name="widget_l_header_bar">#D4D4D4</color>
</resources>
```

- [ ] **Step 2: ic_book_heart.xml (책 하트 아이콘)**

spec §13.1의 SVG를 Vector Drawable로 변환. 22×22 viewport, fill을 tint로 분리:

`widget/src/main/res/drawable/ic_book_heart.xml`:
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="22dp"
    android:height="22dp"
    android:viewportWidth="22"
    android:viewportHeight="22"
    android:tint="?attr/colorControlNormal">
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M19.2487,15.5834V14.6667H20.1654V1.83341H19.2487V0.916748L3.66536,0.916748V1.83341H2.7487V2.75008H1.83203L1.83203,19.2501H2.7487V20.1667H3.66536V21.0834H19.2487V20.1667H20.1654V19.2501H19.2487V18.3334H18.332V15.5834H19.2487ZM16.4987,19.2501H5.4987V18.3334H4.58203V16.5001H5.4987V15.5834H16.4987V19.2501ZM16.4987,8.25008H15.582V9.16675H14.6654V10.0834H13.7487V11.0001H12.832V11.9167H11.9154V12.8334H10.9987V11.9167H10.082V11.0001H9.16536V10.0834H8.2487V9.16675H7.33203L7.33203,8.25008H6.41536V5.50008H7.33203V4.58341H10.082V5.50008H10.9987V6.41675H11.9154V5.50008H12.832V4.58341L15.582,4.58341V5.50008H16.4987V8.25008Z" />
</vector>
```

- [ ] **Step 3: ic_widget_refresh.xml (새로고침)**

spec §13.2 변환 (12×12, 두 path):

`widget/src/main/res/drawable/ic_widget_refresh.xml`:
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="12dp"
    android:height="12dp"
    android:viewportWidth="12"
    android:viewportHeight="12"
    android:tint="?attr/colorControlNormal">
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M11.5,7V7.5H11V8.5H10.5V9.5H10V10H9.5V10.5H8.5V11H7.5V11.5H4.5V11H3.5V10.5H2.5V10H1.5V10.5H1V11H0.5V7H4.5V7.5H4V8H3.5V9H4V9.5H5V10H7V9.5H8V9H8.5V8.5H9V7.5H9.5V7H11.5Z" />
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M11.5,1V5H7.5V4.5H8V4H8.5V3H8V2.5H7V2H5V2.5H4V3H3.5V3.5H3V4.5H2.5V5H0.5V4.5H1V3.5H1.5V2.5H2V2H2.5V1.5H3.5V1H4.5V0.5H7.5V1H8.5V1.5H9.5V2H10.5V1.5H11V1H11.5Z" />
</vector>
```

- [ ] **Step 4: widget_small_info.xml**

`widget/src/main/res/xml/widget_small_info.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:configure="project.side.widget.ui.WidgetConfigurationActivity"
    android:initialLayout="@layout/widget_loading"
    android:minWidth="158dp"
    android:minHeight="158dp"
    android:resizeMode="none"
    android:targetCellWidth="2"
    android:targetCellHeight="2"
    android:updatePeriodMillis="0"
    android:widgetCategory="home_screen" />
```

- [ ] **Step 5: widget_medium_info.xml**

`widget/src/main/res/xml/widget_medium_info.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:configure="project.side.widget.ui.WidgetConfigurationActivity"
    android:initialLayout="@layout/widget_loading"
    android:minWidth="338dp"
    android:minHeight="158dp"
    android:resizeMode="none"
    android:targetCellWidth="4"
    android:targetCellHeight="2"
    android:updatePeriodMillis="0"
    android:widgetCategory="home_screen" />
```

- [ ] **Step 6: widget_large_info.xml**

`widget/src/main/res/xml/widget_large_info.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<appwidget-provider xmlns:android="http://schemas.android.com/apk/res/android"
    android:configure="project.side.widget.ui.WidgetConfigurationActivity"
    android:initialLayout="@layout/widget_loading"
    android:minWidth="338dp"
    android:minHeight="354dp"
    android:resizeMode="none"
    android:targetCellWidth="4"
    android:targetCellHeight="4"
    android:updatePeriodMillis="0"
    android:widgetCategory="home_screen" />
```

- [ ] **Step 7: 임시 widget_loading 레이아웃 (Glance가 첫 composition 전 표시)**

`widget/src/main/res/layout/widget_loading.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/widget_white_bg" />
```

- [ ] **Step 8: 빌드 통과 확인**

Run: `./gradlew :widget:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 9: 커밋**
```bash
git add widget/src/main/res/
git commit -m "Add widget XML metadata, colors, and vector drawables"
```

---

## Task 9: Hilt module + DataStore providers

**Goal:** widget 전용 DataStore 인스턴스 2개(cache, prefs)와 binding 구성.

**Files:**
- Create: `widget/src/main/kotlin/project/side/widget/di/WidgetModule.kt`

- [ ] **Step 1: WidgetModule.kt 작성**

`widget/src/main/kotlin/project/side/widget/di/WidgetModule.kt`:
```kotlin
package project.side.widget.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import project.side.widget.data.GlanceWidgetUpdateNotifier
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetPreferences
import project.side.widget.data.WidgetUpdateNotifier
import project.side.widget.data.WidgetUpdater
import project.side.widget.data.WidgetUpdaterImpl

@Qualifier @Retention(AnnotationRetention.BINARY) annotation class WidgetCacheStore
@Qualifier @Retention(AnnotationRetention.BINARY) annotation class WidgetPrefsStore

private val Context.widgetCacheStore by preferencesDataStore(name = "widget_cache")
private val Context.widgetPrefsStore by preferencesDataStore(name = "widget_prefs")

@Module
@InstallIn(SingletonComponent::class)
object WidgetProvideModule {
    @Provides @Singleton @WidgetCacheStore
    fun provideWidgetCacheStore(@ApplicationContext ctx: Context): DataStore<Preferences> =
        ctx.widgetCacheStore

    @Provides @Singleton @WidgetPrefsStore
    fun provideWidgetPrefsStore(@ApplicationContext ctx: Context): DataStore<Preferences> =
        ctx.widgetPrefsStore

    @Provides @Singleton
    fun provideWidgetCache(@WidgetCacheStore store: DataStore<Preferences>): WidgetCache =
        WidgetCache(store)

    @Provides @Singleton
    fun provideWidgetPreferences(@WidgetPrefsStore store: DataStore<Preferences>): WidgetPreferences =
        WidgetPreferences(store)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class WidgetBindModule {
    @Binds @Singleton
    abstract fun bindWidgetUpdater(impl: WidgetUpdaterImpl): WidgetUpdater

    @Binds @Singleton
    abstract fun bindWidgetUpdateNotifier(impl: GlanceWidgetUpdateNotifier): WidgetUpdateNotifier
}
```

- [ ] **Step 2: GlanceWidgetUpdateNotifier 스텁 (다음 task에서 채움)**

`widget/src/main/kotlin/project/side/widget/data/GlanceWidgetUpdateNotifier.kt`:
```kotlin
package project.side.widget.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlanceWidgetUpdateNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : WidgetUpdateNotifier {
    override suspend fun notifyAllWidgets() {
        // Filled in Task 10/13/16 once each GlanceAppWidget exists.
        // Each widget calls .updateAll(context).
    }
}
```

- [ ] **Step 3: 빌드 통과 확인**

Run: `./gradlew :widget:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: 커밋**
```bash
git add widget/src/main/kotlin/project/side/widget/di/ widget/src/main/kotlin/project/side/widget/data/GlanceWidgetUpdateNotifier.kt
git commit -m "Wire Hilt DI for widget DataStore and updater"
```

---

## Task 10: Glance 공통 컴포넌트 + Theme

**Goal:** S/M/L 모두에서 쓰는 책 아이콘, 새로고침, empty state, 페이지 인디케이터 등을 미리 작성.

**Files:**
- Create: `widget/src/main/kotlin/project/side/widget/glance/theme/WidgetTheme.kt`
- Create: `widget/src/main/kotlin/project/side/widget/glance/components/BookHeartIcon.kt`
- Create: `widget/src/main/kotlin/project/side/widget/glance/components/RefreshIcon.kt`
- Create: `widget/src/main/kotlin/project/side/widget/glance/components/EmptyState.kt`
- Create: `widget/src/main/kotlin/project/side/widget/glance/components/PageIndicator.kt`

- [ ] **Step 1: WidgetTheme.kt — color resolver**

```kotlin
package project.side.widget.glance.theme

import androidx.glance.color.ColorProvider
import androidx.glance.unit.ColorProvider
import androidx.compose.ui.graphics.Color
import project.side.widget.theme.ColorVariant

data class WidgetColors(
    val background: Color,
    val text: Color,
    val accent: Color,
    val dummyText: Color,
    val indicatorActive: Color,
    val indicatorInactive: Color,
    val refreshTint: Color,
    val refreshAlpha: Float,
)

fun colorsFor(variant: ColorVariant): WidgetColors = when (variant) {
    ColorVariant.WHITE -> WidgetColors(
        background = Color(0xFFF6F9FF),
        text = Color(0xFF333333),
        accent = Color(0xFF010196),
        dummyText = Color(0xFFA7A7A7),
        indicatorActive = Color(0xFF747474),
        indicatorInactive = Color(0xFFD9D9D9),
        refreshTint = Color(0xFF333333),
        refreshAlpha = 0.6f,
    )
    ColorVariant.BLUE -> WidgetColors(
        background = Color(0xFF010196),
        text = Color(0xFFFFFFFF),
        accent = Color(0xFFFFFFFF),
        dummyText = Color(0xFFFFFFFF).copy(alpha = 0.6f),
        indicatorActive = Color(0xFFFFFFFF),
        indicatorInactive = Color(0xFFFFFFFF).copy(alpha = 0.3f),
        refreshTint = Color(0xFFFFFFFF),
        refreshAlpha = 0.6f,
    )
}
```

- [ ] **Step 2: BookHeartIcon.kt**

```kotlin
package project.side.widget.glance.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.color.ColorProvider
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import project.side.widget.R

@Composable
fun BookHeartIcon(tint: Color, sizeDp: Int = 16, modifier: GlanceModifier = GlanceModifier) {
    Image(
        provider = ImageProvider(R.drawable.ic_book_heart),
        contentDescription = null,
        colorFilter = androidx.glance.ColorFilter.tint(ColorProvider(tint)),
        modifier = modifier.size(androidx.compose.ui.unit.dp(sizeDp.toFloat()))
    )
}
```

> 참고: Glance의 `dp()` 호출 형식은 1.1+에서 `androidx.compose.ui.unit.dp` 확장 사용. 컴파일 오류 발생 시 `import androidx.glance.layout.size` 와 `androidx.compose.ui.unit.dp.Dp` 변환 패턴으로 조정.

- [ ] **Step 3: RefreshIcon.kt**

```kotlin
package project.side.widget.glance.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.color.ColorProvider
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider
import project.side.widget.R

@Composable
fun RefreshIcon(
    tint: Color,
    onClick: Action,
    sizeDp: Int = 16,
    modifier: GlanceModifier = GlanceModifier,
) {
    Image(
        provider = ImageProvider(R.drawable.ic_widget_refresh),
        contentDescription = "새로고침",
        colorFilter = ColorFilter.tint(ColorProvider(tint)),
        modifier = modifier
            .size(androidx.compose.ui.unit.dp(sizeDp.toFloat()))
            .clickable(onClick),
    )
}
```

- [ ] **Step 4: EmptyState.kt**

```kotlin
package project.side.widget.glance.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

@Composable
fun EmptyState(
    textColor: Color,
    onClick: Action,
    fontSizeSp: Int = 14,
) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(16.dp())
            .clickable(onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "읽고 싶은 책을 추가해 보세요 !",
            style = TextStyle(
                color = ColorProvider(textColor),
                fontSize = androidx.compose.ui.unit.sp(fontSizeSp.toFloat()),
                fontWeight = FontWeight.Normal,
            )
        )
    }
}

private fun Int.dp() = androidx.compose.ui.unit.dp(this.toFloat())
```

- [ ] **Step 5: PageIndicator.kt**

```kotlin
package project.side.widget.glance.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.glance.GlanceModifier
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.unit.ColorProvider
import project.side.widget.R

@Composable
fun PageIndicator(
    total: Int,
    current: Int,
    activeColor: Color,
    inactiveColor: Color,
    onPrev: Action,
    onNext: Action,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        ArrowButton(label = "◀", color = activeColor, onClick = onPrev)
        Spacer(modifier = GlanceModifier.width(androidx.compose.ui.unit.dp(8f)))
        repeat(total) { idx ->
            Box(
                modifier = GlanceModifier
                    .size(androidx.compose.ui.unit.dp(6f))
                    .cornerRadius(androidx.compose.ui.unit.dp(3f))
                    .background(ColorProvider(if (idx == current) activeColor else inactiveColor))
            ) {}
            Spacer(modifier = GlanceModifier.width(androidx.compose.ui.unit.dp(2f)))
        }
        Spacer(modifier = GlanceModifier.width(androidx.compose.ui.unit.dp(8f)))
        ArrowButton(label = "▶", color = activeColor, onClick = onNext)
    }
}

@Composable
private fun ArrowButton(label: String, color: Color, onClick: Action) {
    androidx.glance.text.Text(
        text = label,
        style = androidx.glance.text.TextStyle(
            color = ColorProvider(color),
            fontSize = androidx.compose.ui.unit.sp(14f),
        ),
        modifier = GlanceModifier
            .padding(horizontal = androidx.compose.ui.unit.dp(4f))
            .clickable(onClick),
    )
}
```

- [ ] **Step 6: 빌드 확인**

Run: `./gradlew :widget:assembleDebug`
Expected: BUILD SUCCESSFUL

> 컴파일 오류가 나면 import 경로(`androidx.glance.unit.ColorProvider` vs `androidx.glance.color.ColorProvider`)와 dp/sp 헬퍼 형식을 Glance 1.1.1 공식 문서로 보정.

- [ ] **Step 7: 커밋**
```bash
git add widget/src/main/kotlin/project/side/widget/glance/
git commit -m "Add shared Glance components and color theme"
```

---

## Task 11: Intent 헬퍼 + 공통 액션 (OpenBook, OpenApp, Refresh, Prev, Next)

**Goal:** 위젯에서 만들어 보내는 Intent 표준화 + ActionRunCallback 4개 작성.

**Files:**
- Create: `widget/src/main/kotlin/project/side/widget/intent/WidgetIntents.kt`
- Create: `widget/src/main/kotlin/project/side/widget/action/OpenBookAction.kt`
- Create: `widget/src/main/kotlin/project/side/widget/action/OpenAppAction.kt`
- Create: `widget/src/main/kotlin/project/side/widget/action/RefreshSmallAction.kt`
- Create: `widget/src/main/kotlin/project/side/widget/action/PrevAction.kt`
- Create: `widget/src/main/kotlin/project/side/widget/action/NextAction.kt`
- Create: `widget/src/main/kotlin/project/side/widget/state/WidgetState.kt`

- [ ] **Step 1: WidgetIntents.kt**

```kotlin
package project.side.widget.intent

import android.content.ComponentName
import android.content.Context
import android.content.Intent

object WidgetIntents {
    const val MAIN_ACTIVITY_CLASS = "project.side.ui.MainActivity"
    const val EXTRA_WIDGET_TARGET = "widget_target"
    const val EXTRA_MYBOOK_ID = "mybook_id"
    const val TARGET_BOOK = "book"
    const val TARGET_HOME = "home"

    fun openBook(context: Context, mybookId: Int): Intent =
        Intent().apply {
            component = ComponentName(context.packageName, MAIN_ACTIVITY_CLASS)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(EXTRA_WIDGET_TARGET, TARGET_BOOK)
            putExtra(EXTRA_MYBOOK_ID, mybookId)
        }

    fun openApp(context: Context): Intent =
        Intent().apply {
            component = ComponentName(context.packageName, MAIN_ACTIVITY_CLASS)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(EXTRA_WIDGET_TARGET, TARGET_HOME)
        }
}
```

- [ ] **Step 2: WidgetState.kt — Glance state 키 정의**

```kotlin
package project.side.widget.state

import androidx.datastore.preferences.core.intPreferencesKey

object WidgetStateKeys {
    val SMALL_CURRENT_INDEX = intPreferencesKey("small_current_index")
    val MEDIUM_CURRENT_INDEX = intPreferencesKey("medium_current_index")
}
```

- [ ] **Step 3: OpenBookAction.kt**

```kotlin
package project.side.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import project.side.widget.intent.WidgetIntents

class OpenBookAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val id = parameters[mybookIdKey] ?: return
        val intent = WidgetIntents.openBook(context, id)
        context.startActivity(intent)
    }

    companion object {
        val mybookIdKey = ActionParameters.Key<Int>("mybook_id")
    }
}
```

- [ ] **Step 4: OpenAppAction.kt**

```kotlin
package project.side.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import project.side.widget.intent.WidgetIntents

class OpenAppAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        context.startActivity(WidgetIntents.openApp(context))
    }
}
```

- [ ] **Step 5: RefreshSmallAction.kt + 헬퍼 로직 분리 (TDD)**

`widget/src/main/kotlin/project/side/widget/action/RefreshLogic.kt`:
```kotlin
package project.side.widget.action

import project.side.widget.data.WidgetUiBook

object RefreshLogic {
    /** 현재 책을 제외한 책 중 random index 반환. 1권뿐이면 -1, 0권이면 -2. */
    fun pickNextIndex(books: List<WidgetUiBook>, currentIndex: Int, randomInt: (Int) -> Int): Int {
        if (books.isEmpty()) return -2
        if (books.size == 1) return -1
        val candidates = books.indices.filter { it != currentIndex }
        return candidates[randomInt(candidates.size)]
    }
}
```

`widget/src/test/kotlin/project/side/widget/action/RefreshLogicTest.kt`:
```kotlin
package project.side.widget.action

import org.junit.Assert.assertEquals
import org.junit.Test
import project.side.widget.data.WidgetUiBook

class RefreshLogicTest {
    private fun books(n: Int) = (1..n).map { WidgetUiBook(it, "t$it", null, "2026-05-10") }

    @Test fun `empty returns -2`() =
        assertEquals(-2, RefreshLogic.pickNextIndex(emptyList(), 0) { 0 })

    @Test fun `single returns -1`() =
        assertEquals(-1, RefreshLogic.pickNextIndex(books(1), 0) { 0 })

    @Test fun `two excludes current`() =
        assertEquals(1, RefreshLogic.pickNextIndex(books(2), 0) { 0 })

    @Test fun `nine excludes current and uses random for selection`() =
        assertEquals(5, RefreshLogic.pickNextIndex(books(9), 4) { 4 })
}
```

`RefreshSmallAction.kt`:
```kotlin
package project.side.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlin.random.Random
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetUpdater
import project.side.widget.glance.SmallWidget
import project.side.widget.state.WidgetStateKeys

class RefreshSmallAction : ActionCallback {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface RefreshDeps {
        fun cache(): WidgetCache
        fun updater(): WidgetUpdater
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val deps = EntryPointAccessors.fromApplication(context, RefreshDeps::class.java)
        deps.updater().refreshAll()  // server + cache + notify
        val books = deps.cache().read()
        val currentIndex = currentIndexFromState(context, glanceId)
        val next = RefreshLogic.pickNextIndex(books, currentIndex) { Random.nextInt(it) }
        if (next < 0) {
            // -2: empty, leave state alone (composable will render empty)
            // -1: single book, keep current
        } else {
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[WidgetStateKeys.SMALL_CURRENT_INDEX] = next
            }
        }
        SmallWidget().update(context, glanceId)
    }

    private suspend fun currentIndexFromState(context: Context, glanceId: GlanceId): Int {
        var idx = 0
        updateAppWidgetState(context, glanceId) { prefs ->
            idx = prefs[WidgetStateKeys.SMALL_CURRENT_INDEX] ?: 0
        }
        return idx
    }
}
```

- [ ] **Step 6: PrevAction.kt / NextAction.kt**

`PrevAction.kt`:
```kotlin
package project.side.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import project.side.widget.data.WidgetCache
import project.side.widget.glance.MediumWidget
import project.side.widget.state.WidgetStateKeys

class PrevAction : ActionCallback {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PrevDeps { fun cache(): WidgetCache }

    override suspend fun onAction(
        context: Context, glanceId: GlanceId, parameters: ActionParameters,
    ) {
        val total = EntryPointAccessors.fromApplication(context, PrevDeps::class.java)
            .cache().read().take(5).size
        if (total == 0) return
        updateAppWidgetState(context, glanceId) { prefs ->
            val current = prefs[WidgetStateKeys.MEDIUM_CURRENT_INDEX] ?: 0
            prefs[WidgetStateKeys.MEDIUM_CURRENT_INDEX] = ((current - 1) % total + total) % total
        }
        MediumWidget().update(context, glanceId)
    }
}
```

`NextAction.kt`:
```kotlin
package project.side.widget.action

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import project.side.widget.data.WidgetCache
import project.side.widget.glance.MediumWidget
import project.side.widget.state.WidgetStateKeys

class NextAction : ActionCallback {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface NextDeps { fun cache(): WidgetCache }

    override suspend fun onAction(
        context: Context, glanceId: GlanceId, parameters: ActionParameters,
    ) {
        val total = EntryPointAccessors.fromApplication(context, NextDeps::class.java)
            .cache().read().take(5).size
        if (total == 0) return
        updateAppWidgetState(context, glanceId) { prefs ->
            val current = prefs[WidgetStateKeys.MEDIUM_CURRENT_INDEX] ?: 0
            prefs[WidgetStateKeys.MEDIUM_CURRENT_INDEX] = (current + 1) % total
        }
        MediumWidget().update(context, glanceId)
    }
}
```

- [ ] **Step 7: 빌드 + RefreshLogic 테스트 확인**

Run: `./gradlew :widget:testDebugUnitTest --tests "*RefreshLogicTest"`
Expected: 4 tests passed.

Run: `./gradlew :widget:assembleDebug`
Expected: BUILD SUCCESSFUL (SmallWidget/MediumWidget 클래스 참조는 다음 task에서 생성되므로 이번 step은 컴파일이 멈출 수 있음).

> **빌드 실패 시 처리**: SmallWidget / MediumWidget 클래스가 다음 task들에서 생긴다. 이 step에서는 두 클래스를 빈 stub으로 잠시 추가:
> ```kotlin
> // widget/src/main/kotlin/project/side/widget/glance/SmallWidget.kt
> package project.side.widget.glance
> import androidx.glance.appwidget.GlanceAppWidget
> import androidx.glance.GlanceId
> import android.content.Context
> import androidx.compose.runtime.Composable
> class SmallWidget : GlanceAppWidget() {
>     override suspend fun provideGlance(context: Context, id: GlanceId) {}
> }
> ```
> MediumWidget도 동일 stub. Task 12/14에서 본 내용으로 대체.

- [ ] **Step 8: 커밋**
```bash
git add widget/src/main/kotlin/project/side/widget/action/ widget/src/main/kotlin/project/side/widget/intent/ widget/src/main/kotlin/project/side/widget/state/ widget/src/test/kotlin/project/side/widget/action/
git commit -m "Add widget action callbacks and intent helpers"
```

---

## Task 12: SmallWidget Composable + Receiver

**Goal:** S 위젯의 Glance composable과 receiver 작성.

**Files:**
- Modify: `widget/src/main/kotlin/project/side/widget/glance/SmallWidget.kt`
- Create: `widget/src/main/kotlin/project/side/widget/receiver/SmallWidgetReceiver.kt`

- [ ] **Step 1: SmallWidget.kt 본 구현**

```kotlin
package project.side.widget.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.LocalGlanceId
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import project.side.widget.action.OpenBookAction
import project.side.widget.action.RefreshSmallAction
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetPreferences
import project.side.widget.domain.DateLabel
import project.side.widget.glance.components.BookHeartIcon
import project.side.widget.glance.components.EmptyState
import project.side.widget.glance.components.RefreshIcon
import project.side.widget.glance.theme.colorsFor
import project.side.widget.state.WidgetStateKeys
import project.side.widget.theme.ColorVariant

class SmallWidget : GlanceAppWidget() {

    override val sizeMode: SizeMode = SizeMode.Single

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SmallDeps {
        fun cache(): WidgetCache
        fun prefs(): WidgetPreferences
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val deps = EntryPointAccessors.fromApplication(context, SmallDeps::class.java)
        val books = deps.cache().read()
        val appWidgetId = androidx.glance.appwidget.GlanceAppWidgetManager(context)
            .getAppWidgetId(id)
        val variant = deps.prefs().colorFor(appWidgetId)
        provideContent { SmallContent(books = books, variant = variant) }
    }
}

@Composable
private fun SmallContent(books: List<project.side.widget.data.WidgetUiBook>, variant: ColorVariant) {
    val colors = colorsFor(variant)
    val state = androidx.glance.currentState<androidx.datastore.preferences.core.Preferences>()
    val currentIndex = state[WidgetStateKeys.SMALL_CURRENT_INDEX] ?: 0
    val safeIndex = if (books.isNotEmpty()) currentIndex.coerceIn(0, books.size - 1) else 0

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(colors.background))
            .cornerRadius(22.dp)
    ) {
        if (books.isEmpty()) {
            EmptyState(
                textColor = colors.text,
                onClick = androidx.glance.action.actionStartActivity(
                    project.side.widget.intent.WidgetIntents.openApp(LocalContext.current)
                ),
                fontSizeSp = 14,
            )
        } else {
            val book = books[safeIndex]
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .clickable(actionRunCallback<OpenBookAction>(
                        actionParametersOf(OpenBookAction.mybookIdKey to book.mybookId)
                    )),
            ) {
                Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    BookHeartIcon(tint = colors.accent, sizeDp = 16)
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    RefreshIcon(
                        tint = colors.refreshTint.copy(alpha = colors.refreshAlpha),
                        onClick = actionRunCallback<RefreshSmallAction>(),
                        sizeDp = 16,
                    )
                }
                Spacer(modifier = GlanceModifier.size(8.dp))
                Text(
                    text = book.title,
                    maxLines = 2,
                    style = TextStyle(
                        color = ColorProvider(colors.text),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                    ),
                    modifier = GlanceModifier.fillMaxWidth(),
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = DateLabel.format(book.createdDate),
                    style = TextStyle(
                        color = ColorProvider(colors.accent),
                        fontSize = 10.sp,
                        textAlign = TextAlign.End,
                    ),
                    modifier = GlanceModifier.fillMaxWidth(),
                )
            }
        }
    }
}
```

- [ ] **Step 2: SmallWidgetReceiver.kt**

```kotlin
package project.side.widget.receiver

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dagger.hilt.android.AndroidEntryPoint
import project.side.widget.glance.SmallWidget
import javax.inject.Inject
import project.side.widget.data.WidgetUpdater
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SmallWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SmallWidget()

    @Inject lateinit var updater: WidgetUpdater
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        scope.launch { updater.refreshAll() }
    }
}
```

- [ ] **Step 3: GlanceWidgetUpdateNotifier 채움**

`widget/src/main/kotlin/project/side/widget/data/GlanceWidgetUpdateNotifier.kt` 갱신:
```kotlin
package project.side.widget.data

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import project.side.widget.glance.LargeWidget
import project.side.widget.glance.MediumWidget
import project.side.widget.glance.SmallWidget

@Singleton
class GlanceWidgetUpdateNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : WidgetUpdateNotifier {
    override suspend fun notifyAllWidgets() {
        SmallWidget().updateAll(context)
        // MediumWidget / LargeWidget will be added in Tasks 13/15.
    }
}
```

> Task 13/15에서 Medium/LargeWidget 본 구현이 들어가면 이 함수의 `// MediumWidget...` 부분을 실제 호출로 바꾼다.

- [ ] **Step 4: AndroidManifest에 SmallWidgetReceiver 등록 (임시)**

`widget/src/main/AndroidManifest.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <receiver
            android:name="project.side.widget.receiver.SmallWidgetReceiver"
            android:exported="false">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>
            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/widget_small_info" />
        </receiver>
    </application>
</manifest>
```

- [ ] **Step 5: 빌드 확인**

Run: `./gradlew :widget:assembleDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 6: 커밋**
```bash
git add widget/src/main/kotlin/project/side/widget/glance/SmallWidget.kt widget/src/main/kotlin/project/side/widget/receiver/SmallWidgetReceiver.kt widget/src/main/kotlin/project/side/widget/data/GlanceWidgetUpdateNotifier.kt widget/src/main/AndroidManifest.xml
git commit -m "Implement SmallWidget composable and receiver"
```

---

## Task 13: MediumWidget Composable + Receiver

**Goal:** M 위젯 본 구현.

**Files:**
- Create/Modify: `widget/src/main/kotlin/project/side/widget/glance/MediumWidget.kt`
- Create: `widget/src/main/kotlin/project/side/widget/receiver/MediumWidgetReceiver.kt`
- Modify: `widget/src/main/AndroidManifest.xml`
- Modify: `widget/src/main/kotlin/project/side/widget/data/GlanceWidgetUpdateNotifier.kt`

- [ ] **Step 1: MediumWidget.kt 구현**

```kotlin
package project.side.widget.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import project.side.widget.action.NextAction
import project.side.widget.action.OpenBookAction
import project.side.widget.action.PrevAction
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetPreferences
import project.side.widget.data.WidgetUiBook
import project.side.widget.domain.DateLabel
import project.side.widget.glance.components.BookHeartIcon
import project.side.widget.glance.components.EmptyState
import project.side.widget.glance.components.PageIndicator
import project.side.widget.glance.theme.colorsFor
import project.side.widget.state.WidgetStateKeys
import project.side.widget.theme.ColorVariant

private const val MAX_PAGES = 5

class MediumWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MediumDeps {
        fun cache(): WidgetCache
        fun prefs(): WidgetPreferences
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val deps = EntryPointAccessors.fromApplication(context, MediumDeps::class.java)
        val books = deps.cache().read().take(MAX_PAGES)
        val appWidgetId = androidx.glance.appwidget.GlanceAppWidgetManager(context).getAppWidgetId(id)
        val variant = deps.prefs().colorFor(appWidgetId)
        provideContent { MediumContent(books, variant) }
    }
}

@Composable
private fun MediumContent(books: List<WidgetUiBook>, variant: ColorVariant) {
    val colors = colorsFor(variant)
    val state = androidx.glance.currentState<androidx.datastore.preferences.core.Preferences>()
    val rawIndex = state[WidgetStateKeys.MEDIUM_CURRENT_INDEX] ?: 0
    val total = books.size
    val current = if (total > 0) rawIndex.coerceIn(0, total - 1) else 0

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(colors.background))
            .cornerRadius(22.dp)
    ) {
        if (books.isEmpty()) {
            EmptyState(
                textColor = colors.text,
                onClick = actionRunCallback<project.side.widget.action.OpenAppAction>(),
                fontSizeSp = 14,
            )
        } else {
            val book = books[current]
            Column(
                modifier = GlanceModifier.fillMaxSize().padding(12.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BookHeartIcon(tint = colors.accent, sizeDp = 16)
                    Spacer(GlanceModifier.size(6.dp))
                    Text(
                        text = book.title,
                        maxLines = 1,
                        style = TextStyle(
                            color = ColorProvider(colors.text),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                        ),
                        modifier = GlanceModifier.defaultWeight().clickable(
                            actionRunCallback<OpenBookAction>(
                                actionParametersOf(OpenBookAction.mybookIdKey to book.mybookId)
                            )
                        ),
                    )
                }
                Spacer(GlanceModifier.size(8.dp))
                val reasonText = book.reason?.takeIf { it.isNotBlank() }
                Text(
                    text = reasonText ?: "읽고 싶은 이유를 추가해 주세요.",
                    maxLines = 3,
                    style = TextStyle(
                        color = ColorProvider(if (reasonText == null) colors.dummyText else colors.text),
                        fontSize = 12.sp,
                    ),
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .clickable(actionRunCallback<OpenBookAction>(
                            actionParametersOf(OpenBookAction.mybookIdKey to book.mybookId)
                        )),
                )
                Spacer(GlanceModifier.defaultWeight())
                Text(
                    text = DateLabel.formatDisplay(book.createdDate),
                    style = TextStyle(
                        color = ColorProvider(colors.accent),
                        fontSize = 12.sp,
                        textAlign = TextAlign.End,
                    ),
                    modifier = GlanceModifier.fillMaxWidth(),
                )
                Spacer(GlanceModifier.size(6.dp))
                Box(modifier = GlanceModifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    PageIndicator(
                        total = total,
                        current = current,
                        activeColor = colors.indicatorActive,
                        inactiveColor = colors.indicatorInactive,
                        onPrev = actionRunCallback<PrevAction>(),
                        onNext = actionRunCallback<NextAction>(),
                    )
                }
            }
        }
    }
}
```


- [ ] **Step 2: MediumWidgetReceiver.kt**

```kotlin
package project.side.widget.receiver

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import project.side.widget.data.WidgetUpdater
import project.side.widget.glance.MediumWidget

@AndroidEntryPoint
class MediumWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MediumWidget()

    @Inject lateinit var updater: WidgetUpdater
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        scope.launch { updater.refreshAll() }
    }
}
```

- [ ] **Step 3: AndroidManifest 수정 — Medium receiver 추가**

`<application>` 안에 추가:
```xml
<receiver
    android:name="project.side.widget.receiver.MediumWidgetReceiver"
    android:exported="false">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/widget_medium_info" />
</receiver>
```

- [ ] **Step 4: GlanceWidgetUpdateNotifier 갱신**

```kotlin
override suspend fun notifyAllWidgets() {
    SmallWidget().updateAll(context)
    MediumWidget().updateAll(context)
    // LargeWidget will be added in Task 15.
}
```

- [ ] **Step 5: 빌드 확인**

Run: `./gradlew :widget:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: 커밋**
```bash
git add widget/src/main/kotlin/project/side/widget/glance/MediumWidget.kt widget/src/main/kotlin/project/side/widget/receiver/MediumWidgetReceiver.kt widget/src/main/AndroidManifest.xml widget/src/main/kotlin/project/side/widget/data/GlanceWidgetUpdateNotifier.kt
git commit -m "Implement MediumWidget composable and receiver"
```

---

## Task 14: LargeWidget Composable + Receiver

**Goal:** L 위젯 본 구현 (헤더 + 9권 리스트).

**Files:**
- Create: `widget/src/main/kotlin/project/side/widget/glance/LargeWidget.kt`
- Create: `widget/src/main/kotlin/project/side/widget/receiver/LargeWidgetReceiver.kt`
- Modify: `widget/src/main/AndroidManifest.xml`
- Modify: `widget/src/main/kotlin/project/side/widget/data/GlanceWidgetUpdateNotifier.kt`

- [ ] **Step 1: LargeWidget.kt**

```kotlin
package project.side.widget.glance

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import project.side.widget.action.OpenAppAction
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetPreferences
import project.side.widget.data.WidgetUiBook
import project.side.widget.glance.components.BookHeartIcon
import project.side.widget.glance.theme.colorsFor
import project.side.widget.theme.ColorVariant
import androidx.compose.ui.graphics.Color

class LargeWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface LargeDeps {
        fun cache(): WidgetCache
        fun prefs(): WidgetPreferences
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val deps = EntryPointAccessors.fromApplication(context, LargeDeps::class.java)
        val books = deps.cache().read().take(9)
        val appWidgetId = androidx.glance.appwidget.GlanceAppWidgetManager(context).getAppWidgetId(id)
        val variant = deps.prefs().colorFor(appWidgetId)
        provideContent { LargeContent(books, variant) }
    }
}

@Composable
private fun LargeContent(books: List<WidgetUiBook>, variant: ColorVariant) {
    val colors = colorsFor(variant)
    val openApp = actionRunCallback<OpenAppAction>()
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(colors.background))
            .cornerRadius(22.dp)
            .clickable(openApp)
    ) {
        // Header bar
        Box(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(ColorProvider(headerBarColor(variant)))
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "BOOK NAME",
                style = TextStyle(
                    color = ColorProvider(if (variant == ColorVariant.WHITE) Color(0xFF333333) else Color(0xFF010196)),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                ),
            )
        }
        Spacer(GlanceModifier.size(4.dp))
        if (books.isEmpty()) {
            Box(
                modifier = GlanceModifier.fillMaxSize().padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "읽고 싶은 책을 추가해 보세요 !",
                    style = TextStyle(
                        color = ColorProvider(colors.text),
                        fontSize = 12.sp,
                    ),
                )
            }
        } else {
            LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                items(books, itemId = { it.mybookId.toLong() }) { book ->
                    Row(
                        modifier = GlanceModifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BookHeartIcon(tint = colors.accent, sizeDp = 14)
                        Spacer(GlanceModifier.size(8.dp))
                        Text(
                            text = book.title,
                            maxLines = 1,
                            style = TextStyle(
                                color = ColorProvider(colors.text),
                                fontSize = 14.sp,
                            ),
                        )
                    }
                }
            }
        }
    }
}

private fun headerBarColor(variant: ColorVariant): Color = when (variant) {
    ColorVariant.WHITE -> Color(0xFFD4D4D4)
    // L 파란 변형 헤더 색은 spec §14.2의 미해결 항목. 잠정값 흰색.
    ColorVariant.BLUE -> Color(0xFFFFFFFF)
}
```

- [ ] **Step 2: LargeWidgetReceiver.kt**

```kotlin
package project.side.widget.receiver

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import project.side.widget.data.WidgetUpdater
import project.side.widget.glance.LargeWidget

@AndroidEntryPoint
class LargeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LargeWidget()

    @Inject lateinit var updater: WidgetUpdater
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: android.appwidget.AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        scope.launch { updater.refreshAll() }
    }
}
```

- [ ] **Step 3: Manifest 수정 (Large receiver 추가)**

```xml
<receiver
    android:name="project.side.widget.receiver.LargeWidgetReceiver"
    android:exported="false">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/widget_large_info" />
</receiver>
```

- [ ] **Step 4: GlanceWidgetUpdateNotifier 마무리**

```kotlin
override suspend fun notifyAllWidgets() {
    SmallWidget().updateAll(context)
    MediumWidget().updateAll(context)
    LargeWidget().updateAll(context)
}
```

- [ ] **Step 5: 빌드 확인**

Run: `./gradlew :widget:assembleDebug`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: 커밋**
```bash
git add widget/src/main/kotlin/project/side/widget/glance/LargeWidget.kt widget/src/main/kotlin/project/side/widget/receiver/LargeWidgetReceiver.kt widget/src/main/AndroidManifest.xml widget/src/main/kotlin/project/side/widget/data/GlanceWidgetUpdateNotifier.kt
git commit -m "Implement LargeWidget composable and receiver"
```

---

## Task 15: WidgetConfigurationActivity (색상 선택)

**Goal:** 위젯 추가 시 색상 선택 UI. Compose 기반.

**Files:**
- Create: `widget/src/main/kotlin/project/side/widget/ui/WidgetConfigurationActivity.kt`
- Modify: `widget/src/main/AndroidManifest.xml`
- Modify: `widget/build.gradle.kts` (Compose deps 추가)

- [ ] **Step 1: build.gradle.kts에 Compose dep 추가**

`widget/build.gradle.kts` `dependencies`에 추가:
```kotlin
implementation(platform(libs.androidx.compose.bom))
implementation(libs.androidx.compose.ui)
implementation(libs.androidx.compose.material3)
implementation(libs.androidx.activity.compose)
```

- [ ] **Step 2: WidgetConfigurationActivity.kt**

```kotlin
package project.side.widget.ui

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch
import project.side.widget.data.WidgetPreferences
import project.side.widget.data.WidgetUpdater
import project.side.widget.theme.ColorVariant

@AndroidEntryPoint
class WidgetConfigurationActivity : ComponentActivity() {

    @Inject lateinit var prefs: WidgetPreferences
    @Inject lateinit var updater: WidgetUpdater

    private var appWidgetId: Int = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish(); return
        }

        setContent {
            MaterialTheme {
                ConfigurationScreen(
                    onConfirm = { variant ->
                        lifecycleScope.launch {
                            prefs.setColor(appWidgetId, variant)
                            updater.refreshAll()
                            val resultValue = Intent().putExtra(
                                AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId
                            )
                            setResult(Activity.RESULT_OK, resultValue)
                            finish()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ConfigurationScreen(onConfirm: (ColorVariant) -> Unit) {
    var selected by remember { mutableStateOf(ColorVariant.WHITE) }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("배경 색을 선택해 주세요", fontSize = 18.sp)
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly) {
            VariantCard(
                label = "흰색",
                background = Color(0xFFF6F9FF),
                textColor = Color(0xFF333333),
                selected = selected == ColorVariant.WHITE,
                onClick = { selected = ColorVariant.WHITE },
            )
            VariantCard(
                label = "파란색",
                background = Color(0xFF010196),
                textColor = Color.White,
                selected = selected == ColorVariant.BLUE,
                onClick = { selected = ColorVariant.BLUE },
            )
        }
        Spacer(Modifier.height(32.dp))
        Button(onClick = { onConfirm(selected) }, modifier = Modifier.fillMaxWidth()) {
            Text("완료")
        }
    }
}

@Composable
private fun VariantCard(
    label: String,
    background: Color,
    textColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(width = 120.dp, height = 120.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(background)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) Color(0xFF010196) else Color.LightGray,
                shape = RoundedCornerShape(22.dp),
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = textColor, fontSize = 16.sp)
    }
}
```

- [ ] **Step 3: AndroidManifest에 Configuration Activity 등록**

`<application>` 안에 추가:
```xml
<activity
    android:name="project.side.widget.ui.WidgetConfigurationActivity"
    android:exported="true"
    android:theme="@style/Theme.AppCompat.Light.NoActionBar">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_CONFIGURE" />
    </intent-filter>
</activity>
```

> Theme.AppCompat이 widget 모듈에 없으면 `android:theme="@android:style/Theme.Material.Light.NoActionBar"` 또는 ui 모듈의 `Theme.Ikdaman`을 의존하도록 변경. 실행 시 검증.

- [ ] **Step 4: 빌드 확인**

Run: `./gradlew :widget:assembleDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: 커밋**
```bash
git add widget/src/main/kotlin/project/side/widget/ui/WidgetConfigurationActivity.kt widget/src/main/AndroidManifest.xml widget/build.gradle.kts
git commit -m "Add WidgetConfigurationActivity for color variant selection"
```

---

## Task 16: app 모듈에 widget 의존성 추가 + Application 진입 검증

**Goal:** `app`에서 `:widget`을 의존하고, Hilt가 `widget` 모듈의 `@Module`을 발견하는지 확인.

**Files:**
- Modify: `app/build.gradle.kts`

- [ ] **Step 1: app/build.gradle.kts dependencies에 추가**

```kotlin
implementation(project(":widget"))
```

- [ ] **Step 2: 전체 앱 빌드**

Run: `./gradlew :app:assembleDebug`
Expected: BUILD SUCCESSFUL.

> 빌드 실패 시: Hilt 컴파일 에러는 보통 `widget` 모듈의 `@Module`/`@AndroidEntryPoint`가 IkdamanApplication의 컴파일 그래프에 포함되지 않아 발생. `IkdamanApplication`이 `@HiltAndroidApp`인지 확인하고, 모듈 의존이 정상 추가됐는지 확인.

- [ ] **Step 3: 커밋**
```bash
git add app/build.gradle.kts
git commit -m "Wire widget module into app build"
```

---

## Task 17: ui 모듈 MainActivity 인텐트 처리 + launchMode 변경

**Goal:** 위젯에서 보낸 Intent extras를 받아 NavController로 화면 이동.

**Files:**
- Modify: `ui/src/main/AndroidManifest.xml`
- Modify: `ui/src/main/java/project/side/ui/MainActivity.kt`

- [ ] **Step 1: ui Manifest에 launchMode 추가**

`ui/src/main/AndroidManifest.xml` `<activity>`에 `android:launchMode="singleTask"` 추가:
```xml
<activity
    android:name="project.side.ui.MainActivity"
    android:exported="true"
    android:label="@string/app_name"
    android:launchMode="singleTask"
    android:theme="@style/Theme.Ikdaman">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

- [ ] **Step 2: MainActivity 수정 — Intent extras 핸들러 + onNewIntent**

`ui/src/main/java/project/side/ui/MainActivity.kt`의 onCreate 직전에 다음 멤버 추가:
```kotlin
private val pendingTarget = androidx.compose.runtime.mutableStateOf<WidgetTarget?>(null)

private fun extractWidgetTarget(intent: android.content.Intent?): WidgetTarget? {
    if (intent == null) return null
    val target = intent.getStringExtra("widget_target") ?: return null
    return when (target) {
        "book" -> WidgetTarget.Book(intent.getIntExtra("mybook_id", -1))
        "home" -> WidgetTarget.Home
        else -> null
    }
}

override fun onNewIntent(intent: android.content.Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    extractWidgetTarget(intent)?.let { pendingTarget.value = it }
}

sealed class WidgetTarget {
    data class Book(val mybookId: Int) : WidgetTarget()
    data object Home : WidgetTarget()
}
```

`onCreate` 도입부에 (super.onCreate 다음):
```kotlin
pendingTarget.value = extractWidgetTarget(intent)
```

`setContent { IkdamanTheme { ... } }` 안 NavHost 직전에 추가:
```kotlin
val target by pendingTarget
LaunchedEffect(target) {
    val t = target ?: return@LaunchedEffect
    when (t) {
        is WidgetTarget.Book -> if (t.mybookId != -1) {
            navController.navigate("BookInfo/${t.mybookId}")
        }
        WidgetTarget.Home -> navController.navigate(MAIN_ROUTE) {
            popUpTo(MAIN_ROUTE) { inclusive = true }
        }
    }
    pendingTarget.value = null
}
```

> 주의: 현재 NavHost에 `BookInfo/{mybookId}?description={description}` 라우트의 composable 등록을 직접 본 코드에 추가해야 할 수 있음. 검색해서 어디에 정의됐는지 확인 — 없다면 BookInfoScreen이 MainScreen 안의 sub-NavHost일 가능성. 그 경우 위젯에서 들어올 때 MainScreen → BookInfo로 가는 별도 진입 path를 만들어야 한다 (별도 task).

- [ ] **Step 3: BookInfo 라우트 진입 가능성 검증**

```bash
grep -rn "BOOK_INFO_ROUTE\|BookInfo/" --include="*.kt" ui/src/main 2>/dev/null
```

Expected: BookInfoScreen이 어디서 NavHost composable로 등록됐는지 확인.

`ui/src/main/java/project/side/ui/screen/BookInfoScreen.kt`가 있다면 그 위치의 NavHost에 등록되어 있을 것. 등록 위치가 MainScreen 내부의 sub-graph라면 위젯 진입 시 root NavHost에서 `MAIN_ROUTE` → 그 다음 `BOOK_INFO_ROUTE`로 두 단계 navigate해야 함.

이 경우 Step 2의 `WidgetTarget.Book` 분기를:
```kotlin
is WidgetTarget.Book -> if (t.mybookId != -1) {
    navController.navigate(MAIN_ROUTE) {
        popUpTo(MAIN_ROUTE) { inclusive = true }
    }
    // delegate to MainScreen's nav: 추가 신호 전달은 MainScreen 측 핸들러 필요
}
```
로 변경하고 MainScreen으로 추가 인자 전달 메커니즘을 별도로 추가. 구현자 판단.

- [ ] **Step 4: 빌드 확인**

Run: `./gradlew :ui:assembleDebug && ./gradlew :app:assembleDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: 커밋**
```bash
git add ui/src/main/AndroidManifest.xml ui/src/main/java/project/side/ui/MainActivity.kt
git commit -m "Handle widget intent extras in MainActivity with singleTask launch"
```

---

## Task 18: 앱 레벨 갱신 hook — UseCase 성공 시 WidgetUpdater 호출

**Goal:** 책 추가/삭제/수정/상태변경 직후 위젯이 즉시 갱신되도록.

**Files:**
- Modify: `presentation/src/main/java/project/side/presentation/viewmodel/MainViewModel.kt` (또는 관련 ViewModel)
- 또는: data 레이어 Repository impl에서 직접 hook (선호: ViewModel에서 명시적으로)

- [ ] **Step 1: MainViewModel + 관련 ViewModel 위치 확인**

```bash
grep -rn "saveMyBook\|deleteMyBook\|updateMyBook\|updateReadingStatus" --include="*.kt" presentation/ ui/ 2>/dev/null
```

Expected: ViewModel/Screen 위치 식별.

- [ ] **Step 2: WidgetUpdater 인터페이스를 presentation에서 사용하기 위해 의존 추가**

`presentation/build.gradle.kts` dependencies에 추가:
```kotlin
implementation(project(":widget"))
```

> 순환 의존 주의: widget이 domain만 의존하고 presentation은 widget을 의존 → OK.

- [ ] **Step 3: ViewModel 수정 예시 (Save 케이스)**

`presentation/src/main/java/project/side/presentation/viewmodel/{Relevant}ViewModel.kt`에서 saveMyBook 사용 위치를 찾아 다음 패턴 적용:
```kotlin
@HiltViewModel
class XxxViewModel @Inject constructor(
    private val saveManualBookInfoUseCase: SaveManualBookInfoUseCase,
    private val widgetUpdater: project.side.widget.data.WidgetUpdater,
) : ViewModel() {

    fun saveBook(info: ManualBookInfo) {
        viewModelScope.launch {
            saveManualBookInfoUseCase(info).collect { res ->
                if (res is DataResource.Success) {
                    widgetUpdater.refreshAll()
                }
                // 기존 처리 유지
            }
        }
    }
}
```

같은 패턴을 다음 호출 지점에 적용:
- `DeleteMyBookUseCase` 호출 후
- `UpdateMyBookUseCase` 호출 후
- `UpdateReadingStatusUseCase` 호출 후 (TODO ↔ 다른 상태 전환은 모두 위젯 영향)

- [ ] **Step 4: 빌드 확인**

Run: `./gradlew :presentation:assembleDebug && ./gradlew :app:assembleDebug`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: 커밋**
```bash
git add presentation/build.gradle.kts presentation/src/main/java/project/side/presentation/viewmodel/
git commit -m "Trigger WidgetUpdater on book mutation success in ViewModels"
```

---

## Task 19: 수동 테스트 + 마무리

**Goal:** 실제 디바이스/에뮬레이터에서 위젯 동작 검증.

- [ ] **Step 1: 디바이스/에뮬레이터에 설치**

Run: `./gradlew :app:installDebug`

- [ ] **Step 2: 수동 테스트 체크리스트 (spec §11 참조)**

다음을 확인하고 각 결과 기록:

1. **위젯 추가 흐름**: 홈 → 위젯 추가 → 모아북 S/M/L 카드가 모두 노출되는지
2. **Configuration 화면**: 색상 선택 → 완료 → 위젯이 선택한 색상으로 표시되는지
3. **Empty/미로그인**: 로그인 안 한 상태로 위젯 추가 → "읽고 싶은 책을 추가해 보세요 !" 표시 → 탭 시 앱 열림
4. **데이터 채우기**: 앱에서 책 1권 추가 → 위젯 즉시 반영
5. **S 새로고침**: 책 5권 추가한 상태에서 S 위젯 새로고침 → 다른 책으로 교체
6. **S 탭**: S 위젯 탭 → 해당 책 상세 화면으로 이동
7. **M 페이지 이동**: 좌/우 화살표 → 다른 책 표시. 5점 인디케이터 정상
8. **M 본문 탭**: 책 상세로 이동
9. **L 위젯**: 9권 표시. 어디 탭해도 앱 홈
10. **이유 없는 책**: M 위젯에 회색 "읽고 싶은 이유를 추가해 주세요." 표시
11. **101일 이상 된 책**: S 위젯에 "책에 먼지가 쌓였어요..." 표시
12. **앱에서 책 삭제**: 위젯 즉시 갱신 (현재 표시 책이 사라지면 다른 책으로 교체)
13. **회전/리사이즈**: 위젯 사이즈 변경 시 정상 표시 (resizeMode=none이라 변경 불가능해야 함 → 검증)
14. **다크 모드**: 시스템 다크 모드 전환 → 위젯 색상 영향 없음 확인

- [ ] **Step 3: 발견된 이슈를 spec/plan에 반영**

발견된 모든 회귀/이슈를 별도 commit으로 수정. 큰 이슈는 별도 task로 plan에 추가.

- [ ] **Step 4: 최종 커밋 + 출시 노트 초안 (선택)**

수동 테스트 완료 후 별도 PR 또는 단일 커밋으로 마무리.

---

## Self-Review Checklist (이 plan 자체 점검)

| 항목 | 상태 |
|---|---|
| spec §3 (S/M/L UI) → Tasks 12, 13, 14 |  Covered |
| spec §4 (데이터 모델) → Task 7 (StoreBookMapper) | Covered |
| spec §5 (모듈 구조) → Tasks 1, 2 | Covered |
| spec §6 (갱신 메커니즘) → Tasks 7, 18 | Covered |
| spec §7 (UI 사양) → Tasks 8, 10 | Covered |
| spec §8 (Configuration Activity) → Task 15 | Covered |
| spec §9 (Intent 처리) → Tasks 11, 17 | Covered |
| spec §10 (의존성) → Tasks 1, 2 | Covered |
| spec §11 (테스트) → Tasks 4, 5, 6, 7, 11 (RefreshLogic) + Task 19 manual | Covered |
| spec §12 (마이그레이션) → 기존 plan 이미 삭제됨 (이전 commit), launchMode 변경 Task 17 | Covered |
| spec §13 (SVG 자산) → Task 8 | Covered |
| spec §14 (미해결 항목) — 구현 시 확인 사항으로 plan 곳곳에 명시 | Covered |

**알려진 plan 한계 (구현자가 판단해야 할 것)**:
- Glance 1.1.1 API의 dp/sp helper 정확한 임포트 (코드는 일반 Compose dp/sp 가정 — Glance 공식 예시로 보정 가능)
- BookInfo 라우트가 root NavHost에 없을 수 있음 (Task 17 Step 3에서 검증, 별도 작업 필요할 수 있음)
- Theme.AppCompat 의존 (Task 15 Step 3) — widget 모듈에 없으면 다른 테마로 대체

---

## 실행 핸드오프

**Plan saved to `docs/superpowers/plans/2026-05-10-widget-implementation.md`. 두 가지 실행 옵션:**

1. **Subagent-Driven (recommended)** — 매 task마다 새 subagent dispatch, task 사이 review, 빠른 반복
2. **Inline Execution** — 이 세션에서 executing-plans 스킬로 batch 실행 (체크포인트 review)

**어느 쪽으로 진행하시겠어요?**
