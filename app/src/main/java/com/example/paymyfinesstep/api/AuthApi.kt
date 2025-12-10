package com.example.paymyfinesstep.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class SignupRequest(
    val fullName: String,
    val email: String,
    val idNumber: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String?,
    val fullName: String?,
    val email: String?,
    val idNumber: String?
)

data class UserResponse(
    val fullName: String,
    val email: String,
    val idNumber: String
)

data class SimpleResponse(
    val message: String?,
    val error: String?
)

data class ReactivateRequest(
    val email: String
)




interface AuthApi {
    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<Unit>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(
        /*@Header("Authorization") token: String*/
    ): UserResponse

    @POST("auth/deactivate")
    suspend fun deactivate(
        /*@Header("Authorization") token: String*/
    ): SimpleResponse

    @POST("auth/reactivate")
    suspend fun reactivate(
        @Body request: ReactivateRequest
    ): SimpleResponse

    @POST("/auth/deactivate")
    suspend fun deactivateAccount(): Response<Unit>




}
