package com.example.paymyfine.data.infringements

import com.example.paymyfine.data.fines.IForceItem

class InfringementRepository(
    private val service: InfringementService
) {

    private var cachedOpen: List<IForceItem> = emptyList()
    private var cachedClosed: List<IForceItem> = emptyList()

    suspend fun loadIndividual(
        idNumber: String,
        force: Boolean
    ): Result<List<IForceItem>> {
        return try {
            if (!force && cachedOpen.isNotEmpty()) {
                return Result.success(cachedOpen + cachedClosed)
            }

            val openResp = service.getOpen(idNumber)
            val closedResp = service.getClosed()

            cachedOpen = openResp.iForce.orEmpty()
            cachedClosed = closedResp.iForce.orEmpty()

            Result.success(cachedOpen + cachedClosed)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearCache() {
        cachedOpen = emptyList()
        cachedClosed = emptyList()
    }
}
