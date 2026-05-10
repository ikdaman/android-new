package project.side.widget.action

import org.junit.Test

/**
 * RefreshSmallAction은 Hilt EntryPoint + Glance 런타임에 의존하므로
 * 단위 테스트에서 직접 실행이 어렵습니다.
 * 핵심 인덱스 선택 로직은 RefreshLogic에 위임되어 RefreshLogicTest에서 검증됩니다.
 * 여기서는 클래스가 ActionCallback을 구현하는지만 확인합니다.
 */
class RefreshSmallActionTest {

    @Test
    fun `RefreshSmallAction implements ActionCallback`() {
        val action = RefreshSmallAction()
        assert(action is androidx.glance.appwidget.action.ActionCallback) {
            "RefreshSmallAction must implement ActionCallback"
        }
    }
}
