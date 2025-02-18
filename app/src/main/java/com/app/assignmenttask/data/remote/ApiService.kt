package com.app.assignmenttask.data.remote

import com.app.assignmenttask.data.remote.response.BookResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("test_api.json")
    suspend fun getBookData() : Response<BookResponse>
}