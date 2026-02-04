package com.example.paymyfine.screens.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.ui.BottomNavBar

class NotificationsScreen : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current!!

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            // ✅ Screen Content
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Text("Notifications Screen")
            }

            // ✅ Bottom Bar
            BottomNavBar(navigator)
        }
    }
}
