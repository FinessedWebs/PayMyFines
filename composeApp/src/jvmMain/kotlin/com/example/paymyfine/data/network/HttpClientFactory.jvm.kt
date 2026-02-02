package com.example.paymyfine.data.network

import com.example.paymyfine.data.session.SessionStore
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

actual object HttpClientFactory {

    actual fun create(sessionStore: SessionStore): HttpClient =
        HttpClient(OkHttp) {

            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 30_000
            }

            HttpResponseValidator {

                handleResponseExceptionWithRequest { cause, request ->

                    println("HTTP EXCEPTION for ${request.url}: $cause")

                    val clientException =
                        cause as? ResponseException
                            ?: return@handleResponseExceptionWithRequest

                    println("HTTP STATUS: ${clientException.response.status}")
                }
            }

            defaultRequest {
                sessionStore.getToken()?.let {
                    header("Authorization", "Bearer $it")
                }
            }
        }
}
