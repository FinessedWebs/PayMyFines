package com.example.paymyfine.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator

@Composable
fun ResponsiveScreenShell(
    content: @Composable () -> Unit
) {

    val navigator = LocalNavigator.current!!

    BoxWithConstraints(Modifier.fillMaxSize()) {

        val isDesktop = maxWidth > 900.dp

        if (isDesktop) {

            // DESKTOP LAYOUT
            Row(Modifier.fillMaxSize()) {

                DesktopSideNav(
                    navigator = navigator,
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

                BottomNavBar(navigator)
            }
        }
    }
}
