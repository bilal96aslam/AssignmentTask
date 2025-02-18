package com.app.assignmenttask.presentation

import com.app.assignmenttask.network.ApiService
import com.app.assignmenttask.network.base.ApiError
import com.app.assignmenttask.network.base.ApiResponse
import com.app.assignmenttask.network.mappers.mapBookResponseToBooks
import com.app.assignmenttask.network.response.Book
import javax.inject.Inject

class BookRepository @Inject constructor(private val apiService: ApiService) {
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
}