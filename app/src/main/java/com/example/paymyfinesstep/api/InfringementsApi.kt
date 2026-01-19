package com.example.paymyfinesstep.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming

interface InfringementsApi {

    @GET("infringements")
    suspend fun getInfringements(): InfringementResponse

    @GET("infringements/{idNumber}")
    suspend fun getFamilyInfringements(@Path("idNumber") idNumber: String): InfringementResponse

    @GET("infringements/closed")
    suspend fun getClosedInfringements(): InfringementResponse

    // ✅ NEW: Image endpoint (stream)
    @Streaming
    @GET("infringements/image/{token}")
    suspend fun getEvidenceImage(@Path("token") token: String): Response<ResponseBody>
}


