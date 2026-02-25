package com.example.paymyfine.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.example.paymyfine.data.notification.NotificationsBadgeProvider
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.screens.about.AboutScreen
import com.example.paymyfine.screens.home.HomeScreenRoute
import com.example.paymyfine.screens.notifications.NotificationsScreenRoute

@Composable
fun BottomNavBar(
    navigator: Navigator,
    sessionStore: SessionStore,
    currentRoute: String // pass current screen key
) {

    val badgeManager = remember {
        NotificationsBadgeProvider.create(sessionStore)
    }

    val unread by badgeManager.count.collectAsState()

    LaunchedEffect(Unit) {
        badgeManager.refresh()
    }

    val iconTint = MaterialTheme.colorScheme.onSurfaceVariant

    NavigationBar(
        containerColor = Color.White
    ) {

        // HOME
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { navigator.replace(HomeScreenRoute()) },
            icon = {
                Icon(
                    imageVector =
                        if (currentRoute == "home")
                            Icons.Filled.Home
                        else
                            Icons.Outlined.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp),
                    tint = iconTint
                )
            },
            label = {
                Text(
                    "Home",
                    color = iconTint
                )
            }
        )

        // ABOUT
        NavigationBarItem(
            selected = currentRoute == "about",
            onClick = { navigator.replace(AboutScreen(sessionStore)) },
            icon = {
                Icon(
                    imageVector =
                        if (currentRoute == "about")
                            Icons.Filled.Info
                        else
                            Icons.Outlined.Info,
                    contentDescription = "About",
                    modifier = Modifier.size(24.dp),
                    tint = iconTint
                )
            },
            label = {
                Text(
                    "About",
                    color = iconTint
                )
            }
        )

        // NOTIFICATIONS
        NavigationBarItem(
            selected = currentRoute == "alerts",
            onClick = { navigator.replace(NotificationsScreenRoute()) },
            icon = {
                BadgedBox(
                    badge = {
                        if (unread > 0) {
                            Badge {
                                Text(
                                    if (unread > 99) "99+"
                                    else unread.toString()
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector =
                            if (currentRoute == "alerts")
                                Icons.Filled.Notifications
                            else
                                Icons.Outlined.Notifications,
                        contentDescription = "Alerts",
                        modifier = Modifier.size(24.dp),
                        tint = iconTint
                    )
                }
            },
            label = {
                Text(
                    "Alerts",
                    color = iconTint
                )
            }
        )
    }
}