package com.example.paymyfine


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import com.example.paymyfine.data.auth.AuthRepository
import com.example.paymyfine.data.auth.AuthService
import com.example.paymyfine.data.infringements.InfringementRepository
import com.example.paymyfine.data.infringements.InfringementService
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

        val settings = remember { Settings() }
        val sessionStore = remember { SessionStore(settings) }

        val baseUrl = remember { BaseUrlProvider.get() }
        val client = remember { HttpClientFactory.create(sessionStore) }

        val authRepo = remember {
            AuthRepository(
                AuthService(client, baseUrl),
                sessionStore
            )
        }

        val infringementRepo = remember {
            InfringementRepository(
                InfringementService(client, baseUrl)
            )
        }

        val loginVm = remember { LoginViewModel(authRepo) }
        val signupVm = remember { SignupViewModel(authRepo) }

        Navigator(
            screen = LoginScreen(loginVm, signupVm)
        )
    }
}
