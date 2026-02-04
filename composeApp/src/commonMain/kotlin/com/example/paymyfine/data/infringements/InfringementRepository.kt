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

            is ApiResult.ApiError -> result
            is ApiResult.NetworkError -> result
            is ApiResult.UnknownError -> result
            ApiResult.Unauthorized -> ApiResult.Unauthorized
        }
    }
}
