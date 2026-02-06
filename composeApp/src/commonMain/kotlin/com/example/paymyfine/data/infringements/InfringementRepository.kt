package com.example.paymyfine.data.infringements

import com.example.paymyfine.data.fines.IForceItem
import com.example.paymyfine.data.network.*
import com.example.paymyfine.data.session.SessionStore

class InfringementRepository(
    private val service: InfringementService,
    private val sessionStore: SessionStore
) {

    private var cached: List<IForceItem> = emptyList()

    suspend fun loadOpenFines(
        force: Boolean
    ): ApiResult<List<IForceItem>> {

        if (!force && cached.isNotEmpty()) {
            return ApiResult.Success(cached)
        }

        val result =
            safeCall<InfringementResponse>(sessionStore) {
                service.getOpen()
            }

        return when (result) {

            is ApiResult.Success -> {

                val err =
                    result.data.errorDetails?.firstOrNull()

                if (err != null) {
                    ApiResult.ApiError(
                        err.statusCode ?: 400,
                        err.message ?: "Failed to load fines"
                    )
                } else {
                    val fines =
                        result.data.iForce.orEmpty()

                    cached = fines
                    ApiResult.Success(fines)
                }
            }

            is ApiResult.ApiError ->
                ApiResult.ApiError(result.code, result.message)

            is ApiResult.NetworkError ->
                ApiResult.NetworkError(result.message)

            is ApiResult.UnknownError ->
                ApiResult.UnknownError(result.message)

            ApiResult.Unauthorized ->
                ApiResult.Unauthorized
        }
    }

    suspend fun loadFinesForMember(
        idNumber: String
    ): ApiResult<List<IForceItem>> {

        val res =
            safeCall<InfringementResponse>(sessionStore) {
                service.getForFamily(idNumber) // ✅ FIXED
            }

        return when (res) {

            is ApiResult.Success -> {
                val fines = res.data.iForce.orEmpty()
                ApiResult.Success(fines)
            }

            is ApiResult.ApiError ->
                ApiResult.ApiError(res.code, res.message)

            is ApiResult.NetworkError ->
                ApiResult.NetworkError(res.message)

            is ApiResult.UnknownError ->
                ApiResult.UnknownError(res.message)

            ApiResult.Unauthorized ->
                ApiResult.Unauthorized
        }
    }
}
