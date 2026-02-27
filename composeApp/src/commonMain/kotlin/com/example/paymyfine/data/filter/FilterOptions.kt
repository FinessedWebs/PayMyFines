package com.example.paymyfine.data.filter

data class FilterOptions(
    val statuses: List<String> = emptyList(),
    val amountRanges: List<AmountRange> = emptyList(),
    val paymentAllowed: Boolean? = null,
    val dateRange: DateRangeFilter? = null,
    val issuingAuthorities: List<String> = emptyList(),
    val vehicleFilter: VehicleFilter = VehicleFilter.ALL
) {
    val isEmpty: Boolean
        get() = statuses.isEmpty() &&
                amountRanges.isEmpty() &&
                paymentAllowed == null &&
                dateRange == null &&
                issuingAuthorities.isEmpty() &&
                vehicleFilter == VehicleFilter.ALL

    val activeFilterCount: Int
        get() {
            var count = 0
            if (statuses.isNotEmpty()) count++
            if (amountRanges.isNotEmpty()) count++
            if (paymentAllowed != null) count++
            if (dateRange != null) count++
            if (issuingAuthorities.isNotEmpty()) count++
            if (vehicleFilter != VehicleFilter.ALL) count++
            return count
        }
}

enum class AmountRange {
    LOW,      // < R500
    MEDIUM,   // R500 - R1000
    HIGH      // > R1000
}

enum class DateRangeFilter {
    LAST_30_DAYS,
    LAST_3_MONTHS,
    LAST_6_MONTHS,
    LAST_YEAR,
    ALL_TIME
}

enum class VehicleFilter {
    ALL,
    WITH_VEHICLE,
    WITHOUT_VEHICLE
}