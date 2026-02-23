package com.example.paymyfine


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.Navigator
import com.example.paymyfine.data.auth.AuthRepository
import com.example.paymyfine.data.auth.AuthService
import com.example.paymyfine.data.cart.CartManager
import com.example.paymyfine.data.network.BaseUrlProvider
import com.example.paymyfine.data.network.HttpClientFactory
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.screens.login.LoginScreen
import com.example.paymyfine.screens.login.LoginViewModel
import com.example.paymyfine.screens.signup.SignupViewModel
import com.example.paymyfine.theme.PayMyFineTheme
import com.russhwolf.settings.Settings
import com.example.paymyfine.data.payment.*
import com.example.paymyfine.data.payment.PaymentRepository
import com.example.paymyfine.data.payment.PaymentViewModel
import com.example.paymyfine.screens.home.HomeScreenRoute
import com.example.paymyfine.screens.login.LoginScreenRoute
import com.example.paymyfine.splash.SplashScreen


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

        val paymentRepo = remember {
            PaymentRepository(
                PaymentService(client, baseUrl, sessionStore),
                sessionStore
            )
        }

        val paymentVm = remember { PaymentViewModel(paymentRepo) }

        LaunchedEffect(Unit) {
            PaymentProvider.vm = paymentVm
        }

        var showSplash by remember { mutableStateOf(true) }

        if (showSplash) {
            SplashScreen {
                showSplash = false
            }
        } else {

            // if you dont want login to be skipped remove if condition and use - LoginScreenRoute(sessionStore)
            Navigator(
                if (sessionStore.isLoggedIn())
                    HomeScreenRoute()
                else
                    LoginScreenRoute(sessionStore)
            )
        }
    }
}