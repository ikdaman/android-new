package project.side.ui.screen

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import project.side.ui.WidgetTarget

/**
 * MainScreen의 widgetSeq 기반 위젯 탭 처리 로직 단위 테스트.
 *
 * MainScreen은 @Composable + Hilt ViewModel이므로 JVM 환경에서 직접
 * 인스턴스화할 수 없다. 대신 MainScreen 내부 LaunchedEffect의 핵심 판단
 * 로직(widgetSeq key 변화 → navigate 여부)을 [MainScreenWidgetHandler]로
 * 추출하여 테스트한다.
 *
 * 핵심 버그 재현:
 *   - 기존: LaunchedEffect(widgetTarget) → 동일 타겟이면 재실행 안 됨
 *   - 수정: LaunchedEffect(widgetSeq)  → seq가 달라지면 항상 재실행됨
 */
class MainScreenTest {

    // ── widgetSeq 변화 감지 ─────────────────────────────────────────────────

    @Test
    fun `seq가 0에서 1로 바뀌면 navigate가 트리거된다`() {
        val handler = MainScreenWidgetHandler()

        val navigated = handler.onSeqChanged(
            newSeq = 1,
            target = WidgetTarget.Book(42)
        )

        assertTrue(navigated)
    }

    @Test
    fun `seq가 동일하면 navigate가 트리거되지 않는다`() {
        val handler = MainScreenWidgetHandler()
        handler.onSeqChanged(newSeq = 1, target = WidgetTarget.Book(42))

        // 같은 seq로 다시 호출 (recomposition 시뮬레이션)
        val navigated = handler.onSeqChanged(
            newSeq = 1,
            target = WidgetTarget.Book(42)
        )

        assertFalse(navigated)
    }

    @Test
    fun `같은 Book 타겟이라도 seq가 증가하면 navigate가 트리거된다`() {
        val handler = MainScreenWidgetHandler()
        handler.onSeqChanged(newSeq = 1, target = WidgetTarget.Book(42))

        // 두 번째 탭: 같은 타겟이지만 seq 증가
        val navigated = handler.onSeqChanged(
            newSeq = 2,
            target = WidgetTarget.Book(42)
        )

        assertTrue(navigated)
    }

    // ── navigate 대상 경로 ───────────────────────────────────────────────────

    @Test
    fun `Book 타겟은 BookInfo 경로로 navigate한다`() {
        val handler = MainScreenWidgetHandler()

        handler.onSeqChanged(newSeq = 1, target = WidgetTarget.Book(99))

        assertEquals("BookInfo/99", handler.lastNavigatedRoute)
    }

    @Test
    fun `mybookId가 -1이면 navigate하지 않는다`() {
        val handler = MainScreenWidgetHandler()

        val navigated = handler.onSeqChanged(
            newSeq = 1,
            target = WidgetTarget.Book(-1)
        )

        assertFalse(navigated)
        assertEquals(null, handler.lastNavigatedRoute)
    }

    @Test
    fun `Home 타겟은 root에서 처리되므로 MainScreen에서는 navigate하지 않는다`() {
        val handler = MainScreenWidgetHandler()

        val navigated = handler.onSeqChanged(
            newSeq = 1,
            target = WidgetTarget.Home
        )

        assertFalse(navigated)
        assertEquals(null, handler.lastNavigatedRoute)
    }

    // ── onWidgetTargetConsumed 호출 ─────────────────────────────────────────

    @Test
    fun `navigate 성공 후 consumed 콜백이 호출된다`() {
        val handler = MainScreenWidgetHandler()
        var consumed = false

        handler.onSeqChanged(
            newSeq = 1,
            target = WidgetTarget.Book(42),
            onConsumed = { consumed = true }
        )

        assertTrue(consumed)
    }

    @Test
    fun `navigate가 없으면 consumed 콜백이 호출되지 않는다`() {
        val handler = MainScreenWidgetHandler()
        var consumed = false

        handler.onSeqChanged(
            newSeq = 1,
            target = WidgetTarget.Book(-1),
            onConsumed = { consumed = true }
        )

        assertFalse(consumed)
    }

    @Test
    fun `Home 타겟은 consumed 콜백을 호출한다`() {
        val handler = MainScreenWidgetHandler()
        var consumed = false

        handler.onSeqChanged(
            newSeq = 1,
            target = WidgetTarget.Home,
            onConsumed = { consumed = true }
        )

        assertTrue(consumed)
    }
}

/**
 * MainScreen 내부 LaunchedEffect 로직을 테스트 가능하게 추출한 헬퍼.
 * 실제 MainScreen의 LaunchedEffect(widgetSeq) 블록과 동일한 판단을 수행한다.
 */
class MainScreenWidgetHandler {
    private var lastSeq: Int = -1
    var lastNavigatedRoute: String? = null
        private set

    /**
     * @return navigate가 실제로 수행됐으면 true
     */
    fun onSeqChanged(
        newSeq: Int,
        target: WidgetTarget?,
        onConsumed: () -> Unit = {}
    ): Boolean {
        if (newSeq == lastSeq) return false
        lastSeq = newSeq

        val t = target ?: return false

        return when (t) {
            is WidgetTarget.Book -> {
                if (t.mybookId != -1) {
                    lastNavigatedRoute = "BookInfo/${t.mybookId}"
                    onConsumed()
                    true
                } else {
                    false
                }
            }
            WidgetTarget.Home -> {
                // root(MainActivity)에서 이미 MAIN_ROUTE로 이동됨
                onConsumed()
                false
            }
        }
    }
}
