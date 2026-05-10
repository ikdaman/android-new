package project.side.ui

sealed class WidgetTarget {
    data class Book(val mybookId: Int) : WidgetTarget()
    data object Home : WidgetTarget()
}
