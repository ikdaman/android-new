package project.side.ui

/**
 * 위젯 Intent extras를 [WidgetTarget]으로 변환하고,
 * 연속된 동일 탭에서도 LaunchedEffect가 재실행되도록 sequence number를 관리한다.
 *
 * MainActivity에서 Android Intent 의존성을 분리하여 JVM 유닛 테스트가 가능하게 한다.
 */
class WidgetIntentParser {

    var currentTarget: WidgetTarget? = null
        private set

    var currentSeq: Int = 0
        private set

    /**
     * Intent extras Map → [WidgetTarget] 변환.
     * MainActivity에서 실제 Intent를 받으면 extras를 Map으로 변환하여 위임한다.
     */
    fun extractWidgetTarget(extras: Map<String, Any?>): WidgetTarget? {
        val target = extras["widget_target"] as? String ?: return null
        return when (target) {
            "book" -> WidgetTarget.Book((extras["mybook_id"] as? Int) ?: -1)
            "home" -> WidgetTarget.Home
            else -> null
        }
    }

    /**
     * 새 [WidgetTarget]을 설정하고, null이 아닐 때만 seq를 증가시킨다.
     * LaunchedEffect의 key로 seq를 사용하면 같은 Book을 연속 탭해도 재실행된다.
     */
    fun setPending(target: WidgetTarget?) {
        currentTarget = target
        if (target != null) currentSeq++
    }
}
