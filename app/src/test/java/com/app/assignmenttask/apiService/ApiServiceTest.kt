package com.app.assignmenttask.apiService

import com.app.assignmenttask.data.remote.ApiService
import com.app.assignmenttask.utils.Helper
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @Test
    fun `get book data with empty object`() = runTest {
        val mockResponse = MockResponse()
        mockResponse.setBody("{}")
        mockWebServer.enqueue(mockResponse)

        val response = apiService.getBookData()
        mockWebServer.takeRequest()

        assertEquals(true, response.body()!!.data.isNullOrEmpty())
    }

    @Test
    fun `get book data from file and return response`() = runTest {
        val mockResponse = MockResponse()
        val content = Helper.readFileResource("/bookResponse.json")
        mockResponse.setBody(content)
        mockResponse.setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val response = apiService.getBookData()
        mockWebServer.takeRequest()
        assertNotEquals(false, response.body()!!.data.isNotEmpty())
        assertThat("In Too Deep").isEqualTo(response.body()!!.data[0].attributes.title)
    }

    @Test
    fun `get book and return error` () = runTest {
        val mockResponse = MockResponse()
        mockResponse.setBody("something went wrong")
        mockResponse.setResponseCode(404)

        mockWebServer.enqueue(mockResponse)
        val response = apiService.getBookData()
        mockWebServer.takeRequest()
        assertEquals(404, response.code())
        val errorMessage = response.errorBody()?.string()
        assertThat(errorMessage).isEqualTo("something went wrong")
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}