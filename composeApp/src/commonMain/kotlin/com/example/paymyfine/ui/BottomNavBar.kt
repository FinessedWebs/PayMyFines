package com.example.paymyfine.ui

import androidx.compose.foundation.layout.size
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

@Composable
fun BottomNavBar(
    navigator: Navigator
) {

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
            onClick = { navigator.replace(AboutScreen()) },
            icon = {
                Icon(
                    painterResource(Res.drawable.ic_info),
                    contentDescription = "About",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text("About") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navigator.replace(NotificationsScreen()) },
            icon = {
                Icon(
                    painterResource(Res.drawable.ic_notifications),
                    contentDescription = "Notifications",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text("Alerts") }
        )
    }
}
