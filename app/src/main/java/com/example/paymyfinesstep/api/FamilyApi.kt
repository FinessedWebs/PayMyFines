package com.example.paymyfinesstep.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class FamilyAddRequest(
    val fullName: String,
    val surname: String,
    val idNumber: String,
    val email: String,
    val cell: String
)

data class FamilyAddResponse(
    val message: String?,
    val error: String?
)

data class FamilyMember(
    val id: String,
    val userId: String,
    val fullName: String,
    val surname: String,
    val idNumber: String,
    val email: String?,
    val cell: String?,
    val createdAt: String,
    val finesCount: Int = 0,
    val hasAccount: Boolean
)



interface FamilyApi {

    @POST("family/add")
    suspend fun addFamily(@Body request: FamilyAddRequest): FamilyAddResponse

    @GET("family/list")
    suspend fun getFamilyMembers(): List<FamilyMember>

    @DELETE("family/{id}")
    suspend fun deleteFamilyMember(@Path("id") id: String): Response<Unit>

}
