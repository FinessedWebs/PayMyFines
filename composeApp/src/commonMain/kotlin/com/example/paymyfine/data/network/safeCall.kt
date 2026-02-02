package com.example.paymyfine.data.network

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.utils.io.errors.IOException


suspend inline fun <reified T> safeCall(
    block: suspend () -> HttpResponse
): ApiResult<T> {
    return try {

        val response = block()

        if (response.status.value in 200..299) {
            ApiResult.Success(response.body())
        } else {
            ApiResult.ApiError(
                response.status.value,
                response.bodyAsText()
            )
        }

    } catch (e: IOException) {
        ApiResult.NetworkError("Network error. Check connection.")
    } catch (e: Exception) {
        ApiResult.UnknownError(e.message ?: "Unknown error")
    }
}

