package project.side.remote.login

import android.app.Activity
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class CurrentActivityHolderTest {

    @Test
    fun `require throws when nothing bound`() {
        val holder = CurrentActivityHolder()
        assertThrows(IllegalStateException::class.java) { holder.require() }
    }

    @Test
    fun `require returns bound activity`() {
        val holder = CurrentActivityHolder()
        val activity = mockk<Activity>()
        holder.bind(activity)
        assertEquals(activity, holder.require())
    }

    @Test
    fun `unbind clears reference`() {
        val holder = CurrentActivityHolder()
        holder.bind(mockk<Activity>())
        holder.unbind()
        assertThrows(IllegalStateException::class.java) { holder.require() }
    }
}
