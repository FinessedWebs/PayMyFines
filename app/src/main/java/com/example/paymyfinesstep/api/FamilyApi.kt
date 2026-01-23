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
    val relationship: String,   // REQUIRED
    val nickname: String?,       // optional
    val email: String?,
    val cell: String?
)



data class FamilyAddResponse(
    val message: String?,
    val error: String?
)

data class FamilyMember(
    val linkId: String,       // NEW (delete uses linkId now)
    val profileId: String,    // NEW
    val fullName: String,
    val surname: String,
    val idNumber: String,
    val email: String?,
    val cell: String?,
    val createdAt: String,
    val hasAccount: Boolean,
    val relationship: String,
    val nickname: String? = null,
    val finesCount: Int = 0
)


data class FamilyProfileDto(
    val idNumber: String,
    val fullName: String,
    val surname: String,
    val email: String?,
    val cell: String?,
    val hasAccount: Boolean
)

data class FamilyAddMemberResponse(
    val status: String,
    val message: String,
    val existing: FamilyProfileDto? = null,
    val attempted: FamilyProfileDto? = null,
    val profileId: String? = null,
    val linkId: String? = null
)

data class UpdateFamilyProfileRequest(
    val idNumber: String,
    val fullName: String,
    val surname: String,
    val email: String?,
    val cell: String?
)






interface FamilyApi {

    @POST("family/add")
    suspend fun addFamily(@Body request: FamilyAddRequest): FamilyAddMemberResponse

    @GET("family/list")
    suspend fun getFamilyMembers(): List<FamilyMember>

    @POST("family/profile/update")
    suspend fun updateProfile(@Body req: UpdateFamilyProfileRequest): ApiMessageResponse


    // DELETE now uses linkId (not profile/member id)
    @DELETE("family/{linkId}")
    suspend fun deleteFamilyMember(@Path("linkId") linkId: String): Response<Unit>
}

