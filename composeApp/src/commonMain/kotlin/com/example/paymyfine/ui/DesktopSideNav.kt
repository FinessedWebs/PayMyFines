package com.example.paymyfine.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
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
import com.example.paymyfine.data.cart.CartManager
import com.example.paymyfine.data.payment.PaymentProvider
import com.example.paymyfine.data.payment.PaymentViewModel
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.screens.home.HomeScreenRoute
import com.example.paymyfine.screens.about.AboutScreen
import com.example.paymyfine.screens.cart.CartScreen
import com.example.paymyfine.screens.notifications.NotificationsScreen
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
            onClick = { navigator.replace(ProfileScreen()) },
            icon = {
                Box(
                    Modifier
                        .size(40.dp)
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
                Icon(
                    painterResource(Res.drawable.ic_home),
                    contentDescription = "Home",
                    modifier = Modifier.size(28.dp)
                )
            },
            label = { Text("Home") }
        )

        // ABOUT
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

        // ALERTS
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

        // ⭐ CART WITH ANIMATION + BADGE
        NavigationRailItem(
            selected = false,
            onClick = { navigator.push(
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
                        contentDescription = "Cart",
                        modifier = Modifier.scale(scale)
                    )
                }
            },
            label = { Text("Cart") }
        )
    }
}

