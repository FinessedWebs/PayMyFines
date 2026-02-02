package com.example.paymyfine.data.infringements

import com.example.paymyfine.data.fines.IForceItem
import kotlin.time.Clock

import com.example.paymyfine.data.network.*

class InfringementRepository(
    private val service: InfringementService
) {

    private var cached: List<IForceItem> = emptyList()
    private var lastFetchTime = 0L

    private val cacheDurationMs = 2 * 60 * 1000 // 2 minutes

    suspend fun loadOpenFines(
        force: Boolean = false
    ): ApiResult<List<IForceItem>> {

        val now = Clock.System.now().toEpochMilliseconds()


        if (!force &&
            cached.isNotEmpty() &&
            now - lastFetchTime < cacheDurationMs
        ) {
            return ApiResult.Success(cached)
        }

        return when (
            val result = safeCall<InfringementResponse> {
                service.getOpen()
            }
        ) {

            is ApiResult.Success -> {

                val err = result.data.errorDetails?.firstOrNull()

                if (err != null) {
                    ApiResult.ApiError(
                        err.statusCode ?: 400,
                        err.message ?: "Failed to load fines"
                    )
                } else {
                    val fines = result.data.iForce.orEmpty()

                    cached = fines
                    lastFetchTime = now

                    ApiResult.Success(fines)
                }
            }

            is ApiResult.ApiError -> result
            is ApiResult.NetworkError -> result
            is ApiResult.UnknownError -> result
        }
    }

    fun clearCache() {
        cached = emptyList()
        lastFetchTime = 0
    }
}
