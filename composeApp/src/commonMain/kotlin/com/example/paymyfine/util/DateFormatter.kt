package com.example.paymyfine.util

import kotlinx.datetime.*

object DateFormatter {

    fun format(iso: String): String {
        return try {
            val instant = Instant.parse(iso)
            val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())

            "${dt.dayOfMonth} ${dt.month.name.lowercase()
                .replaceFirstChar { it.uppercase() }} ${dt.year}"
        } catch (e: Exception) {
            iso
        }
    }
}
