package com.example.paymyfine.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.example.paymyfine.data.auth.AuthService
import com.example.paymyfine.data.cart.CartManager
import com.example.paymyfine.data.network.BaseUrlProvider
import com.example.paymyfine.data.network.HttpClientFactory
import com.example.paymyfine.data.notification.NotificationsBadgeProvider
import com.example.paymyfine.data.payment.PaymentProvider
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.screens.*
import com.example.paymyfine.screens.about.AboutScreen
import com.example.paymyfine.screens.cart.CartScreen
import com.example.paymyfine.screens.home.HomeScreenRoute
import com.example.paymyfine.screens.notifications.NotificationsScreenRoute
import com.example.paymyfine.screens.profile.ProfileScreen
import org.jetbrains.compose.resources.painterResource
import paymyfine.composeapp.generated.resources.*

@Composable
fun DesktopSideNav(
    navigator: Navigator,
    cartManager: CartManager,
    sessionStore: SessionStore,
    modifier: Modifier = Modifier
) {

    val cart by cartManager.cartFlow.collectAsState()

    // ⭐ Notifications badge
    val badgeManager = remember {
        NotificationsBadgeProvider.create(sessionStore)
    }

    val unread by badgeManager.count.collectAsState()

    LaunchedEffect(Unit) {
        badgeManager.refresh()
    }

    // ⭐ Cart animation
    var lastCount by remember { mutableStateOf(0) }
    var animate by remember { mutableStateOf(false) }

    LaunchedEffect(cart.size) {
        if (cart.size > lastCount) animate = true
        lastCount = cart.size
    }

    val scale by animateFloatAsState(
        targetValue = if (animate) 1.25f else 1f,
        label = "cartBounce",
        finishedListener = { animate = false }
    )

    NavigationRail(
        modifier = modifier.fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        // PROFILE
        NavigationRailItem(
            selected = false,
            onClick = { navigator?.push(
                ProfileScreen(
                    sessionStore = sessionStore,
                    authService = AuthService(
                        HttpClientFactory.create(sessionStore),
                        BaseUrlProvider.get()
                    )
                )
            ) },
            icon = {
                Box(
                    Modifier.size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null)
                }
            }
        )

        // HOME
        NavigationRailItem(
            selected = false,
            onClick = { navigator.replace(HomeScreenRoute()) },
            icon = {
                Icon(painterResource(Res.drawable.ic_home), "Home",
                    Modifier.size(28.dp))
            },
            label = { Text("Home") }
        )

        // ABOUT
        NavigationRailItem(
            selected = false,
            onClick = { navigator.replace(AboutScreen(sessionStore)) },
            icon = {
                Icon(painterResource(Res.drawable.ic_info),
                    "About", Modifier.size(28.dp))
            },
            label = { Text("About") }
        )

        // ⭐ ALERTS WITH BADGE
        NavigationRailItem(
            selected = false,
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
                        painterResource(Res.drawable.ic_notifications),
                        "Alerts",
                        Modifier.size(28.dp)
                    )
                }
            },
            label = { Text("Alerts") }
        )

        // ⭐ CART
        NavigationRailItem(
            selected = false,
            onClick = {
                navigator.push(
                    CartScreen(sessionStore, PaymentProvider.vm)
                )
            },
            icon = {
                BadgedBox(
                    badge = {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = cart.isNotEmpty(),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            Badge {
                                Text(
                                    if (cart.size > 99) "99+"
                                    else cart.size.toString()
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        "Cart",
                        Modifier.scale(scale)
                    )
                }
            },
            label = { Text("Cart") }
        )
    }
}
