package com.example.paymyfine.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.data.cart.CartManager
import com.example.paymyfine.data.cart.CartProvider
import com.example.paymyfine.data.session.SessionStore
import com.russhwolf.settings.Settings

@Composable
fun ResponsiveScreenShell(
    content: @Composable () -> Unit,

) {
    val navigator = LocalNavigator.current!!

    // ✅ create shared dependencies here
    val settings = remember { Settings() }
    val sessionStore = remember { SessionStore(settings) }

    val userId =
        sessionStore.getIdNumber() ?: "guest"

    val cartManager =
        remember { CartProvider.get(sessionStore) }


    BoxWithConstraints(Modifier.fillMaxSize()) {

        val isDesktop = maxWidth > 900.dp

        if (isDesktop) {

            // DESKTOP LAYOUT
            Row(Modifier.fillMaxSize()) {

                DesktopSideNav(
                    navigator = navigator,
                    sessionStore = sessionStore,
                    cartManager = cartManager,
                    modifier = Modifier.weight(0.1f)
                )

                Box(
                    modifier = Modifier.weight(0.9f)
                ) {
                    content()
                }
            }

        } else {

            // MOBILE LAYOUT
            Column(Modifier.fillMaxSize()) {

                Box(Modifier.weight(1f)) {
                    content()
                }

                val currentRoute =
                    navigator.lastItem::class.simpleName ?: ""

                BottomNavBar(navigator = navigator,
                    sessionStore = sessionStore,
                    currentRoute = currentRoute)
            }
        }
    }
}
