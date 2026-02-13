package com.example.paymyfine.data.payment

import com.russhwolf.settings.Settings
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object PaymentHistoryStore {

    private val json = Json
    private val settings = Settings()

    private const val KEY = "payment_history"

    fun save(item: PaymentHistoryItem) {

        val list = get().toMutableList()
        list.add(0, item)

        settings.putString(
            KEY,
            json.encodeToString(list)
        )
    }

    fun get(): List<PaymentHistoryItem> =
        settings.getStringOrNull(KEY)?.let {
            json.decodeFromString(it)
        } ?: emptyList()
}
