package com.example.paymyfinesstep.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InfringementItem(
    val requestId: String?,
    val dataSource: Int,
    val issuingAuthority: String?,
    val noticeNumber: String?,
    val offenceDate: String?,
    val offenceLocation: String?,
    val offenceDemeritPoints: Int?,
    val vehicleLicenseNumber: String?,
    val infringerIdNumber: String?,
    val infringerDemeritPointBalance: Int?,
    val chargeDescriptions: List<String>?,
    val amountDueInCents: Int,
    val paymentAllowed: Boolean,
    val paymentNotAllowedReason: String?,
    val status: String?,
    val images: List<String>?,
    val caseNumber: String?,
    val courtDate: String?,
    val contemptAmountPaid: Int?,
    val summonsNumber: String?
) : Parcelable