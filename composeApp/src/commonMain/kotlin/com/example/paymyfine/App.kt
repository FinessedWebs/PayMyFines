package com.example.paymyfine

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.example.paymyfine.data.auth.AuthRepository
import com.example.paymyfine.data.auth.AuthService
import com.example.paymyfine.data.network.BaseUrlProvider
import com.example.paymyfine.data.network.HttpClientFactory
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.screens.login.LoginScreen
import com.example.paymyfine.screens.login.LoginViewModel
import com.example.paymyfine.screens.signup.SignupViewModel
import com.example.paymyfine.theme.PayMyFineTheme
import com.russhwolf.settings.Settings

@Composable
fun App() {
    PayMyFineTheme {

        val settings = Settings()
        val sessionStore = SessionStore(settings)

        val baseUrl = BaseUrlProvider.get()
        val client = HttpClientFactory.create()

        val authRepo = AuthRepository(
            AuthService(client, baseUrl),
            sessionStore
        )

        val loginVm = LoginViewModel(authRepo)
        val signupVm = SignupViewModel(authRepo)

        // 🔥 THIS IS CRITICAL
        Navigator(
            screen = LoginScreen(
                loginVm = loginVm,
                signupVm = signupVm
            )
        )
    }
}
