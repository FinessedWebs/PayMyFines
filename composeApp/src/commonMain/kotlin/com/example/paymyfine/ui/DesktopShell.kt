package com.example.paymyfine.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator

//Delete late

@Composable
fun DesktopShell(
    content: @Composable () -> Unit
) {

    val navigator = LocalNavigator.current!!

    Row(Modifier.fillMaxSize()) {

        // LEFT NAV (10%)
        DesktopSideNav(
            navigator = navigator,
            modifier = Modifier.weight(0.1f)
        )

        // CONTENT (90%)
        Box(
            modifier = Modifier.weight(0.9f)
        ) {
            content()
        }
    }
}
