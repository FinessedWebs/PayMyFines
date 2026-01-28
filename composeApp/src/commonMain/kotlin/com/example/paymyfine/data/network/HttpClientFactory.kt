package com.example.paymyfine.data.network

import io.ktor.client.HttpClient

expect object HttpClientFactory {
    fun create(): HttpClient
}
