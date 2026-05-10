package project.side.widget.action

import org.junit.Test

/**
 * OpenAppAction은 ActionCallback(Glance 런타임)이라 단위 테스트에서 직접 실행이 어렵습니다.
 * 클래스가 ActionCallback을 구현하는지만 컴파일 타임에 확인합니다.
 */
class OpenAppActionTest {

    @Test
    fun `OpenAppAction implements ActionCallback`() {
        val action = OpenAppAction()
        assert(action is androidx.glance.appwidget.action.ActionCallback) {
            "OpenAppAction must implement ActionCallback"
        }
    }
}
