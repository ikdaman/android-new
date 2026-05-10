package project.side.widget.state

import org.junit.Assert.assertEquals
import org.junit.Test
import project.side.widget.state.WidgetStateKeys

class WidgetStateTest {

    @Test
    fun `SMALL_CURRENT_INDEX key name is small_current_index`() {
        assertEquals("small_current_index", WidgetStateKeys.SMALL_CURRENT_INDEX.name)
    }

    @Test
    fun `MEDIUM_CURRENT_INDEX key name is medium_current_index`() {
        assertEquals("medium_current_index", WidgetStateKeys.MEDIUM_CURRENT_INDEX.name)
    }

    @Test
    fun `SMALL and MEDIUM keys are distinct`() {
        assert(WidgetStateKeys.SMALL_CURRENT_INDEX != WidgetStateKeys.MEDIUM_CURRENT_INDEX)
    }
}
