package com.app.assignmenttask.presentation

import app.cash.turbine.test
import com.app.assignmenttask.data.local.BookEntity
import com.app.assignmenttask.data.local.FavouriteBookDao
import com.app.assignmenttask.data.local.mappers.toBook
import com.app.assignmenttask.data.remote.ApiService
import com.app.assignmenttask.data.remote.base.ApiResponse
import com.app.assignmenttask.data.remote.response.BookResponse
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

class BookRepositoryTest {

    private val apiService: ApiService = mock()
    private val favouriteBookDao: FavouriteBookDao = mock()
    private lateinit var bookRepository: BookRepository

    @BeforeEach   // in junit 5 we use this annotation
    fun setUp() {
        bookRepository = BookRepository(apiService, favouriteBookDao) // arrange
    }

    @Test
    fun `returns empty book list when no books are available`() = runTest {
        val expectedResponse = BookResponse(emptyList())
        whenever(apiService.getBookData()).thenReturn(Response.success(expectedResponse))
        val actualResult = bookRepository.getBooks() // act
        assertThat(actualResult).isInstanceOf(ApiResponse.Success::class.java) // assert
        val successResponse = actualResult as ApiResponse.Success
        assertThat(successResponse.data).isEmpty()
    }

    @Test
    fun `returns book list when books are available`() = runTest {
        val dummyBookResponse = BookResponse(
            data = listOf(
                BookResponse.Data(
                    attributes = BookResponse.Data.Attributes(
                        image = "https://example.com/book.jpg",
                        title = "Sample Book"
                    ),
                    id = "1",
                    relationships = BookResponse.Data.Relationships(
                        author = BookResponse.Data.Relationships.Author(
                            data = BookResponse.Data.Relationships.Author.Data(
                                id = "10",
                                type = "author"
                            )
                        ),
                        reviews = null // Optional field left empty
                    ),
                    type = "book"
                )
            ),
            includes = emptyList() // Optional field left empty
        )

        whenever(apiService.getBookData()).thenReturn(Response.success(dummyBookResponse))
        val result = bookRepository.getBooks()
        val response = result as ApiResponse.Success
        assertThat(response).isInstanceOf(ApiResponse.Success::class.java)
        assertThat("1").isEqualTo(response.data[0].id)
        assertThat("Sample Book").isEqualTo(response.data[0].title)
    }

    @Test
    fun `returns error when JSON parsing fails`() = runTest {
        whenever(apiService.getBookData()).thenThrow(JsonSyntaxException("JSON parsing error"))
        val result = bookRepository.getBooks()
        val response = result as ApiResponse.Error
        assertThat(response.error.code).isEqualTo(500)
        assertThat(response.error.message).contains("JSON parsing error")
    }

    @Test
    fun `returns favouriteBooks when favourite book list available`(): Unit = runTest {
        val expectedResponse = BookEntity("1", "Book 1", "John", "", "2", null, emptyList())
        whenever(favouriteBookDao.getFavouriteBooks()).thenReturn(flowOf(listOf(expectedResponse)))
        val result = bookRepository.getFavouriteBooks()
        result.collect {
            assertEquals(1, it.size)
            assertEquals("Book 1", it[0].title)
        }
    }

    @Test
    fun `return true if book isFavourite`(): Unit = runTest {
        val expectedResponse = BookEntity("1", "Book 1", "John", "", "2", null, emptyList())
        whenever(favouriteBookDao.getFavouriteBooks()).thenReturn(flowOf(listOf(expectedResponse)))
        val result = bookRepository.isBookFavourite("1")
        result.test {
            assertEquals(true, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `mark book as favourite`(): Unit = runTest {
        val expectedResponse = BookEntity("1", "Book 1", "John", "", "2", null, emptyList())
        bookRepository.markAsFavourite(expectedResponse.toBook())
        verify(favouriteBookDao).upsert(expectedResponse)
    }


    @Test
    fun `delete book as favourite`(): Unit = runTest {
        bookRepository.deleteFromFavourite("1")
        verify(favouriteBookDao).deleteFavouriteBook("1")
    }

}