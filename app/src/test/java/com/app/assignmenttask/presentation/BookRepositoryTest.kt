package com.app.assignmenttask.presentation

import com.app.assignmenttask.data.local.FavouriteBookDao
import com.app.assignmenttask.data.remote.ApiService
import com.app.assignmenttask.data.remote.response.BookResponse
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

class BookRepositoryTest {

    private val apiService: ApiService = mock()
    private val favouriteBookDao: FavouriteBookDao = mock()
    private lateinit var bookRepository: BookRepository

    @BeforeEach   // in junit 5 we use this annotation
    fun setUp() {
        bookRepository = BookRepository(apiService, favouriteBookDao)
    }

    @Test
    fun `returns empty book list when no books are available`() = runTest {
        val expectedResponse: BookResponse = mock()
        whenever(apiService.getBookData()).thenReturn(Response.success(expectedResponse))
        val actualResult = bookRepository.getBooks()
        assertThat(actualResult).isEqualTo(expectedResponse)
    }

}