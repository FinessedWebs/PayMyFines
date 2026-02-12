package com.example.paymyfine.data.fines

import kotlinx.serialization.Serializable

@Serializable
data class CartItem(
    val noticeNumber: String,
    val description: String,
    val amountInCents: Int
)


@Serializable
data class IForceItem(
    val requestId: String? = null,
    val dataSource: Int? = null,
    val issuingAuthority: String? = null,
    val noticeNumber: String? = null,
    val offenceDate: String? = null,
    val offenceLocation: String? = null,
    val offenceDemeritPoints: Int? = null,
    val vehicleLicenseNumber: String? = null,
    val infringerIdNumber: String? = null,
    val infringerDemeritPointBalance: Int? = null,
    val chargeDescriptions: List<String>? = null,
    val amountDueInCents: Int? = null,
    val paymentAllowed: Boolean? = null,
    val paymentNotAllowedReason: String? = null,
    val status: String? = null,
    val images: List<String>? = null,
    val caseNumber: String? = null,
    val courtDate: String? = null,
    val contemptAmountPaid: Double? = null,
    val summonsNumber: String? = null
)

