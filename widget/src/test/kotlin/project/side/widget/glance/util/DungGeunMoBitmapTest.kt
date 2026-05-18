package project.side.widget.glance.util

import org.junit.Test

/**
 * renderDungGeunMoBitmap 은 Android Bitmap/Canvas 에 의존해서 JVM unit test 로는 실제 렌더를 검증할 수 없다.
 * 여기서는 함수 시그니처가 유지되는지만 보장하고, 실제 렌더 검증은 instrumented test 또는 수동 QA 로 한다.
 */
class DungGeunMoBitmapTest {

    @Test
    fun `renderDungGeunMoBitmap function reference is callable`() {
        val ref: (android.content.Context, String, Float, Int) -> android.graphics.Bitmap = ::renderDungGeunMoBitmap
        assert(ref != null) { "renderDungGeunMoBitmap signature must remain stable" }
    }
}
