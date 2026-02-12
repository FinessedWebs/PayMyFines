package com.example.paymyfine.data.payment

import kotlinx.serialization.Serializable

@Serializable
data class PaymentRegisterRequest(
    val freshFine: Boolean,
    val issuingAuthorityCode: String,
    val noticeNumber: String,
    val amountInCents: Int,
    val paymentDate: String,
    val paymentProvider: Int,
    val terminalId: Int,
    val requestId: String,
    val receiptNumber: String,
    val paidNoticeNumbers: List<String> = emptyList()
)

@Serializable
data class PaymentRegisterResponse(
    val requestId: String,
    val isSuccessful: Boolean,
    val noticeNumber: String,
    val receiptNumber: String,
    val resultDescription: String,
    val amountPaidInCents: Int,
    val contemptAmountPaidInCents: Int
)
