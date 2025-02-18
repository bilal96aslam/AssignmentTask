package com.app.assignmenttask.network

import com.app.assignmenttask.network.response.BookResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("test_api.json")
    suspend fun getBookData() : Response<BookResponse>
}