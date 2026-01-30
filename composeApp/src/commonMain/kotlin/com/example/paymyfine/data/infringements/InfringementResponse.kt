package com.example.paymyfine.data.infringements

import com.example.paymyfine.data.fines.IForceItem
import kotlinx.serialization.Serializable

@Serializable
data class InfringementResponse(
    val iForce: List<IForceItem>? = null,
    val errorDetails: List<ErrorDetail>? = null
)

@Serializable
data class ErrorDetail(
    val statusCode: Int?,
    val message: String?
)
