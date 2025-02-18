package com.app.assignmenttask.presentation

import com.app.assignmenttask.data.local.FavouriteBookDao
import com.app.assignmenttask.data.local.mappers.toBook
import com.app.assignmenttask.data.local.mappers.toBookEntity
import com.app.assignmenttask.data.remote.ApiService
import com.app.assignmenttask.data.remote.base.ApiError
import com.app.assignmenttask.data.remote.base.ApiResponse
import com.app.assignmenttask.data.remote.mappers.mapBookResponseToBooks
import com.app.assignmenttask.data.remote.response.Book
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookRepository @Inject constructor(
    private val apiService: ApiService,
    private val favouriteBookDao: FavouriteBookDao
) {
    suspend fun getBooks(): ApiResponse<List<Book>> {
        return try {
            val response = apiService.getBookData()
            val responseBody = response.body()
            val statusCode = response.code()

            if (response.isSuccessful && responseBody != null) {
                // Mapping API response to the Book objects
                val books = mapBookResponseToBooks(responseBody)
                ApiResponse.Success(books, statusCode)
            } else {
                ApiResponse.Error(ApiError(statusCode, "Something went wrong!"))
            }
        } catch (e: Exception) {
            ApiResponse.Error(ApiError(500, "Unexpected error: ${e.localizedMessage}"))
        }
    }

    fun getFavouriteBooks(): Flow<List<Book>> {
        return favouriteBookDao.getFavouriteBooks()
            .map { entities ->
                entities.map {
                    it.toBook()
                }
            }
    }

    fun isBookFavourite(bookId: String): Flow<Boolean> {
        return favouriteBookDao.getFavouriteBooks()
            .map { entities ->
                entities.any { it.id == bookId }
            }
    }

    suspend fun markAsFavourite(book: Book) {
        favouriteBookDao.upsert(book.toBookEntity())
    }

    suspend fun deleteFromFavourite(bookId: String) {
        return favouriteBookDao.deleteFavouriteBook(bookId)
    }

}