package com.example.paymyfine.data


import com.example.paymyfine.data.filter.AmountRange
import com.example.paymyfine.data.filter.DateRangeFilter
import com.example.paymyfine.data.filter.FilterOptions
import com.example.paymyfine.data.filter.VehicleFilter
import com.example.paymyfine.data.fines.IForceItem
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime

// YOUR EXISTING FUNCTION - KEEP AS IS
fun List<IForceItem>.filterByQuery(query: String): List<IForceItem> {
    if (query.isBlank()) return this

    val q = query.trim().lowercase()

    return filter { fine ->
        fine.noticeNumber?.lowercase()?.contains(q) == true ||
                fine.vehicleLicenseNumber?.lowercase()?.contains(q) == true ||
                fine.issuingAuthority?.lowercase()?.contains(q) == true ||
                fine.offenceLocation?.lowercase()?.contains(q) == true ||
                fine.status?.lowercase()?.contains(q) == true ||
                fine.summonsNumber?.lowercase()?.contains(q) == true ||
                fine.amountDueInCents?.let {
                    (it / 100).toString().contains(q)
                } == true
    }
}

// ADD THIS NEW FUNCTION AT THE END
fun List<IForceItem>.applyAdvancedFilters(filters: FilterOptions): List<IForceItem> {
    if (filters.isEmpty) return this

    return filter { fine ->
        // Status filter
        val statusMatch = filters.statuses.isEmpty() ||
                fine.status in filters.statuses

        // Amount range filter
        val amountMatch = filters.amountRanges.isEmpty() || run {
            val amount = fine.amountDueInCents ?: 0
            val range = when {
                amount < 50_000 -> AmountRange.LOW      // < R500
                amount <= 100_000 -> AmountRange.MEDIUM // R500 - R1000
                else -> AmountRange.HIGH                // > R1000
            }
            range in filters.amountRanges
        }

        // Payment allowed filter
        val paymentMatch = filters.paymentAllowed == null ||
                fine.paymentAllowed == filters.paymentAllowed

        // Date range filter
        val dateMatch = filters.dateRange == null || run {
            (fine.offenceDate?.let { dateStr ->
                val fineDate = parseDateString(dateStr) ?: return@run true
                val today = Clock.System.now()
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date

                val daysSince = fineDate.daysUntil(today)

                when (filters.dateRange) {
                    DateRangeFilter.LAST_30_DAYS -> daysSince <= 30
                    DateRangeFilter.LAST_3_MONTHS -> daysSince <= 90
                    DateRangeFilter.LAST_6_MONTHS -> daysSince <= 180
                    DateRangeFilter.LAST_YEAR -> daysSince <= 365
                    DateRangeFilter.ALL_TIME -> true
                    null -> true
                }
            } ?: true) as Boolean
        }

        // Vehicle filter
        val vehicleMatch = filters.vehicleFilter == VehicleFilter.ALL || run {
            val hasVehicle = !fine.vehicleLicenseNumber.isNullOrBlank() &&
                    fine.vehicleLicenseNumber != "N/A"
            when (filters.vehicleFilter) {
                VehicleFilter.WITH_VEHICLE -> hasVehicle
                VehicleFilter.WITHOUT_VEHICLE -> !hasVehicle
                else -> true
            }
        }

        // Issuing authority filter
        val authorityMatch = filters.issuingAuthorities.isEmpty() || run {
            fine.issuingAuthority?.let { auth ->
                val courtName = extractCourtName(auth)
                courtName in filters.issuingAuthorities
            } ?: false
        }

        statusMatch && amountMatch && paymentMatch && dateMatch && vehicleMatch && authorityMatch
    }
}

// Helper functions
private fun parseDateString(dateStr: String): LocalDate? {
    return try {
        if (dateStr.length == 8) {
            val year = dateStr.substring(0, 4).toInt()
            val month = dateStr.substring(4, 6).toInt()
            val day = dateStr.substring(6, 8).toInt()
            LocalDate(year, month, day)
        } else null
    } catch (e: Exception) {
        null
    }
}

private fun extractCourtName(issuingAuthority: String): String {
    return issuingAuthority
        .substringAfter("COURT:", issuingAuthority)
        .trim()
        .takeIf { it.isNotEmpty() } ?: issuingAuthority
}