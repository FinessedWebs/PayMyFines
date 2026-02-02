package com.example.paymyfine.data.infringements

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class InfringementService(
    private val client: HttpClient,
    private val baseUrl: String
) {

    // ✅ Individual fines (JWT provides idNumber)
    suspend fun getOpen(): HttpResponse =
        client.get("$baseUrl/infringements")

    // ✅ Closed fines
    suspend fun getClosed(): HttpResponse =
        client.get("$baseUrl/infringements/closed")

    // ✅ Family member fines
    suspend fun getForFamily(idNumber: String): HttpResponse =
        client.get("$baseUrl/infringements/$idNumber")
}
