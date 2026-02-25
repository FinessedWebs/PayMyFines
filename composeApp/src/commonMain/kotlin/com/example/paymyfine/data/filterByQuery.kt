package com.example.paymyfine.data

import com.example.paymyfine.data.fines.IForceItem

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

                // Amount search (R800 or 800)
                fine.amountDueInCents?.let {
                    (it / 100).toString().contains(q)
                } == true    }
}