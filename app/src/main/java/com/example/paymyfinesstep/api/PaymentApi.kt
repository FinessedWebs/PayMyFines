package com.example.paymyfinesstep.api

import com.example.paymyfinesstep.payment.PaymentInitResponse
import com.example.paymyfinesstep.payment.PaymentRegisterRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


data class PaymentHistoryItem(
    val noticeNumber: String,
    val date: String,
    val amountCents: Int,
    val success: Boolean
)

interface PaymentApi {

    @POST("payment/register")
    suspend fun registerPayment(
        @Body body: PaymentRegisterRequest
    ): PaymentInitResponse
}






