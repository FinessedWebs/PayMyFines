package com.example.paymyfine.screens.details

import androidx.compose.runtime.Composable
import io.ktor.client.HttpClient

@Composable
expect fun EvidenceImage(
    client: HttpClient,
    baseUrl: String,
    token: String
)
