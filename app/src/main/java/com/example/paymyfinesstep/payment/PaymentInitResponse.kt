package com.example.paymyfinesstep.payment

data class PaymentInitResponse(
    val requestId: String,
    val isSuccessful: Boolean,
    val noticeNumber: String,
    val receiptNumber: String,
    val resultDescription: String,
    val amountPaidInCents: Int,
    val contemptAmountPaidInCents: Int
)
