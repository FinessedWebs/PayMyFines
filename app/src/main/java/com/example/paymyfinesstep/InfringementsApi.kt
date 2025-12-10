package com.example.paymyfinesstep

import com.example.paymyfinesstep.api.InfringementResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface InfringementsApi {

    // 🔹 Logged-in user infringements
    @GET("infringements")
    suspend fun getInfringements(): InfringementResponse

    // 🔹 Family member infringements (passing ID number)
    @GET("infringements/{idNumber}")
    suspend fun getFamilyInfringements(
        @Path("idNumber") idNumber: String
    ): InfringementResponse

}