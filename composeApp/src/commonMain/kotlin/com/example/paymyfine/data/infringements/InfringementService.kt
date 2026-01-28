package com.example.paymyfine.data.infringements

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class InfringementService(
    private val client: HttpClient,
    private val baseUrl: String
) {

    suspend fun getOpen(): InfringementResponse =
        client.get("$baseUrl/infringements") {
            accept(ContentType.Application.Json)
        }.body()

    suspend fun getClosed(): InfringementResponse =
        client.get("$baseUrl/infringements/closed") {
            accept(ContentType.Application.Json)
        }.body()
}
