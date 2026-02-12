package com.example.paymyfine.screens.payments

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

                PaymentState.Processing ->
                    CircularProgressIndicator()

                is PaymentState.Result -> {
                    LaunchedEffect(s) {
                        navigator?.replace(
                            PaymentResultScreen(
                                success = s.data.isSuccessful,
                                amountCents = s.data.amountPaidInCents,
                                reference = s.data.receiptNumber
                            )
                        )
                    }
                }

                is PaymentState.Error ->
                    Text(s.message)

                else -> Unit
            }
        }
    }
}
