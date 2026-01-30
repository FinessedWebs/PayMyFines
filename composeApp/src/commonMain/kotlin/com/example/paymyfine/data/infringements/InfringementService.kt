package com.example.paymyfine.data.infringements

import com.example.paymyfine.data.session.SessionStore
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class InfringementService(
    private val client: HttpClient,
    private val baseUrl: String,
    private val sessionStore: SessionStore
) {

    suspend fun getOpen(idNumber: String): InfringementResponse =
        client.get("$baseUrl/infringements/$idNumber") {
            header(
                HttpHeaders.Authorization,
                "Bearer ${sessionStore.getToken()}"
            )
            accept(ContentType.Application.Json)
        }.body()

    suspend fun getClosed(idNumber: String): InfringementResponse =
        client.get("$baseUrl/infringements/closed") {
            header(
                HttpHeaders.Authorization,
                "Bearer ${sessionStore.getToken()}"
            )
            accept(ContentType.Application.Json)
        }.body()
}
