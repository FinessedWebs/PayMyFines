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
                fine.amountDueInCents?.let { (it / 100).toString().contains(q) } == true
    }
}

fun List<IForceItem>.applyAdvancedFilters(filters: FilterOptions): List<IForceItem> {
    if (filters.isEmpty) return this

    return this.filter { fine ->
        val statusMatch = filters.statuses.isEmpty() ||
                (fine.status != null && fine.status in filters.statuses)

        val amountMatch = filters.amountRanges.isEmpty() || run {
            val amount = fine.amountDueInCents ?: 0
            val range = when {
                amount < 50_000 -> AmountRange.LOW
                amount <= 100_000 -> AmountRange.MEDIUM
                else -> AmountRange.HIGH
            }
            range in filters.amountRanges
        }

        val paymentMatch = filters.paymentAllowed == null ||
                fine.paymentAllowed == filters.paymentAllowed

        // FIXED: Proper date filter with correct API
        val dateMatch = filters.dateRange == null || run {
            fine.offenceDate?.let { dateStr ->
                val fineDate = parseDateString(dateStr) ?: return@run true

                // FIXED: Use Clock.System.now() with toLocalDateTime()
                val timeZone = TimeZone.currentSystemDefault()
                val today = Clock.System.now().toLocalDateTime(timeZone).date
                val daysSince = fineDate.daysUntil(today)

                when (filters.dateRange) {
                    DateRangeFilter.LAST_30_DAYS -> daysSince <= 30
                    DateRangeFilter.LAST_3_MONTHS -> daysSince <= 90
                    DateRangeFilter.LAST_6_MONTHS -> daysSince <= 180
                    DateRangeFilter.LAST_YEAR -> daysSince <= 365
                    DateRangeFilter.ALL_TIME -> true
                    null -> true
                }
            } ?: true
        }

        val vehicleMatch = filters.vehicleFilter == VehicleFilter.ALL || run {
            val hasVehicle = !fine.vehicleLicenseNumber.isNullOrBlank() &&
                    fine.vehicleLicenseNumber != "N/A"
            when (filters.vehicleFilter) {
                VehicleFilter.WITH_VEHICLE -> hasVehicle
                VehicleFilter.WITHOUT_VEHICLE -> !hasVehicle
                else -> true
            }
        }

        val authorityMatch = filters.issuingAuthorities.isEmpty() || run {
            fine.issuingAuthority?.let { auth ->
                val courtName = auth.substringAfter("COURT:", auth).trim()
                    .takeIf { it.isNotEmpty() } ?: auth
                courtName in filters.issuingAuthorities
            } ?: false
        }

        statusMatch && amountMatch && paymentMatch && dateMatch && vehicleMatch && authorityMatch
    }
}

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