package com.app.assignmenttask.presentation

import app.cash.turbine.test
import com.app.assignmenttask.data.remote.base.ApiError
import com.app.assignmenttask.data.remote.base.ApiResponse
import com.app.assignmenttask.data.remote.response.Book
import com.app.assignmenttask.extension.CoroutineTestExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(CoroutineTestExtension::class)
class BookViewModelTest {

    private val bookRepository: BookRepository = mock()
    private lateinit var viewModel: BookViewModel
    private val coroutineExtension = CoroutineTestExtension()

    @Test
    fun `getBooks should emit Loading and Success states when repository returns success`() =
        runTest {
            val expectedResponse = listOf(
                Book("1", "Book 1", "John", "", "2", null, emptyList()),
                Book("2", "Book 2", "Rock", "", "3", null, emptyList())
            )
            whenever(bookRepository.getBooks()).thenReturn(
                ApiResponse.Success(
                    expectedResponse,
                    200
                )
            )
            /** getFavouriteBooks() is also calling from init block to prevent flow collects null
             * value we assign the emptyList **/
            whenever(bookRepository.getFavouriteBooks()).thenReturn(flowOf(emptyList()))

            viewModel = BookViewModel(bookRepository)
            coroutineExtension.testScheduler.advanceUntilIdle()

            viewModel.bookState.test {
                skipItems(2) // skip first 2 states
                assertThat(awaitItem()).isEqualTo(BookUIState.Success(expectedResponse))
                expectNoEvents()
            }
        }

    @Test
    fun `getBooks should emit the empty list when repository returns no books`() = runTest {
        whenever(bookRepository.getBooks()).thenReturn(ApiResponse.Success(emptyList(), 200))
        whenever(bookRepository.getFavouriteBooks()).thenReturn(flowOf(emptyList()))

        viewModel = BookViewModel(bookRepository)
        coroutineExtension.testScheduler.advanceUntilIdle()
        viewModel.bookState.test {
            skipItems(2)
            val successState = awaitItem() as BookUIState.Success // / Cast to Success state
            assertEquals(0, successState.response.size)
            assertThat(successState).isEqualTo(BookUIState.Success(emptyList()))
            expectNoEvents()
        }
    }

    @Test
    fun `getBooks should emit the error when repository returns error`() = runTest {
        val expectedErrorResponse = ApiError(500, "Something went wrong")
        whenever(bookRepository.getBooks()).thenReturn(ApiResponse.Error(expectedErrorResponse))
        whenever(bookRepository.getFavouriteBooks()).thenReturn(flowOf(emptyList()))

        viewModel = BookViewModel(bookRepository)
        coroutineExtension.testScheduler.advanceUntilIdle()
        viewModel.bookState.test {
            skipItems(2)
            val errorState = awaitItem() as BookUIState.Error
            assertThat(errorState.errorMessage).isEqualTo("Something went wrong")
            expectNoEvents()
        }
    }


}