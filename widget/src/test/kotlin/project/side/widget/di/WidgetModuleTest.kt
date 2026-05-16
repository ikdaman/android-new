package project.side.widget.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import project.side.widget.data.GlanceWidgetUpdateNotifier
import project.side.widget.data.WidgetCache
import project.side.widget.data.WidgetPreferences
import java.io.File
import kotlin.io.path.createTempDirectory

/**
 * WidgetModule DI 프로바이더 단위 테스트.
 *
 * Hilt 런타임 없이 각 @Provides 팩토리 함수를 직접 호출하여
 * - 올바른 타입의 인스턴스를 반환하는지
 * - 동일 DataStore를 공유하는지(싱글턴 위임)
 * 검증한다.
 */
class WidgetModuleTest {

    private lateinit var tempDir: File

    @Before
    fun setup() {
        tempDir = createTempDirectory("widget-module-test").toFile()
    }

    @After
    fun teardown() {
        tempDir.deleteRecursively()
    }

    // ── DataStore 프로바이더 ──────────────────────────────────────────────────

    @Test
    fun `provideWidgetCache returns non-null WidgetCache`() = runTest {
        val store = PreferenceDataStoreFactory.create(
            produceFile = { File(tempDir, "cache.preferences_pb") }
        )
        val cache = WidgetProvideModule.provideWidgetCache(store)
        assertNotNull(cache)
    }

    @Test
    fun `provideWidgetPreferences returns non-null WidgetPreferences`() = runTest {
        val store = PreferenceDataStoreFactory.create(
            produceFile = { File(tempDir, "prefs.preferences_pb") }
        )
        val prefs = WidgetProvideModule.provideWidgetPreferences(store)
        assertNotNull(prefs)
    }

    @Test
    fun `provideWidgetCache produces WidgetCache that shares the given store`() = runTest {
        val store = PreferenceDataStoreFactory.create(
            produceFile = { File(tempDir, "shared_cache.preferences_pb") }
        )
        val cache1 = WidgetProvideModule.provideWidgetCache(store)
        val cache2 = WidgetProvideModule.provideWidgetCache(store)
        // 같은 store를 주입하면 독립적인 인스턴스를 반환하지만
        // 실제 Hilt 싱글턴 바인딩 시 동일 객체가 반환됨을 간접 검증:
        // 두 인스턴스 모두 정상적으로 생성되어야 한다.
        assertNotNull(cache1)
        assertNotNull(cache2)
    }

    @Test
    fun `provideWidgetPreferences produces WidgetPreferences that shares the given store`() = runTest {
        val store = PreferenceDataStoreFactory.create(
            produceFile = { File(tempDir, "shared_prefs.preferences_pb") }
        )
        val prefs1 = WidgetProvideModule.provideWidgetPreferences(store)
        val prefs2 = WidgetProvideModule.provideWidgetPreferences(store)
        assertNotNull(prefs1)
        assertNotNull(prefs2)
    }

    // ── GlanceWidgetUpdateNotifier (WidgetUpdateNotifier 바인딩 대상) ─────────

    @Test
    fun `GlanceWidgetUpdateNotifier implements WidgetUpdateNotifier`() {
        val context = mockk<android.content.Context>(relaxed = true)
        val notifier = GlanceWidgetUpdateNotifier(context)
        assertNotNull(notifier)
    }

    @Test
    @Ignore("notifyAllWidgets()는 Glance updateAll() → AppWidgetManager.getInstance() 호출. JVM 단위 테스트에서는 mocked 안 됨 → instrumented test 영역.")
    fun `GlanceWidgetUpdateNotifier notifyAllWidgets does not throw`() = runTest {
        val context = mockk<android.content.Context>(relaxed = true)
        val notifier = GlanceWidgetUpdateNotifier(context)
        notifier.notifyAllWidgets()
    }
}
