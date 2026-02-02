package com.example.paymyfine.data.family.models

import kotlinx.serialization.Serializable

@Serializable
data class FamilyMemberDto(
    val linkId: String,
    val profileId: String? = null,
    val fullName: String,
    val surname: String,
    val idNumber: String,
    val email: String? = null,
    val cell: String? = null,
    val hasAccount: Boolean,
    val relationship: String,
    val nickname: String? = null,
    val createdAt: String? = null,
    val isDeleted: Boolean? = null
)

@Serializable
data class AddFamilyMemberRequest(
    val fullName: String,
    val surname: String,
    val idNumber: String,
    val email: String? = null,
    val cell: String? = null,
    val relationship: String,
    val nickname: String? = null
)

@Serializable
data class FamilyAddMemberResponse(
    val status: String,
    val message: String,
    val profileId: String? = null,
    val linkId: String? = null
)
