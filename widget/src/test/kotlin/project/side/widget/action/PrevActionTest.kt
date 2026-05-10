package project.side.widget.action

import org.junit.Test

/**
 * PrevAction은 Hilt EntryPoint + Glance 런타임에 의존하므로
 * 단위 테스트에서 직접 실행이 어렵습니다.
 * 클래스가 ActionCallback을 구현하는지만 확인합니다.
 */
class PrevActionTest {

    @Test
    fun `PrevAction implements ActionCallback`() {
        val action = PrevAction()
        assert(action is androidx.glance.appwidget.action.ActionCallback) {
            "PrevAction must implement ActionCallback"
        }
    }
}
