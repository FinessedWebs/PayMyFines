package com.example.paymyfine.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.data.cart.CartManager
import com.example.paymyfine.data.cart.CartProvider
import com.example.paymyfine.data.payment.PaymentViewModel
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.screens.payments.PaymentScreen
import com.russhwolf.settings.Settings

class CartScreen(
    private val sessionStore: SessionStore,
    private val paymentVm: PaymentViewModel
) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current

        val manager =
            remember { CartProvider.get(sessionStore) }


        var cart by remember {
            mutableStateOf(manager.getCart())
        }

        val total =
            cart.sumOf { it.amountInCents } / 100.0

        Column(
            Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            /////////////////////////////////////
            // ⭐ BACK + TITLE ROW
            /////////////////////////////////////

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = { navigator?.pop() }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }

                Spacer(Modifier.width(8.dp))

                Text(
                    "Your Cart",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(16.dp))

            /////////////////////////////////////
            // ⭐ CART LIST
            /////////////////////////////////////

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                items(cart) { item ->

                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(6.dp)
                    ) {

                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement =
                                Arrangement.SpaceBetween
                        ) {

                            Column {
                                Text(item.description)
                                Text("R${item.amountInCents / 100.0}")
                            }

                            IconButton(
                                onClick = {
                                    manager.remove(
                                        item.noticeNumber
                                    )
                                    cart =
                                        manager.getCart()
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    null
                                )
                            }
                        }
                    }
                }
            }

            /////////////////////////////////////
            // ⭐ TOTAL
            /////////////////////////////////////

            Text(
                "Total: R$total",
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            /////////////////////////////////////
            // ⭐ CHECKOUT BUTTON
            /////////////////////////////////////

            Button(
                onClick = {
                    if (cart.isNotEmpty()) {
                        navigator?.push(
                            PaymentScreen(paymentVm)
                        )
                    }
                }
                ,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape =
                    RoundedCornerShape(40.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color.Yellow
                    )
            ) {
                Text(
                    "Proceed to Checkout",
                    color = Color.Black
                )
            }
        }
    }
}
