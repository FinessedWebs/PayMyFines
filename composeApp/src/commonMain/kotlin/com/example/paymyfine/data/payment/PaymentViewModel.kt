package com.example.paymyfine.data.payment

import com.example.paymyfine.screens.payments.PaymentState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class PaymentViewModel(
    private val repo: PaymentRepository
) {

    private val scope =
        CoroutineScope(Dispatchers.Main)

    private val _state =
        MutableStateFlow<PaymentState>(
            PaymentState.Idle
        )

    val state: StateFlow<PaymentState> =
        _state

    fun startPayment() {
        scope.launch {

            try {
                val results = repo.checkout { current, total ->
                    _state.value =
                        PaymentState.Progress(current, total)
                }

                val totalPaid =
                    results.sumOf { it.amountPaidInCents }

                val summary =
                    results.last().copy(
                        amountPaidInCents = totalPaid
                    )

                _state.value =
                    PaymentState.Result(summary)

            } catch (e: Exception) {
                _state.value =
                    PaymentState.Error(e.message ?: "Payment failed")
            }
        }
    }

}
