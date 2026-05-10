package project.side.widget.action

import project.side.widget.data.WidgetUiBook

object RefreshLogic {
    /** 현재 책을 제외한 책 중 random index 반환. 1권뿐이면 -1, 0권이면 -2. */
    fun pickNextIndex(books: List<WidgetUiBook>, currentIndex: Int, randomInt: (Int) -> Int): Int {
        if (books.isEmpty()) return -2
        if (books.size == 1) return -1
        val candidates = books.indices.filter { it != currentIndex }
        return candidates[randomInt(candidates.size)]
    }
}
