package com.example.paymyfine.data.payment

import kotlinx.serialization.Serializable

@Serializable
data class PaymentHistoryItem(
    val noticeNumber: String,
    val amount: Int,
    val date: String,
    val receipt: String
)
