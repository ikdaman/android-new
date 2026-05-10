package project.side.widget.data

interface WidgetUpdater {
    suspend fun refreshAll()
}

interface WidgetUpdateNotifier {
    suspend fun notifyAllWidgets()
}
