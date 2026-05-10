package project.side.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/**
 * MainActivity 위젯 인텐트 흐름 통합 테스트.
 *
 * MainActivity는 Android ComponentActivity를 상속하므로 JVM 테스트에서
 * 직접 인스턴스화할 수 없다. 대신 MainActivity가 위임하는 [WidgetIntentParser]를
 * 통해 핵심 흐름(extract → setPending → seq 변화)을 검증한다.
 *
 * 위젯 탭 버그 시나리오:
 *   같은 Book(42)를 연속 두 번 탭 → LaunchedEffect key(seq)가 달라야 재실행됨.
 */
class MainActivityTest {

    private lateinit var parser: WidgetIntentParser

    @Before
    fun setUp() {
        parser = WidgetIntentParser()
    }

    @Test
    fun `onCreate에서 위젯 인텐트를 받으면 target과 seq가 설정된다`() {
        val extras = mapOf("widget_target" to "book", "mybook_id" to 42)

        val target = parser.extractWidgetTarget(extras)
        parser.setPending(target)

        assertEquals(WidgetTarget.Book(42), parser.currentTarget)
        assertEquals(1, parser.currentSeq)
    }

    @Test
    fun `같은 Book 위젯을 연속 두 번 탭하면 seq가 달라 LaunchedEffect가 재실행된다`() {
        val extras = mapOf("widget_target" to "book", "mybook_id" to 42)

        // 첫 번째 탭 (onCreate)
        parser.setPending(parser.extractWidgetTarget(extras))
        val seqAfterFirst = parser.currentSeq

        // 두 번째 탭 (onNewIntent)
        parser.setPending(parser.extractWidgetTarget(extras))
        val seqAfterSecond = parser.currentSeq

        // seq가 달라야 LaunchedEffect(seq)가 재실행됨
        assertEquals(1, seqAfterFirst)
        assertEquals(2, seqAfterSecond)
        assert(seqAfterFirst != seqAfterSecond) { "동일 타겟 연속 탭 시 seq가 달라야 합니다" }
    }

    @Test
    fun `onWidgetTargetConsumed 후 setPending(null)하면 target이 제거된다`() {
        parser.setPending(WidgetTarget.Book(42))

        // MainScreen의 onWidgetTargetConsumed 호출 시뮬레이션
        parser.setPending(null)

        assertNull(parser.currentTarget)
    }

    @Test
    fun `Home 위젯 탭은 seq를 증가시키고 target을 Home으로 설정한다`() {
        val extras = mapOf("widget_target" to "home")

        parser.setPending(parser.extractWidgetTarget(extras))

        assertEquals(WidgetTarget.Home, parser.currentTarget)
        assertEquals(1, parser.currentSeq)
    }

    @Test
    fun `유효하지 않은 인텐트는 pending 상태를 변경하지 않는다`() {
        parser.setPending(WidgetTarget.Book(42))
        val seqBefore = parser.currentSeq

        // widget_target 없는 인텐트 → extractWidgetTarget이 null 반환
        val target = parser.extractWidgetTarget(emptyMap())
        if (target != null) parser.setPending(target)

        assertEquals(seqBefore, parser.currentSeq)
        assertEquals(WidgetTarget.Book(42), parser.currentTarget)
    }
}
