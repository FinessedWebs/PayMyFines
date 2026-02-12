package com.example.paymyfine.screens.details

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.data.cart.CartManager
import com.example.paymyfine.data.cart.CartProvider
import com.example.paymyfine.data.fines.CartItem
import com.example.paymyfine.data.fines.IForceItem
import com.example.paymyfine.data.network.BaseUrlProvider
import com.example.paymyfine.data.payment.PaymentProvider
import com.example.paymyfine.data.payment.PaymentViewModel
import com.example.paymyfine.data.session.SessionStore
import com.example.paymyfine.screens.cart.CartScreen
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient

class InfringementDetailsScreen(
    private val fine: IForceItem,
    private val client: HttpClient,
    private val sessionStore: SessionStore
) : Screen {

    @Composable
    override fun Content() {
        InfringementDetailsContent(
            fine = fine,
            client = client,
            sessionStore = sessionStore
        )
    }
}

//////////////////////////////////////////////////////
// CONTENT
//////////////////////////////////////////////////////

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfringementDetailsContent(
    fine: IForceItem,
    client: HttpClient,
    sessionStore: SessionStore
) {

    var bounce by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (bounce) 1.15f else 1f,
        label = "addBounce",
        finishedListener = { bounce = false }
    )


    val navigator = LocalNavigator.current

    val settings = remember { Settings() }

    val userId =
        sessionStore.getIdNumber() ?: "guest"

    val cartManager =
        remember { CartProvider.get(sessionStore) }


    var showSheet by remember {
        mutableStateOf(false)
    }

    val baseUrl = BaseUrlProvider.get()

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        Text(
            "Infringement Details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF0B1120)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {

            Column(Modifier.padding(20.dp)) {

                Text("Notice: ${fine.noticeNumber}", color = Color.White)
                Text("Status: ${fine.status}", color = Color.Red)

                Spacer(Modifier.height(12.dp))

                val amount =
                    (fine.amountDueInCents ?: 0) / 100.0

                Text(
                    "R$amount",
                    color = Color(0xFF22C55E),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                Row(Modifier.fillMaxWidth()) {

                    Summary("Location", fine.offenceLocation, Modifier.weight(1f))
                    Summary("Vehicle", fine.vehicleLicenseNumber, Modifier.weight(1f))
                    Summary("Court", fine.courtDate, Modifier.weight(1f))
                }

                Spacer(Modifier.height(12.dp))

                Text("Charges", color = Color.White)

                Text(
                    fine.chargeDescriptions?.joinToString("\n")
                        ?: "No description",
                    color = Color.Gray
                )

                Spacer(Modifier.height(12.dp))

                Text("Evidence", color = Color.White)

                val tokens = fine.images.orEmpty()

                if (tokens.isEmpty()) {
                    Text("No evidence", color = Color.Gray)
                } else {
                    LazyRow(
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        items(tokens) { token ->
                            EvidenceImage(
                                client = client,
                                baseUrl = baseUrl,
                                token = token
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {

                        cartManager.add(
                            CartItem(
                                noticeNumber = fine.noticeNumber ?: return@Button,
                                description = fine.offenceLocation ?: "Fine",
                                amountInCents = fine.amountDueInCents ?: 0
                            )
                        )

                        bounce = true
                        showSheet = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(scale)
                ) {
                    Text("Add to Cart")
                }

            }
        }
    }

    //////////////////////////////////////////////////////
    // Bottom Sheet
    //////////////////////////////////////////////////////



    if (showSheet) {



        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
            }
        ) {



            Column(
                Modifier.padding(24.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    "Item added to cart",
                    style =
                        MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        showSheet = false
                        navigator?.push(
                            CartScreen(sessionStore, PaymentProvider.vm)
                        )


                    }
                ) {
                    Text("Go to Cart")
                }
            }
        }
    }
}

//////////////////////////////////////////////////////
// Helper
//////////////////////////////////////////////////////

@Composable
fun Summary(
    label: String,
    value: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier.padding(8.dp)) {
        Text(label, color = Color.Gray)
        Text(value ?: "-", color = Color.White)
    }
}



