package com.example.paymyfine.screens.details

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.example.paymyfine.data.fines.IForceItem
import com.example.paymyfine.data.network.BaseUrlProvider
import com.example.paymyfine.data.session.SessionStore
import io.ktor.client.HttpClient

class InfringementDetailsScreen(
    private val fine: IForceItem,
    private val client: HttpClient,
    private val sessionStore: SessionStore
) : Screen {

    @Composable
    override fun Content() {

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

                    Text(
                        "Notice: ${fine.noticeNumber ?: "-"}",
                        color = Color.White
                    )

                    Text(
                        "Status: ${fine.status ?: "-"}",
                        color = Color.Red
                    )

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

                    // ✅ weight applied HERE (correct)
                    Row(Modifier.fillMaxWidth()) {

                        Summary(
                            label = "Location",
                            value = fine.offenceLocation,
                            modifier = Modifier.weight(1f)
                        )

                        Summary(
                            label = "Vehicle",
                            value = fine.vehicleLicenseNumber,
                            modifier = Modifier.weight(1f)
                        )

                        Summary(
                            label = "Court",
                            value = fine.courtDate,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text("Charges", color = Color.White)

                    Text(
                        fine.chargeDescriptions
                            ?.joinToString("\n")
                            ?: "No description",
                        color = Color.Gray
                    )

                    Spacer(Modifier.height(12.dp))

                    Text("Evidence", color = Color.White)

                    val tokens =
                        fine.images.orEmpty()

                    if (tokens.isEmpty()) {

                        Text(
                            "No evidence",
                            color = Color.Gray
                        )

                    } else {

                        LazyRow {

                            items(tokens) { token ->

                                EvidenceImage(
                                    client = client,
                                    baseUrl = baseUrl,
                                    token = token
                                )
                            }
                        }
                    }


                }
            }
        }



    }
    @Composable
    fun Summary(
        label: String,
        value: String?,
        modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier.padding(8.dp)
        ) {
            Text(label, color = Color.Gray)
            Text(value ?: "-", color = Color.White)
        }
    }

}
