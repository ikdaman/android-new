package project.side.data.repository

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import project.side.data.datasource.MyBookDataSource
import project.side.data.model.BookInfoEntity
import project.side.data.model.BookInfoUpdateEntity
import project.side.data.model.DataApiResult
import project.side.data.model.HistoryInfoEntity
import project.side.data.model.MyBookDetailEntity
import project.side.data.model.MyBookSearchEntity
import project.side.data.model.MyBookSearchItemEntity
import project.side.data.model.StoreBookEntity
import project.side.data.model.StoreBookItemEntity
import project.side.domain.DataResource

class MyBookRepositoryImplTest {

    @MockK
    private lateinit var myBookDataSource: MyBookDataSource

    private lateinit var repository: MyBookRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = MyBookRepositoryImpl(myBookDataSource)
    }

    // getMyBookDetail tests
    @Test
    fun `getMyBookDetail emits loading then success with detail data`() = runTest {
        // Given
        val mybookId = 123
        val detailEntity = MyBookDetailEntity(
            mybookId = "123",
            readingStatus = "READING",
            shelfType = "MAIN",
            createdDate = "2024-01-01",
            reason = "재미있어서",
            bookInfo = BookInfoEntity(
                bookId = "1",
                source = "ALADIN",
                title = "테스트 책",
                author = "저자",
                coverImage = "http://example.com/cover.jpg",
                publisher = "출판사",
                totalPage = 300,
                publishDate = "2024-01-01",
                isbn = "1234567890",
                aladinId = "aladin123"
            ),
            historyInfo = HistoryInfoEntity(
                startedDate = "2024-01-01",
                finishedDate = null
            )
        )
        coEvery { myBookDataSource.getMyBookDetail(mybookId) } returns DataApiResult.Success(detailEntity)

        // When
        val flow = repository.getMyBookDetail(mybookId)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Success)
        val successData = (results[1] as DataResource.Success).data
        assertEquals("123", successData.mybookId)
        assertEquals("READING", successData.readingStatus)
        assertEquals("테스트 책", successData.bookInfo.title)
        coVerify(exactly = 1) { myBookDataSource.getMyBookDetail(mybookId) }
    }

    @Test
    fun `getMyBookDetail emits loading then error when data source returns error`() = runTest {
        // Given
        val mybookId = 999
        val errorMessage = "책 정보를 찾을 수 없습니다"
        coEvery { myBookDataSource.getMyBookDetail(mybookId) } returns DataApiResult.Error(errorMessage)

        // When
        val flow = repository.getMyBookDetail(mybookId)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
    }

    @Test
    fun `getMyBookDetail emits loading then error when exception occurs`() = runTest {
        // Given
        val mybookId = 456
        val exceptionMessage = "네트워크 오류"
        coEvery { myBookDataSource.getMyBookDetail(mybookId) } throws RuntimeException(exceptionMessage)

        // When
        val flow = repository.getMyBookDetail(mybookId)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(exceptionMessage, (results[1] as DataResource.Error).message)
    }

    // deleteMyBook tests
    @Test
    fun `deleteMyBook emits loading then success`() = runTest {
        // Given
        val mybookId = 789
        coEvery { myBookDataSource.deleteMyBook(mybookId) } returns DataApiResult.Success(Unit)

        // When
        val flow = repository.deleteMyBook(mybookId)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Success)
        coVerify(exactly = 1) { myBookDataSource.deleteMyBook(mybookId) }
    }

    @Test
    fun `deleteMyBook emits loading then error when data source returns error`() = runTest {
        // Given
        val mybookId = 111
        val errorMessage = "삭제 실패"
        coEvery { myBookDataSource.deleteMyBook(mybookId) } returns DataApiResult.Error(errorMessage)

        // When
        val flow = repository.deleteMyBook(mybookId)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
    }

    // searchMyBooks tests
    @Test
    fun `searchMyBooks emits loading then success with search results`() = runTest {
        // Given
        val query = "검색어"
        val page = 1
        val size = 10
        val searchEntity = MyBookSearchEntity(
            totalPages = 5,
            nowPage = 1,
            totalElements = 50,
            books = listOf(
                MyBookSearchItemEntity(
                    mybookId = 1,
                    readingStatus = "READING",
                    createdDate = "2024-01-01",
                    startedDate = "2024-01-01",
                    finishedDate = null,
                    title = "검색된 책",
                    author = listOf("저자1", "저자2"),
                    coverImage = "http://example.com/cover.jpg",
                    description = "설명"
                )
            )
        )
        coEvery { myBookDataSource.searchMyBooks(query, page, size) } returns DataApiResult.Success(searchEntity)

        // When
        val flow = repository.searchMyBooks(query, page, size)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Success)
        val successData = (results[1] as DataResource.Success).data
        assertEquals(5, successData.totalPages)
        assertEquals(50, successData.totalElements)
        assertEquals(1, successData.books.size)
        assertEquals("검색된 책", successData.books[0].title)
        coVerify(exactly = 1) { myBookDataSource.searchMyBooks(query, page, size) }
    }

    @Test
    fun `searchMyBooks emits loading then error when data source returns error`() = runTest {
        // Given
        val query = "오류검색"
        val page = 1
        val size = 10
        val errorMessage = "검색 실패"
        coEvery { myBookDataSource.searchMyBooks(query, page, size) } returns DataApiResult.Error(errorMessage)

        // When
        val flow = repository.searchMyBooks(query, page, size)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
    }

    @Test
    fun `searchMyBooks handles empty results`() = runTest {
        // Given
        val query = "없는검색어"
        val page = 1
        val size = 10
        val emptySearchEntity = MyBookSearchEntity(
            totalPages = 0,
            nowPage = 0,
            totalElements = 0,
            books = emptyList()
        )
        coEvery { myBookDataSource.searchMyBooks(query, page, size) } returns DataApiResult.Success(emptySearchEntity)

        // When
        val flow = repository.searchMyBooks(query, page, size)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[1] is DataResource.Success)
        val successData = (results[1] as DataResource.Success).data
        assertEquals(0, successData.totalElements)
        assertEquals(emptyList<Any>(), successData.books)
    }

    // getStoreBooks tests
    @Test
    fun `getStoreBooks emits loading then success with store books`() = runTest {
        // Given
        val keyword = "키워드"
        val page = 0
        val size = 20
        val storeEntity = StoreBookEntity(
            content = listOf(
                StoreBookItemEntity(
                    mybookId = 1,
                    createdDate = "2024-01-01",
                    title = "보관함 책",
                    author = listOf("저자"),
                    coverImage = "http://example.com/cover.jpg",
                    description = "설명"
                )
            ),
            totalPages = 3,
            totalElements = 60,
            last = false,
            first = true,
            size = 20,
            number = 0,
            numberOfElements = 20,
            empty = false
        )
        coEvery { myBookDataSource.getStoreBooks(keyword, page, size) } returns DataApiResult.Success(storeEntity)

        // When
        val flow = repository.getStoreBooks(keyword, page, size)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Success)
        val successData = (results[1] as DataResource.Success).data
        assertEquals(3, successData.totalPages)
        assertEquals(60, successData.totalElements)
        assertEquals(1, successData.content.size)
        assertEquals("보관함 책", successData.content[0].title)
        coVerify(exactly = 1) { myBookDataSource.getStoreBooks(keyword, page, size) }
    }

    @Test
    fun `getStoreBooks emits loading then error when data source returns error`() = runTest {
        // Given
        val keyword = "오류"
        val page = 0
        val size = 20
        val errorMessage = "보관함 조회 실패"
        coEvery { myBookDataSource.getStoreBooks(keyword, page, size) } returns DataApiResult.Error(errorMessage)

        // When
        val flow = repository.getStoreBooks(keyword, page, size)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
    }

    // updateReadingStatus tests
    @Test
    fun `updateReadingStatus emits loading then success with mybookId`() = runTest {
        // Given
        val mybookId = 123
        val startedDate = "2024-01-01"
        val finishedDate = "2024-01-31"
        coEvery { myBookDataSource.updateReadingStatus(mybookId, startedDate, finishedDate) } returns DataApiResult.Success(mybookId)

        // When
        val flow = repository.updateReadingStatus(mybookId, startedDate, finishedDate)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Success)
        val successData = (results[1] as DataResource.Success).data
        assertEquals(mybookId, successData)
        coVerify(exactly = 1) { myBookDataSource.updateReadingStatus(mybookId, startedDate, finishedDate) }
    }

    @Test
    fun `updateReadingStatus emits loading then error when data source returns error`() = runTest {
        // Given
        val mybookId = 456
        val startedDate = "2024-01-01"
        val finishedDate = null
        val errorMessage = "상태 업데이트 실패"
        coEvery { myBookDataSource.updateReadingStatus(mybookId, startedDate, finishedDate) } returns DataApiResult.Error(errorMessage)

        // When
        val flow = repository.updateReadingStatus(mybookId, startedDate, finishedDate)
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
    }

    // updateMyBook tests
    @Test
    fun `updateMyBook emits loading then success with mybookId`() = runTest {
        // Given
        val mybookId = 789
        val reason = "재미있어서"
        val startedDate = "2024-01-01"
        val finishedDate = "2024-01-31"
        coEvery { myBookDataSource.updateMyBook(mybookId, any()) } returns DataApiResult.Success(mybookId)

        // When
        val flow = repository.updateMyBook(
            mybookId = mybookId,
            status = null,
            reason = reason,
            startedDate = startedDate,
            finishedDate = finishedDate,
            bookInfoTitle = null,
            bookInfoAuthor = null,
            bookInfoPublisher = null,
            bookInfoPublishDate = null,
            bookInfoIsbn = null,
            bookInfoTotalPage = null
        )
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Success)
        val successData = (results[1] as DataResource.Success).data
        assertEquals(mybookId, successData)
        coVerify(exactly = 1) { myBookDataSource.updateMyBook(mybookId, any()) }
    }

    @Test
    fun `updateMyBook emits loading then error when data source returns error`() = runTest {
        // Given
        val mybookId = 321
        val reason = "재미없어서"
        val startedDate = null
        val finishedDate = null
        val errorMessage = "책 정보 업데이트 실패"
        coEvery { myBookDataSource.updateMyBook(mybookId, any()) } returns DataApiResult.Error(errorMessage)

        // When
        val flow = repository.updateMyBook(
            mybookId = mybookId,
            status = null,
            reason = reason,
            startedDate = startedDate,
            finishedDate = finishedDate,
            bookInfoTitle = null,
            bookInfoAuthor = null,
            bookInfoPublisher = null,
            bookInfoPublishDate = null,
            bookInfoIsbn = null,
            bookInfoTotalPage = null
        )
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(errorMessage, (results[1] as DataResource.Error).message)
    }

    @Test
    fun `updateMyBook emits loading then error when exception occurs`() = runTest {
        // Given
        val mybookId = 654
        val reason = "이유"
        val startedDate = "2024-01-01"
        val finishedDate = "2024-01-31"
        val exceptionMessage = "서버 오류"
        coEvery { myBookDataSource.updateMyBook(mybookId, any()) } throws RuntimeException(exceptionMessage)

        // When
        val flow = repository.updateMyBook(
            mybookId = mybookId,
            status = null,
            reason = reason,
            startedDate = startedDate,
            finishedDate = finishedDate,
            bookInfoTitle = null,
            bookInfoAuthor = null,
            bookInfoPublisher = null,
            bookInfoPublishDate = null,
            bookInfoIsbn = null,
            bookInfoTotalPage = null
        )
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[0] is DataResource.Loading)
        assertTrue(results[1] is DataResource.Error)
        assertEquals(exceptionMessage, (results[1] as DataResource.Error).message)
    }

    @Test
    fun `updateMyBook with status passes status in entity`() = runTest {
        // Given
        val mybookId = 100
        val status = "READING"
        coEvery {
            myBookDataSource.updateMyBook(eq(mybookId), any())
        } returns DataApiResult.Success(mybookId)

        // When
        val flow = repository.updateMyBook(
            mybookId = mybookId,
            status = status,
            reason = null,
            startedDate = null,
            finishedDate = null,
            bookInfoTitle = null,
            bookInfoAuthor = null,
            bookInfoPublisher = null,
            bookInfoPublishDate = null,
            bookInfoIsbn = null,
            bookInfoTotalPage = null
        )
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[1] is DataResource.Success)
        coVerify(exactly = 1) {
            myBookDataSource.updateMyBook(mybookId, withArg { entity ->
                assertEquals(status, entity.status)
            })
        }
    }

    @Test
    fun `updateMyBook with bookInfo creates BookInfoUpdateEntity`() = runTest {
        // Given
        val mybookId = 200
        val title = "책 제목"
        val author = "저자"
        val publisher = "출판사"
        coEvery {
            myBookDataSource.updateMyBook(eq(mybookId), any())
        } returns DataApiResult.Success(mybookId)

        // When
        val flow = repository.updateMyBook(
            mybookId = mybookId,
            status = null,
            reason = null,
            startedDate = null,
            finishedDate = null,
            bookInfoTitle = title,
            bookInfoAuthor = author,
            bookInfoPublisher = publisher,
            bookInfoPublishDate = null,
            bookInfoIsbn = null,
            bookInfoTotalPage = null
        )
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[1] is DataResource.Success)
        coVerify(exactly = 1) {
            myBookDataSource.updateMyBook(mybookId, withArg { entity ->
                assertTrue(entity.bookInfo != null)
                assertEquals(title, entity.bookInfo?.title)
                assertEquals(author, entity.bookInfo?.author)
                assertEquals(publisher, entity.bookInfo?.publisher)
            })
        }
    }

    @Test
    fun `updateMyBook without bookInfo params sets bookInfo to null in entity`() = runTest {
        // Given
        val mybookId = 300
        val reason = "이유만 있음"
        coEvery {
            myBookDataSource.updateMyBook(eq(mybookId), any())
        } returns DataApiResult.Success(mybookId)

        // When
        val flow = repository.updateMyBook(
            mybookId = mybookId,
            status = null,
            reason = reason,
            startedDate = null,
            finishedDate = null,
            bookInfoTitle = null,
            bookInfoAuthor = null,
            bookInfoPublisher = null,
            bookInfoPublishDate = null,
            bookInfoIsbn = null,
            bookInfoTotalPage = null
        )
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[1] is DataResource.Success)
        coVerify(exactly = 1) {
            myBookDataSource.updateMyBook(mybookId, withArg { entity ->
                assertEquals(reason, entity.reason)
                assertEquals(null, entity.bookInfo)
            })
        }
    }

    @Test
    fun `updateMyBook with only some bookInfo params still creates BookInfoUpdateEntity`() = runTest {
        // Given
        val mybookId = 400
        val title = "제목만"
        coEvery {
            myBookDataSource.updateMyBook(eq(mybookId), any())
        } returns DataApiResult.Success(mybookId)

        // When
        val flow = repository.updateMyBook(
            mybookId = mybookId,
            status = null,
            reason = null,
            startedDate = null,
            finishedDate = null,
            bookInfoTitle = title,
            bookInfoAuthor = null,
            bookInfoPublisher = null,
            bookInfoPublishDate = null,
            bookInfoIsbn = null,
            bookInfoTotalPage = null
        )
        val results = flow.toList()

        // Then
        assertEquals(2, results.size)
        assertTrue(results[1] is DataResource.Success)
        coVerify(exactly = 1) {
            myBookDataSource.updateMyBook(mybookId, withArg { entity ->
                assertTrue(entity.bookInfo != null)
                assertEquals(title, entity.bookInfo?.title)
                assertEquals(null, entity.bookInfo?.author)
                assertEquals(null, entity.bookInfo?.publisher)
            })
        }
    }
}
