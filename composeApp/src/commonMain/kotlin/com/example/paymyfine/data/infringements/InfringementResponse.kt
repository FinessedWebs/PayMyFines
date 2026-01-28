package com.example.paymyfine.data.infringements

import com.example.paymyfine.data.fines.IForceItem

data class InfringementResponse(
    val iForce: List<IForceItem>? = null,
    val errorDetails: List<ErrorDetail>? = null
)

data class ErrorDetail(
    val statusCode: Int?,
    val message: String?
)
