package project.side.ui.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class SignupDataHolderTest {

    private lateinit var holder: SignupDataHolder

    @Before
    fun setUp() {
        holder = SignupDataHolder()
    }

    @Test
    fun `초기 상태에서 consume은 null을 반환한다`() {
        assertNull(holder.consume())
    }

    @Test
    fun `set 후 consume하면 저장한 데이터를 반환한다`() {
        holder.set("token123", "google", "provider_id_456")

        val data = holder.consume()

        assertNotNull(data)
        assertEquals("token123", data!!.socialToken)
        assertEquals("google", data.provider)
        assertEquals("provider_id_456", data.providerId)
    }

    @Test
    fun `consume 후 다시 consume하면 null을 반환한다`() {
        holder.set("token", "naver", "id")

        holder.consume()
        val second = holder.consume()

        assertNull(second)
    }

    @Test
    fun `set을 여러 번 호출하면 마지막 값이 반환된다`() {
        holder.set("token1", "google", "id1")
        holder.set("token2", "kakao", "id2")

        val data = holder.consume()

        assertNotNull(data)
        assertEquals("token2", data!!.socialToken)
        assertEquals("kakao", data.provider)
        assertEquals("id2", data.providerId)
    }

    @Test
    fun `consume 후 다시 set하면 새 데이터를 반환한다`() {
        holder.set("token1", "google", "id1")
        holder.consume()

        holder.set("token2", "naver", "id2")
        val data = holder.consume()

        assertNotNull(data)
        assertEquals("token2", data!!.socialToken)
        assertEquals("naver", data.provider)
    }
}
