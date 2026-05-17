package project.side.ui.util

/**
 * 모아북 전역 날짜 포맷터.
 * 모든 화면에서 동일한 표기(`YYMMdd`)를 사용하도록 강제.
 *
 * 입력으로 ISO `YYYY-MM-DD`, ISO datetime, 또는 이미 변환된 `YY.MM.DD` / `YYYY.MM.DD` 모두 허용.
 */
object DateFormatter {
    private val datePattern = Regex("""^(\d{2,4})[-.](\d{2})[-.](\d{2})""")

    fun toShortDate(value: String?): String {
        if (value.isNullOrBlank()) return ""
        val match = datePattern.find(value) ?: return ""
        val (yearRaw, month, day) = match.destructured
        val year = if (yearRaw.length == 4) yearRaw.substring(2) else yearRaw
        return "$year$month$day"
    }
}
