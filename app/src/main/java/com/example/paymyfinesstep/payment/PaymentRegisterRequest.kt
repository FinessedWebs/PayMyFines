package com.example.paymyfinesstep.payment

data class PaymentRegisterRequest(
    val freshFine: Boolean,
    val issuingAuthorityCode: String,
    val noticeNumber: String,
    val amountInCents: Int,
    val paymentDate: String,
    val paymentProvider: Int,
    val terminalId: Int,
    val requestId: String,
    val receiptNumber: String
)


data class PaymentItemRequest(
    val noticeNumber: String,
    val amountInCents: Int
)
