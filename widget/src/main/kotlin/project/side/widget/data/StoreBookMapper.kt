package project.side.widget.data

import project.side.domain.model.StoreBookItem

fun StoreBookItem.toWidgetUiBook(): WidgetUiBook = WidgetUiBook(
    mybookId = mybookId,
    title = title,
    reason = reason,
    createdDate = createdDate
)
