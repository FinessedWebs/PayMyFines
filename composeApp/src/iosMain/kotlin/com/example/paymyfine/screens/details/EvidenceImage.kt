package com.example.paymyfine.screens.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import io.ktor.client.HttpClient

@Composable
actual fun EvidenceImage(
    client: HttpClient,
    baseUrl: String,
    token: String
) {
    Text("Image preview not supported on iOS yet")
}
