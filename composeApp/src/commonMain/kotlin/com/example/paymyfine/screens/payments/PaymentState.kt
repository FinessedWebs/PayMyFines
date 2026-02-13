package com.example.paymyfine.screens.payments


import com.example.paymyfine.data.payment.PaymentRegisterResponse

sealed class PaymentState {
    object Idle : PaymentState()
    object Processing : PaymentState()

    data class Progress(
        val current: Int,
        val total: Int
    ) : PaymentState()

    data class Result(
        val data: PaymentRegisterResponse
    ) : PaymentState()

    data class Error(
        val message: String
    ) : PaymentState()
}

