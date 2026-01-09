package com.example.paymyfinesstep.api

import com.example.paymyfinesstep.models.InfringementItem


/**
 * Represents the full response from /api/Infringement/idNumber.
 */
data class InfringementResponse(
    val eNatis: List<Any>?,
    val iForce: List<IForceItem>?,
    val errorDetails: List<ErrorDetail>?
)

/**
 * Represents a single infringement or fine item from iForce.
 */

data class LoginResponse(
    val token: String,
    val fullName: String,
    val email: String,
    val idNumber: String
)


data class IForceItem(
    val requestId: String?,
    val dataSource: Int?,
    val issuingAuthority: String?,
    val noticeNumber: String?,
    val offenceDate: String?,
    val offenceLocation: String?,
    val offenceDemeritPoints: Int?,
    val vehicleLicenseNumber: String?,
    val infringerIdNumber: String?,
    val infringerDemeritPointBalance: Int?,
    val chargeDescriptions: List<String>?,
    val amountDueInCents: Int?,
    val paymentAllowed: Boolean?,
    val paymentNotAllowedReason: String?,
    val status: String?,
    val images: List<String>?,
    val caseNumber: String?,
    val courtDate: String?,
    val contemptAmountPaid: Int?,
    val summonsNumber: String?,

    // ✅ REQUIRED: helps link fines to the family member
    val userIdNumber: String? = null
) : java.io.Serializable


/**
 * Represents any error or message from the API.
 */
data class ErrorDetail(
    val statusCode: Int?,
    val message: String?
)

/**
 * Represents the session response from /api/Session.
 */
data class SessionResponse(
    val userName: String?,
    val sessionToken: String?,
    val createdTimestamp: String?,
    val expiryTimestamp: String?,
    val timeZoneId: String?,
    val paymentProviderName: String?
)

data class UpdateProfileRequest(
    val fullName: String,
    val email: String
)
