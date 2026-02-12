package com.example.paymyfine.data.payment

import com.example.paymyfine.data.session.SessionStore
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class PaymentService(
    private val client: HttpClient,
    private val baseUrl: String,
    private val sessionStore: SessionStore
) {

    suspend fun registerPayment(
        request: PaymentRegisterRequest
    ): PaymentRegisterResponse {

        val token = sessionStore.requireToken()

        val response: HttpResponse =
            client.post("$baseUrl/payment/register") {

                contentType(ContentType.Application.Json)

                header(
                    HttpHeaders.Authorization,
                    "Bearer $token"
                )

                setBody(request)
            }

        println("PAYMENT STATUS → ${response.status}")

        val raw = response.bodyAsText()
        println("PAYMENT BODY → $raw")

        if (response.status == HttpStatusCode.OK) {
            return response.body()
        } else {
            throw Exception("Payment failed: $raw")
        }
    }
}
