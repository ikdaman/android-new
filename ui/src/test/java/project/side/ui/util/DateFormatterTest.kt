package project.side.ui.util

import org.junit.Assert.assertEquals
import org.junit.Test

class DateFormatterTest {

    @Test
    fun `ISO 날짜를 YYMMdd 포맷으로 변환한다`() {
        assertEquals("260330", DateFormatter.toShortDate("2026-03-30"))
        assertEquals("210504", DateFormatter.toShortDate("2021-05-04"))
    }

    @Test
    fun `ISO datetime 문자열도 처리한다`() {
        assertEquals("260330", DateFormatter.toShortDate("2026-03-30T12:34:56"))
        assertEquals("260330", DateFormatter.toShortDate("2026-03-30T12:34:56.789Z"))
    }

    @Test
    fun `잘못된 입력은 빈 문자열을 반환한다`() {
        assertEquals("", DateFormatter.toShortDate(null))
        assertEquals("", DateFormatter.toShortDate(""))
        assertEquals("", DateFormatter.toShortDate("garbage"))
    }

    @Test
    fun `이미 점이 들어간 YY_MM_DD 입력도 YYMMdd 로 정규화한다`() {
        assertEquals("260330", DateFormatter.toShortDate("26.03.30"))
    }

    @Test
    fun `YYYY_MM_DD 점 표기도 처리`() {
        assertEquals("260330", DateFormatter.toShortDate("2026.03.30"))
    }
}
