package project.side.ui.theme

import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class ColorTest {

    @Test
    fun `상태 컬러 4종이 정의되어 있다`() {
        // 모든 상태색이 서로 구별 가능해야 한다
        val colors = listOf(StatusWish, StatusReading, StatusDone, DangerAccent)
        val distinct = colors.distinct()
        assertEquals("상태 컬러 4종은 모두 달라야 한다", colors.size, distinct.size)
    }

    @Test
    fun `상태 컬러는 불투명하다`() {
        listOf(StatusWish, StatusReading, StatusDone, DangerAccent).forEach { c ->
            assertEquals("${c} 알파는 1.0 이어야 한다", 1.0f, c.alpha, 0.001f)
        }
    }

    @Test
    fun `DangerAccent는 일반 텍스트와 구별된다`() {
        assertNotEquals(DangerAccent, TextPrimary)
        assertNotEquals(DangerAccent, Primary)
    }
}
