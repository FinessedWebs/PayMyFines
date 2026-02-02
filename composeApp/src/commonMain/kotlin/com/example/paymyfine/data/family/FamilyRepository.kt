package com.example.paymyfine.data.family

import com.example.paymyfine.data.family.models.*
import com.example.paymyfine.data.network.*

class FamilyRepository(
    private val service: FamilyService
) {

    private var cached: List<FamilyMemberDto> = emptyList()

    suspend fun getMembers(
        force: Boolean = false
    ): ApiResult<List<FamilyMemberDto>> {

        if (cached.isNotEmpty() && !force) {
            return ApiResult.Success(cached)
        }

        return when (
            val result = safeCall<List<FamilyMemberDto>> {
                service.listMembers()
            }
        ) {

            is ApiResult.Success -> {
                cached = result.data
                result
            }

            else -> result
        }
    }

    suspend fun addMember(
        req: AddFamilyMemberRequest
    ): ApiResult<FamilyAddMemberResponse> =
        safeCall {
            service.addMember(req)
        }

    suspend fun deleteMember(
        linkId: String
    ): ApiResult<Unit> =
        safeCall {
            service.deleteMember(linkId)
        }

    fun clearCache() {
        cached = emptyList()
    }
}
