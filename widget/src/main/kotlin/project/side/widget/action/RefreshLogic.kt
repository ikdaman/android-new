package project.side.widget.action

import project.side.widget.data.WidgetUiBook

object RefreshLogic {
    /** 인덱스 기반 — 호환성 유지용 */
    fun pickNextIndex(books: List<WidgetUiBook>, currentIndex: Int, randomInt: (Int) -> Int): Int {
        if (books.isEmpty()) return -2
        if (books.size == 1) return -1
        val candidates = books.indices.filter { it != currentIndex }
        return candidates[randomInt(candidates.size)]
    }

    /**
     * 현재 mybookId 책을 제외하고 다음 책 1권 랜덤 픽.
     * - books 비면 null
     * - books 1권이면 그 책 그대로 반환 (변경 없음)
     * - 그 외엔 currentMybookId가 아닌 책 중 랜덤
     */
    fun pickNextByMybookId(
        books: List<WidgetUiBook>,
        currentMybookId: Int?,
        randomInt: (Int) -> Int,
    ): WidgetUiBook? {
        if (books.isEmpty()) return null
        if (books.size == 1) return books[0]
        val candidates = books.filter { it.mybookId != currentMybookId }
        if (candidates.isEmpty()) return books[0]  // current가 캐시에 없거나 모두 같은 id (비현실적)
        return candidates[randomInt(candidates.size)]
    }
}
