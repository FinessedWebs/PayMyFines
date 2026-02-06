package com.example.paymyfine.screens.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.ui.BottomNavBar
import com.example.paymyfine.ui.DesktopShell
import com.example.paymyfine.ui.ResponsiveScreenShell

class NotificationsScreen : Screen {

    @Composable
    override fun Content() {

        ResponsiveScreenShell {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Notifications Screen")
            }
        }
    }
}

