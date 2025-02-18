package com.app.assignmenttask.network.base

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T, val code: Int) : ApiResponse<T>()
    data class Error(val error: ApiError) : ApiResponse<Nothing>()
}

data class ApiError(val code: Int, val message: String)