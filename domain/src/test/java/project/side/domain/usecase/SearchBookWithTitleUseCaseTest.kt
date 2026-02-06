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

class SearchBookWithTitleUseCaseTest {

    @MockK
    private lateinit var repository: AladinRepository

    private lateinit var useCase: SearchBookWithTitleUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = SearchBookWithTitleUseCase(repository)
    }

    @Test
    fun `invoke returns results from repository`() = runTest {
        // Given
        val keyword = "코틀린"
        val startPage = 1
        val expectedResult = BookSearchResult(
            totalBookCount = 10,
            books = listOf(
                BookItem(
                    title = "코틀린 인 액션",
                    author = "드미트리 제메로프",
                    isbn = "1234567890"
                )
            )
        )
        coEvery { repository.searchBookWithTitle(keyword, startPage) } returns expectedResult

        // When
        val result = useCase.invoke(keyword, startPage)

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { repository.searchBookWithTitle(keyword, startPage) }
    }

    @Test
    fun `invoke uses default startPage of 0 when not specified`() = runTest {
        // Given
        val keyword = "자바"
        val expectedResult = BookSearchResult(
            totalBookCount = 5,
            books = emptyList()
        )
        coEvery { repository.searchBookWithTitle(keyword, 0) } returns expectedResult

        // When
        val result = useCase.invoke(keyword)

        // Then
        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { repository.searchBookWithTitle(keyword, 0) }
    }

    @Test
    fun `invoke returns empty result when repository returns empty`() = runTest {
        // Given
        val keyword = "비어있는책"
        val emptyResult = BookSearchResult()
        coEvery { repository.searchBookWithTitle(keyword, 0) } returns emptyResult

        // When
        val result = useCase.invoke(keyword)

        // Then
        assertEquals(emptyResult, result)
        assertEquals(0, result.totalBookCount)
        assertEquals(emptyList<BookItem>(), result.books)
    }
}
