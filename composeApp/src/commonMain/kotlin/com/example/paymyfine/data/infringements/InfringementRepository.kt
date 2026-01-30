package com.example.paymyfine.data.infringements

import com.example.paymyfine.data.fines.IForceItem

class InfringementRepository(
    private val service: InfringementService
) {

    private var cached: List<IForceItem> = emptyList()

    suspend fun loadIndividual(
        idNumber: String,
        force: Boolean
    ): Result<List<IForceItem>> {
        return try {
            if (cached.isNotEmpty() && !force) {
                return Result.success(cached)
            }

            val open = service.getOpen(idNumber)
            val closed = service.getClosed(idNumber)

            val err = open.errorDetails?.firstOrNull()
            if (err != null) {
                return Result.failure(
                    Exception(err.message ?: "Failed to load fines")
                )
            }

            cached = open.iForce.orEmpty() + closed.iForce.orEmpty()
            Result.success(cached)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
