package com.example.paymyfine.screens.payments

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.paymyfine.data.payment.PaymentViewModel

class PaymentScreen(
    private val vm: PaymentViewModel
) : Screen {

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current

        val state by vm.state.collectAsState()

        LaunchedEffect(Unit) {
            vm.startPayment()
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            when (val s = state) {

                ////////////////////////////////////
                // ⭐ PROGRESS UI
                ////////////////////////////////////

                is PaymentState.Progress -> {

                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Paying ${s.current} of ${s.total} fines..."
                        )
                    }
                }

                ////////////////////////////////////
                // ⭐ INITIAL LOADING
                ////////////////////////////////////

                PaymentState.Processing ->
                    CircularProgressIndicator()

                ////////////////////////////////////
                // ⭐ SUCCESS
                ////////////////////////////////////

                is PaymentState.Result -> {
                    LaunchedEffect(s) {
                        navigator?.replace(
                            PaymentResultScreen(
                                success =
                                    s.data.isSuccessful,
                                amountCents =
                                    s.data.amountPaidInCents,
                                reference =
                                    s.data.receiptNumber
                            )
                        )
                    }
                }

                ////////////////////////////////////
                // ⭐ ERROR
                ////////////////////////////////////

                is PaymentState.Error ->
                    Text(s.message)

                else -> Unit
            }
        }
    }
}
