package project.side.widget.action

import org.junit.Test

/**
 * NextAction은 Hilt EntryPoint + Glance 런타임에 의존하므로
 * 단위 테스트에서 직접 실행이 어렵습니다.
 * 클래스가 ActionCallback을 구현하는지만 확인합니다.
 */
class NextActionTest {

    @Test
    fun `NextAction implements ActionCallback`() {
        val action = NextAction()
        assert(action is androidx.glance.appwidget.action.ActionCallback) {
            "NextAction must implement ActionCallback"
        }
    }
}
