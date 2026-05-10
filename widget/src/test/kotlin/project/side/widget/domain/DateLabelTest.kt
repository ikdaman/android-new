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
