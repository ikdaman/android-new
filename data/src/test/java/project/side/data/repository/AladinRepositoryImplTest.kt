package project.side.data.repository

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import project.side.data.datasource.AladinBookSearchSource
import project.side.data.model.BookSearchResponse
import project.side.data.model.BookSearchItem
import project.side.domain.model.BookSearchResult

class AladinRepositoryImplTest {

    @MockK
    private lateinit var bookSearchSource: AladinBookSearchSource

    private lateinit var repository: AladinRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = AladinRepositoryImpl(bookSearchSource)
    }

    @Test
    fun `searchBookWithTitle returns mapped domain result on success`() = runTest {
        // Given
        val title = "코틀린"
        val startPage = 1
        val aladinResponse = BookSearchResponse(
            totalResults = 10,
            item = listOf(
                BookSearchItem(
                    title = "코틀린 인 액션",
                    link = "http://example.com",
                    author = "드미트리 제메로프",
                    cover = "http://example.com/cover.jpg",
                    publisher = "에이콘출판사",
                    isbn = null,
                    isbn13 = "9788966262281",
                    itemId = 123456L,
                    description = "코틀린 설명",
                    pubDate = "2017-04-10"
                )
            )
        )
        coEvery { bookSearchSource.searchBookWithTitle(title, startPage) } returns aladinResponse

        // When
        val result = repository.searchBookWithTitle(title, startPage)

        // Then
        assertEquals(10, result.totalBookCount)
        assertEquals(1, result.books.size)
        assertEquals("코틀린 인 액션", result.books[0].title)
        coVerify(exactly = 1) { bookSearchSource.searchBookWithTitle(title, startPage) }
    }

    @Test
    fun `searchBookWithTitle returns empty result on exception`() = runTest {
        // Given
        val title = "에러책"
        val startPage = 0
        coEvery { bookSearchSource.searchBookWithTitle(title, startPage) } throws RuntimeException("Network error")

        // When
        val result = repository.searchBookWithTitle(title, startPage)

        // Then
        assertEquals(BookSearchResult(), result)
        assertEquals(0, result.totalBookCount)
        assertEquals(emptyList<Any>(), result.books)
    }

    @Test
    fun `searchBookWithTitle handles different page numbers`() = runTest {
        // Given
        val title = "자바"
        val startPage = 5
        val aladinResponse = BookSearchResponse(
            totalResults = 100,
            item = emptyList()
        )
        coEvery { bookSearchSource.searchBookWithTitle(title, startPage) } returns aladinResponse

        // When
        val result = repository.searchBookWithTitle(title, startPage)

        // Then
        assertEquals(100, result.totalBookCount)
        assertEquals(0, result.books.size)
        coVerify(exactly = 1) { bookSearchSource.searchBookWithTitle(title, startPage) }
    }

    @Test
    fun `searchBookWithIsbn returns mapped domain result on success`() = runTest {
        // Given
        val isbn = "9788966262281"
        val aladinResponse = BookSearchResponse(
            totalResults = 1,
            item = listOf(
                BookSearchItem(
                    title = "코틀린 인 액션",
                    link = "http://example.com",
                    author = "드미트리 제메로프",
                    cover = "http://example.com/cover.jpg",
                    publisher = "에이콘출판사",
                    isbn = null,
                    isbn13 = isbn,
                    itemId = 123456L,
                    description = "코틀린 설명",
                    pubDate = "2017-04-10"
                )
            )
        )
        coEvery { bookSearchSource.searchBookWithIsbn(isbn) } returns aladinResponse

        // When
        val result = repository.searchBookWithIsbn(isbn)

        // Then
        assertEquals(1, result.totalBookCount)
        assertEquals(1, result.books.size)
        assertEquals(isbn, result.books[0].isbn)
        coVerify(exactly = 1) { bookSearchSource.searchBookWithIsbn(isbn) }
    }

    @Test
    fun `searchBookWithIsbn returns empty result on exception`() = runTest {
        // Given
        val isbn = "0000000000000"
        coEvery { bookSearchSource.searchBookWithIsbn(isbn) } throws RuntimeException("Not found")

        // When
        val result = repository.searchBookWithIsbn(isbn)

        // Then
        assertEquals(BookSearchResult(), result)
        assertEquals(0, result.totalBookCount)
        assertEquals(emptyList<Any>(), result.books)
    }

    @Test
    fun `searchBookWithIsbn handles network timeout exception`() = runTest {
        // Given
        val isbn = "1234567890123"
        coEvery { bookSearchSource.searchBookWithIsbn(isbn) } throws java.net.SocketTimeoutException("Timeout")

        // When
        val result = repository.searchBookWithIsbn(isbn)

        // Then
        assertEquals(BookSearchResult(), result)
        assertEquals(0, result.totalBookCount)
    }
}
