package com.example.paymyfine


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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


@Composable
fun App() {

    PayMyFineTheme {

        // SETTINGS
        val settings = remember { Settings() }

        // SESSION
        val sessionStore = remember { SessionStore(settings) }

        // NETWORK
        val baseUrl = remember { BaseUrlProvider.get() }
        val client = remember { HttpClientFactory.create(sessionStore) }

        // AUTH
        val authRepo = remember {
            AuthRepository(
                AuthService(client, baseUrl),
                sessionStore
            )
        }

        val loginVm = remember { LoginViewModel(authRepo) }
        val signupVm = remember { SignupViewModel(authRepo) }

        // CART (per user)
        val userId = sessionStore.getIdNumber() ?: "guest"
        val cartManager = remember { CartManager(settings, userId) }

        // PAYMENT
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

        // START NAVIGATION
        Navigator(
            screen = LoginScreen(
                loginVm,
                signupVm
            )
        )
    }
}
