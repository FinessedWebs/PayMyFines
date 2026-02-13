package com.example.paymyfine.screens.notifications

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import com.example.paymyfine.data.notification.NotificationsProvider
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.ui.ResponsiveScreenShell
import com.russhwolf.settings.Settings

class NotificationsScreenRoute : Screen {

    @Composable
    override fun Content() {

        // Create sessionStore same way shell does
        val settings = remember { Settings() }
        val sessionStore = remember { SessionStore(settings) }

        val vm = remember(sessionStore) {
            NotificationsProvider.createVM(sessionStore)
        }

        ResponsiveScreenShell {
            NotificationsScreen(vm)
        }
    }
}
