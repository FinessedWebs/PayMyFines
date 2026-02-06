package com.example.paymyfine.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.example.paymyfine.screens.home.HomeScreenRoute
import com.example.paymyfine.screens.about.AboutScreen
import com.example.paymyfine.screens.notifications.NotificationsScreen
import org.jetbrains.compose.resources.painterResource
import paymyfine.composeapp.generated.resources.*
import com.example.paymyfine.screens.profile.ProfileScreen


@Composable
fun DesktopSideNav(
    navigator: Navigator,
    modifier: Modifier = Modifier
) {




    NavigationRail(
        modifier = modifier.fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        // PROFILE AVATAR BUTTON

        NavigationRailItem(
            selected = false,
            onClick = { navigator.replace(ProfileScreen()) },
            icon = {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null)
                }
            }
        )

        NavigationRailItem(
            selected = false,
            onClick = { navigator.replace(HomeScreenRoute()) },
            icon = {
                Icon(
                    painterResource(Res.drawable.ic_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text("Home") }
        )

        NavigationRailItem(
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

        NavigationRailItem(
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
