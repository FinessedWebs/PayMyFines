package com.example.paymyfine.data.auth

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class AuthService(
    private val client: HttpClient,
    private val baseUrl: String
) {

    // --------------------------------------------------
    // LOGIN
    // --------------------------------------------------
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            println("LOGIN → POST $baseUrl/auth/login")
            println("LOGIN → Payload: $request")

            val response = client.post("$baseUrl/auth/login") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(request)
            }

            println("LOGIN → HTTP status: ${response.status}")

            val rawBody = response.bodyAsText()
            println("LOGIN → Raw response body: $rawBody")

            when (response.status) {
                HttpStatusCode.OK -> {
                    val parsed = response.body<LoginResponse>()
                    println("LOGIN → Parsed success: $parsed")
                    Result.success(parsed)
                }

                HttpStatusCode.Unauthorized,
                HttpStatusCode.BadRequest -> {
                    val error = extractErrorSafely(rawBody)
                    println("LOGIN → Backend error: $error")
                    Result.failure(Exception(error))
                }

                else -> {
                    println("LOGIN → Unexpected status ${response.status}")
                    Result.failure(Exception("Unexpected error: ${response.status}"))
                }
            }

        } catch (e: Exception) {
            println("LOGIN → Exception")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // --------------------------------------------------
    // SIGNUP
    // --------------------------------------------------
    suspend fun signup(request: SignupRequest): Result<Unit> {
        return try {
            println("SIGNUP → POST $baseUrl/auth/signup")
            println("SIGNUP → Payload: $request")

            val response = client.post("$baseUrl/auth/signup") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(request)
            }

            println("SIGNUP → HTTP status: ${response.status}")

            val rawBody = response.bodyAsText()
            println("SIGNUP → Raw response body: $rawBody")

            when (response.status) {
                HttpStatusCode.OK -> {
                    println("SIGNUP → Success")
                    Result.success(Unit)
                }

                HttpStatusCode.BadRequest,
                HttpStatusCode.Unauthorized -> {
                    val error = extractErrorSafely(rawBody)
                    println("SIGNUP → Backend error: $error")
                    Result.failure(Exception(error))
                }

                HttpStatusCode.NotFound -> {
                    println("SIGNUP → ❌ Endpoint not found")
                    Result.failure(Exception("Signup endpoint not found (404)"))
                }

                else -> {
                    println("SIGNUP → Unexpected status ${response.status}")
                    Result.failure(Exception("Unexpected error: ${response.status}"))
                }
            }

        } catch (e: Exception) {
            println("SIGNUP → Exception")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // --------------------------------------------------
    // REACTIVATE
    // --------------------------------------------------
    suspend fun reactivate(email: String): Result<Unit> {
        return try {
            println("REACTIVATE → POST $baseUrl/auth/reactivate")

            val response = client.post("$baseUrl/auth/reactivate") {
                contentType(ContentType.Application.Json)
                accept(ContentType.Application.Json)
                setBody(mapOf("email" to email))
            }

            println("REACTIVATE → HTTP status: ${response.status}")
            println("REACTIVATE → Raw body: ${response.bodyAsText()}")

            Result.success(Unit)

        } catch (e: Exception) {
            println("REACTIVATE → Exception")
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // --------------------------------------------------
    // SAFE ERROR PARSER
    // --------------------------------------------------
    private fun extractErrorSafely(rawBody: String): String {
        return try {
            Json.decodeFromString<ErrorResponse>(rawBody).error
                ?: "Unknown server error"
        } catch (_: Exception) {
            rawBody.ifBlank { "Unknown server error" }
        }
    }
}
