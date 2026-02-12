package com.example.paymyfine.data.payment

import com.example.paymyfine.screens.payments.PaymentState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*


class PaymentViewModel(
    private val repo: PaymentRepository
) {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val _state = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val state: StateFlow<PaymentState> = _state

    fun startPayment() {
        scope.launch {
            _state.value = PaymentState.Processing

            try {
                val result = repo.checkout()
                _state.value = PaymentState.Result(result)
            } catch (e: Exception) {
                _state.value = PaymentState.Error(e.message ?: "Payment failed")
            }
        }
    }
}