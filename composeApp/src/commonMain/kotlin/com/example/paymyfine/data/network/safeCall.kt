package com.example.paymyfine.data.network

import com.example.paymyfine.data.session.SessionStore
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.utils.io.errors.IOException

suspend inline fun <reified T> safeCall(
    sessionStore: SessionStore,
    crossinline block: suspend () -> HttpResponse
): ApiResult<T> {

    return try {

        val response = block()

        // ✅ AUTO LOGOUT
        if (response.status.value == 401) {
            sessionStore.clear()
            return ApiResult.Unauthorized
        }

        if (response.status.value in 200..299) {
            ApiResult.Success(response.body())
        } else {
            ApiResult.ApiError(
                response.status.value,
                response.bodyAsText()
            )
        }

    } catch (e: IOException) {
        ApiResult.NetworkError(
            "Network error. Check connection."
        )
    } catch (e: Exception) {
        ApiResult.UnknownError(
            e.message ?: "Unknown error"
        )
    }
}
