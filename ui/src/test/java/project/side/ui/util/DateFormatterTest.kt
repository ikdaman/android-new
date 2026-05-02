package project.side.ui.util

import org.junit.Assert.assertEquals
import org.junit.Test

class DateFormatterTest {

    @Test
    fun `ISO 날짜를 YY_MM_DD 포맷으로 변환한다`() {
        assertEquals("26.03.30", DateFormatter.toShortDate("2026-03-30"))
        assertEquals("21.05.04", DateFormatter.toShortDate("2021-05-04"))
    }

    @Test
    fun `ISO datetime 문자열도 처리한다`() {
        assertEquals("26.03.30", DateFormatter.toShortDate("2026-03-30T12:34:56"))
        assertEquals("26.03.30", DateFormatter.toShortDate("2026-03-30T12:34:56.789Z"))
    }

    @Test
    fun `잘못된 입력은 빈 문자열을 반환한다`() {
        assertEquals("", DateFormatter.toShortDate(null))
        assertEquals("", DateFormatter.toShortDate(""))
        assertEquals("", DateFormatter.toShortDate("garbage"))
    }

    @Test
    fun `이미 YY_MM_DD 인 경우는 그대로 둔다`() {
        assertEquals("26.03.30", DateFormatter.toShortDate("26.03.30"))
    }

    @Test
    fun `YYYY_MM_DD 도 처리`() {
        assertEquals("26.03.30", DateFormatter.toShortDate("2026.03.30"))
    }
}
