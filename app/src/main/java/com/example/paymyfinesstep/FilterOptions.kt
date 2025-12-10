package com.example.paymyfinesstep

data class FilterOptions(
    val statuses: MutableSet<String> = mutableSetOf(),
    val severities: MutableSet<String> = mutableSetOf(),
    val paymentFlags: MutableSet<String> = mutableSetOf(),
    var dateRange: String? = null,                      // "30", "180", "365", null = all
    val issuingAuthorities: MutableSet<String> = mutableSetOf()
) {
    val isEmpty: Boolean
        get() = statuses.isEmpty()
                && severities.isEmpty()
                && paymentFlags.isEmpty()
                && dateRange == null
                && issuingAuthorities.isEmpty()
}