package com.example.paymyfine.screens.settings

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import com.example.paymyfine.data.auth.AuthService
import com.example.paymyfine.data.network.BaseUrlProvider
import com.example.paymyfine.data.network.HttpClientFactory
import com.example.paymyfine.data.session.SessionStore

class SettingsScreenRoute(
    private val sessionStore: SessionStore
) : Screen {

    @Composable
    override fun Content() {

        val client = remember {
            HttpClientFactory.create(sessionStore)
        }

        val authService = remember {
            AuthService(client, BaseUrlProvider.get())
        }

        SettingsScreen(
            sessionStore = sessionStore,
            authService = authService
        ).Content()
    }
}
