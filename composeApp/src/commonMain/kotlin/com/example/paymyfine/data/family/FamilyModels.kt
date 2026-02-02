package com.example.paymyfine.data.family

import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class FamilyMember(
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
    val createdAt: String? = null
)

@Serializable
data class FamilyAddRequest(
    val fullName: String,
    val surname: String,
    val idNumber: String,
    val relationship: String,
    val nickname: String? = null,
    val email: String? = null,
    val cell: String? = null
)

@Serializable
data class FamilyAddMemberResponse(
    val status: String,
    val message: String,
    val existing: FamilyProfileDto? = null,
    val attempted: FamilyProfileDto? = null,
    val profileId: String? = null,
    val linkId: String? = null
)

@Serializable
data class FamilyProfileDto(
    val idNumber: String,
    val fullName: String,
    val surname: String,
    val email: String? = null,
    val cell: String? = null,
    val hasAccount: Boolean
)
