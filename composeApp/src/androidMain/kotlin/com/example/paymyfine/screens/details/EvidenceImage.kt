package com.example.paymyfine.screens.details

import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import io.ktor.client.HttpClient

@Composable
actual fun EvidenceImage(
    client: HttpClient,
    baseUrl: String,
    token: String
) {
    AsyncImage(
        model = "$baseUrl/infringements/image/stream?evidenceToken=$token",
        contentDescription = null
    )
}
