package project.side.widget.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import project.side.widget.theme.ColorVariant
import java.io.File
import kotlin.io.path.createTempDirectory

class WidgetPreferencesTest {
    private lateinit var tempDir: File
    private lateinit var prefs: WidgetPreferences

    @Before fun setup() {
        tempDir = createTempDirectory("widget-prefs-test").toFile()
        val store = PreferenceDataStoreFactory.create(
            produceFile = { File(tempDir, "test.preferences_pb") }
        )
        prefs = WidgetPreferences(store)
    }

    @After fun teardown() { tempDir.deleteRecursively() }

    @Test
    fun `unset id falls back to last default which is WHITE initially`() = runTest {
        assertEquals(ColorVariant.WHITE, prefs.colorFor(101))
    }

    @Test
    fun `set then read returns saved value`() = runTest {
        prefs.setColor(42, ColorVariant.BLUE)
        assertEquals(ColorVariant.BLUE, prefs.colorFor(42))
    }

    @Test
    fun `last set value becomes default for new ids`() = runTest {
        prefs.setColor(1, ColorVariant.BLUE)
        assertEquals(ColorVariant.BLUE, prefs.colorFor(999))
    }

    @Test
    fun `clear removes id mapping`() = runTest {
        prefs.setColor(7, ColorVariant.BLUE)
        prefs.clear(7)
        // last default still BLUE because clear doesn't reset default
        assertEquals(ColorVariant.BLUE, prefs.colorFor(7))
    }
}
