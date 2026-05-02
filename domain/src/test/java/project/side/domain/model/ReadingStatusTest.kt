package project.side.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ReadingStatusTest {

    @Test
    fun `TODO displayName은 읽고 싶은 책`() {
        assertEquals("읽고 싶은 책", ReadingStatus.TODO.displayName)
    }

    @Test
    fun `INPROGRESS displayName은 읽는 중`() {
        assertEquals("읽는 중", ReadingStatus.INPROGRESS.displayName)
    }

    @Test
    fun `DONE displayName은 완독`() {
        assertEquals("완독", ReadingStatus.DONE.displayName)
    }

    @Test
    fun `from은 알 수 없는 값에 대해 TODO를 반환한다`() {
        assertEquals(ReadingStatus.TODO, ReadingStatus.from(null))
        assertEquals(ReadingStatus.TODO, ReadingStatus.from(""))
        assertEquals(ReadingStatus.TODO, ReadingStatus.from("UNKNOWN"))
    }

    @Test
    fun `from은 문자열을 파싱한다`() {
        assertEquals(ReadingStatus.TODO, ReadingStatus.from("TODO"))
        assertEquals(ReadingStatus.INPROGRESS, ReadingStatus.from("INPROGRESS"))
        assertEquals(ReadingStatus.DONE, ReadingStatus.from("DONE"))
    }

    @Test
    fun `from은 COMPLETED 별칭을 DONE으로 매핑한다`() {
        assertEquals(ReadingStatus.DONE, ReadingStatus.from("COMPLETED"))
    }

    @Test
    fun `displayNameOf는 문자열을 직접 표시 문자열로 변환한다`() {
        assertEquals("읽고 싶은 책", ReadingStatus.displayNameOf("TODO"))
        assertEquals("읽는 중", ReadingStatus.displayNameOf("INPROGRESS"))
        assertEquals("완독", ReadingStatus.displayNameOf("DONE"))
        assertEquals("완독", ReadingStatus.displayNameOf("COMPLETED"))
        assertEquals("읽고 싶은 책", ReadingStatus.displayNameOf(null))
    }
}
