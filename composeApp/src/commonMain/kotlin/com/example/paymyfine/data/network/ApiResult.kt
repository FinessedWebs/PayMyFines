package com.example.paymyfine.data.network

sealed class ApiResult<out T> {
    data class Success<T>(val data: T): ApiResult<T>()
    data class ApiError(val code: Int, val message: String): ApiResult<Nothing>()
    data class NetworkError(val message: String): ApiResult<Nothing>()
    data class UnknownError(val message: String): ApiResult<Nothing>()
}


