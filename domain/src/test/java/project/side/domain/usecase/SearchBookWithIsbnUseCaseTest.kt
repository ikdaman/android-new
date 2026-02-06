package project.side.domain.usecase.search

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import project.side.domain.model.BookItem
import project.side.domain.model.BookSearchResult
import project.side.domain.repository.AladinRepository

class SearchBookWithIsbnUseCaseTest {

    @MockK
    private lateinit var repository: AladinRepository

    private lateinit var useCase: SearchBookWithIsbnUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = SearchBookWithIsbnUseCase(repository)
    }

    @Test
    fun `invoke returns result from repository with valid ISBN`() = runTest {
        // Given
        val isbn = "9788966262281"
        val expectedResult = BookSearchResult(
            totalBookCount = 1,
            books = listOf(
                BookItem(
                    title = "코틀린 인 액션",
                    author = "드미트리 제메로프",
                    isbn = isbn,
                    publisher = "에이콘출판사"
                )
            )
        )
        coEvery { repository.searchBookWithIsbn(isbn) } returns expectedResult

        // When
        val result = useCase.invoke(isbn)

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { repository.searchBookWithIsbn(isbn) }
    }

    @Test
    fun `invoke returns empty result when ISBN not found`() = runTest {
        // Given
        val isbn = "0000000000000"
        val emptyResult = BookSearchResult()
        coEvery { repository.searchBookWithIsbn(isbn) } returns emptyResult

        // When
        val result = useCase.invoke(isbn)

        // Then
        assertEquals(emptyResult, result)
        assertEquals(0, result.totalBookCount)
        assertEquals(emptyList<BookItem>(), result.books)
    }

    @Test
    fun `invoke passes ISBN directly to repository`() = runTest {
        // Given
        val isbn = "1234567890123"
        val result = BookSearchResult(totalBookCount = 0, books = emptyList())
        coEvery { repository.searchBookWithIsbn(isbn) } returns result

        // When
        useCase.invoke(isbn)

        // Then
        coVerify(exactly = 1) { repository.searchBookWithIsbn(isbn) }
    }
}
