package com.example.paymyfine.data.network

import com.example.paymyfine.data.session.SessionStore
import io.ktor.client.HttpClient

expect object HttpClientFactory {
    fun create(sessionStore: SessionStore): HttpClient

}

