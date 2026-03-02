package com.example.paymyfine.data.family

import com.example.paymyfine.data.family.models.*
import com.example.paymyfine.data.network.*
import com.example.paymyfine.data.session.SessionStore

class FamilyRepository(
    private val service: FamilyService,
    private val sessionStore: SessionStore
) {

    private var cached: List<FamilyMemberDto> = emptyList()

    fun clearCache() {
        cached = emptyList()
    }

    suspend fun getMembers(
        force: Boolean = false
    ): ApiResult<List<FamilyMemberDto>> {

        if (!force && cached.isNotEmpty()) {
            return ApiResult.Success(cached)
        }

        return when (
            val res = safeCall<List<FamilyMemberDto>>(sessionStore) {
                service.listMembers()
            }
        ) {
            is ApiResult.Success -> {
                cached = res.data
                ApiResult.Success(res.data)
            }

            is ApiResult.ApiError -> res
            is ApiResult.NetworkError -> res
            is ApiResult.UnknownError -> res
            ApiResult.Unauthorized -> res
        }
    }

    suspend fun addMember(req: AddFamilyMemberRequest) =
        safeCall<FamilyAddMemberResponse>(sessionStore) {
            service.addMember(req)
        }

    suspend fun deleteMember(linkId: String): ApiResult<Unit> {
        val result = safeCall<Unit>(sessionStore) {
            service.deleteMember(linkId)
        }

        if (result is ApiResult.Success) {
            cached = cached.filterNot { it.linkId == linkId }
        }

        return result
    }
}
