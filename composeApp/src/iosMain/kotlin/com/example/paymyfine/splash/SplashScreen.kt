package com.example.paymyfine.splash

import androidx.compose.runtime.Composable

@Composable
actual fun SplashScreen(onFinished: () -> Unit) {
    // Immediately continue (no splash for iOS yet)
    onFinished()
}