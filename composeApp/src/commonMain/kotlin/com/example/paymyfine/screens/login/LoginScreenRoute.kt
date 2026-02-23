package com.example.paymyfine.screens.login

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import com.example.paymyfine.data.auth.AuthRepository
import com.example.paymyfine.data.auth.AuthService
import com.example.paymyfine.data.network.BaseUrlProvider
import com.example.paymyfine.data.network.HttpClientFactory
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.screens.signup.SignupViewModel

class LoginScreenRoute(
    private val sessionStore: SessionStore
) : Screen {

    @Composable
    override fun Content() {

        val client = remember {
            HttpClientFactory.create(sessionStore)
        }

        val baseUrl = BaseUrlProvider.get()

        val repo = remember {
            AuthRepository(
                AuthService(client, baseUrl),
                sessionStore
            )
        }

        val loginVm = remember { LoginViewModel(repo) }
        val signupVm = remember { SignupViewModel(repo) }

        LoginScreen(
            loginVm = loginVm,
            signupVm = signupVm
        ).Content()
    }
}
