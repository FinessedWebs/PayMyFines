package com.example.paymyfine.data.fines

data class IForceItem(
    val noticeNumber: String?,
    val offenceDate: String?,
    val offenceLocation: String?,
    val chargeDescriptions: List<String>?,
    val amountDueInCents: Int?,
    val issuingAuthority: String?,
    val status: String?,
    val paymentAllowed: Boolean?,
    val images: List<String>?,
    val userIdNumber: String? = null
)
