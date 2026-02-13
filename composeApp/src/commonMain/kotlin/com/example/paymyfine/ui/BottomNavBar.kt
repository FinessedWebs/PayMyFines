package com.example.paymyfine.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.example.paymyfine.screens.home.HomeScreenRoute
import com.example.paymyfine.screens.about.AboutScreen
import com.example.paymyfine.screens.notifications.NotificationsScreen
import org.jetbrains.compose.resources.painterResource
import paymyfine.composeapp.generated.resources.*
import com.example.paymyfine.screens.profile.ProfileScreen
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.paymyfine.data.notification.NotificationsBadgeProvider
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.screens.notifications.NotificationsScreenRoute


@Composable
fun BottomNavBar(
    navigator: Navigator,
    sessionStore: SessionStore
) {

    val badgeManager = remember {
        NotificationsBadgeProvider.create(sessionStore)
    }

    val unread by badgeManager.count.collectAsState()

    LaunchedEffect(Unit) {
        badgeManager.refresh()
    }

    NavigationBar(
        containerColor = Color.White // ✅ White background
    ) {

        NavigationBarItem(
            selected = false,
            onClick = { navigator.replace(HomeScreenRoute()) },
            icon = {
                Icon(
                    painterResource(Res.drawable.ic_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(28.dp) // ✅ Bigger icon
                )
            },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navigator.replace(AboutScreen(sessionStore))
            },
            icon = {
                Icon(
                    painterResource(Res.drawable.ic_info),
                    contentDescription = "About",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text("About") }
        )

        /*NavigationBarItem(
            selected = false,
            onClick = {navigator.replace(
                NotificationsScreenRoute()
            )

            },
            icon = {
                Icon(
                    painterResource(Res.drawable.ic_notifications),
                    contentDescription = "Notifications",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text("Alerts") }
        )*/

        NavigationBarItem(
            selected = false,
            onClick = {
                navigator.replace(NotificationsScreenRoute())
            },
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
                        painterResource(Res.drawable.ic_notifications),
                        contentDescription = "Notifications",
                        modifier = Modifier.size(28.dp)
                    )
                }
            },
            label = { Text("Alerts") }
        )

    }
}
