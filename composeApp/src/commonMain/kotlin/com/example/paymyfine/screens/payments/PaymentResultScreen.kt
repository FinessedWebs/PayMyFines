package com.example.paymyfine.screens.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

data class PaymentResultScreen(
    val success: Boolean,
    val amountCents: Int,
    val reference: String
) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current
        val amount = amountCents / 100.0

        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                if (success) "Payment Successful"
                else "Payment Failed",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(12.dp))

            if (success) {
                val rands = amountCents / 100
                val cents = amountCents % 100

                Text("Amount Paid: R$rands.${cents.toString().padStart(2, '0')}")

                Text("Reference: $reference")
            } else {
                Text("Transaction did not complete.")
            }

            Spacer(Modifier.height(24.dp))

            Button(onClick = {
                navigator?.popUntilRoot()
            }) {
                Text("Go Home")
            }
        }
    }
}
