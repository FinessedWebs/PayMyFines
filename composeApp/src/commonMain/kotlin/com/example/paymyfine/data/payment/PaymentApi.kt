package com.example.paymyfine.data.payment

import com.example.paymyfine.data.session.SessionStore
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class PaymentApi(
    private val client: HttpClient,
    private val baseUrl: String,
    private val sessionStore: SessionStore
) {
    suspend fun registerPayment(
        body: PaymentRegisterRequest
    ): PaymentRegisterResponse {
        val token = sessionStore.requireToken()

        return client.post("$baseUrl/payment/register") {
            contentType(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(body)
        }.body()
    }
}
