package project.side.presentation.model

import project.side.domain.model.HistoryBookInfo

data class HistoryBookState(
    val isLoading: Boolean = true,
    val viewType: HistoryViewType = HistoryViewType.LIST,
    val totalPages: Int = 1,
    val nowPages: Int = 1,
    val books: List<HistoryBookInfo> = emptyList(),
    val errorMessage: String? = null
)

enum class HistoryViewType {
    DATASET, LIST;

    fun toggle(): HistoryViewType {
        return when (this) {
            DATASET -> LIST
            LIST -> DATASET
        }
    }
}