package project.side.widget.action

import org.junit.Test
import project.side.widget.intent.WidgetIntents

/**
 * OpenBookAction은 ActionCallback(Glance 런타임)이라 단위 테스트에서 직접 실행이 어렵습니다.
 * 핵심 로직은 WidgetIntents.openBook에 위임하므로, 키 상수만 검증합니다.
 */
class OpenBookActionTest {

    @Test
    fun `mybookIdKey name matches WidgetIntents extra key`() {
        // OpenBookAction.mybookIdKey 이름이 Intent extra 키와 일치해야 한다
        val key = OpenBookAction.mybookIdKey
        assert(key.name == WidgetIntents.EXTRA_MYBOOK_ID) {
            "mybookIdKey.name='${key.name}' != '${WidgetIntents.EXTRA_MYBOOK_ID}'"
        }
    }
}
