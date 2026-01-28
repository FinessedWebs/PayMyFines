package com.example.paymyfine.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val fullName: String,
    val email: String,
    val idNumber: String
)

@Serializable
data class SignupRequest(
    val fullName: String,
    val email: String,
    val idNumber: String,
    val password: String
)

@Serializable
data class ErrorResponse(
    val error: String? = null
)

@Serializable
data class MessageResponse(
    val message: String? = null
)
