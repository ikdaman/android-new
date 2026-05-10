package project.side.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * [WidgetIntentParser] 단위 테스트.
 *
 * 핵심 검증:
 *  1. extractWidgetTarget: extras Map → WidgetTarget 변환 정확성
 *  2. setPending + seq: 동일 타겟 연속 탭 시 sequence 증가 보장
 *     (LaunchedEffect key로 seq 사용 → 재실행 보장)
 */
class WidgetIntentParserTest {

    private lateinit var parser: WidgetIntentParser

    @Before
    fun setUp() {
        parser = WidgetIntentParser()
    }

    // ── extractWidgetTarget ─────────────────────────────────────────────────

    @Test
    fun `widget_target book이면 Book 타겟을 반환한다`() {
        val result = parser.extractWidgetTarget(mapOf("widget_target" to "book", "mybook_id" to 42))
        assertEquals(WidgetTarget.Book(42), result)
    }

    @Test
    fun `widget_target home이면 Home 타겟을 반환한다`() {
        val result = parser.extractWidgetTarget(mapOf("widget_target" to "home"))
        assertEquals(WidgetTarget.Home, result)
    }

    @Test
    fun `extras가 비어있으면 null을 반환한다`() {
        assertNull(parser.extractWidgetTarget(emptyMap()))
    }

    @Test
    fun `알 수 없는 widget_target이면 null을 반환한다`() {
        assertNull(parser.extractWidgetTarget(mapOf("widget_target" to "unknown")))
    }

    @Test
    fun `mybook_id가 없으면 Book(-1)을 반환한다`() {
        val result = parser.extractWidgetTarget(mapOf("widget_target" to "book"))
        assertEquals(WidgetTarget.Book(-1), result)
    }

    // ── setPending / currentTarget ──────────────────────────────────────────

    @Test
    fun `setPending 후 currentTarget이 설정된다`() {
        parser.setPending(WidgetTarget.Book(99))
        assertEquals(WidgetTarget.Book(99), parser.currentTarget)
    }

    @Test
    fun `setPending null이면 currentTarget이 null이 된다`() {
        parser.setPending(WidgetTarget.Book(42))
        parser.setPending(null)
        assertNull(parser.currentTarget)
    }

    // ── setPending / sequence number ────────────────────────────────────────

    @Test
    fun `초기 seq는 0이다`() {
        assertEquals(0, parser.currentSeq)
    }

    @Test
    fun `setPending 최초 호출 시 seq가 1이 된다`() {
        parser.setPending(WidgetTarget.Book(42))
        assertEquals(1, parser.currentSeq)
    }

    @Test
    fun `같은 Book 타겟 연속 두 번 탭 시 seq가 2가 된다`() {
        parser.setPending(WidgetTarget.Book(42))
        parser.setPending(WidgetTarget.Book(42))
        assertEquals(2, parser.currentSeq)
    }

    @Test
    fun `setPending null이면 seq가 증가하지 않는다`() {
        parser.setPending(WidgetTarget.Book(42))
        val seqBefore = parser.currentSeq

        parser.setPending(null)

        assertEquals(seqBefore, parser.currentSeq)
    }

    @Test
    fun `서로 다른 타겟을 세 번 탭하면 seq가 3이 된다`() {
        parser.setPending(WidgetTarget.Book(1))
        parser.setPending(WidgetTarget.Book(2))
        parser.setPending(WidgetTarget.Home)
        assertEquals(3, parser.currentSeq)
    }

    @Test
    fun `setPending null 사이에 유효한 탭이 있으면 null은 seq에 영향을 주지 않는다`() {
        parser.setPending(WidgetTarget.Book(42))  // seq = 1
        parser.setPending(null)                    // seq = 1 (불변)
        parser.setPending(WidgetTarget.Book(42))  // seq = 2

        assertEquals(2, parser.currentSeq)
    }
}
