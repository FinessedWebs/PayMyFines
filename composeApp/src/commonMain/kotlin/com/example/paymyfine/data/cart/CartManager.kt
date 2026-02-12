package com.example.paymyfine.data.cart

import com.example.paymyfine.data.fines.CartItem
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CartManager(
    private val settings: Settings,
    private val userId: String
) {

    private val key = "cart_items_$userId"

    private val json =
        Json { ignoreUnknownKeys = true }

    private val _cartFlow =
        MutableStateFlow(load())

    val cartFlow: StateFlow<List<CartItem>> =
        _cartFlow

    ///////////////////////////////////////////////////////
    // LOAD FROM STORAGE (ONLY ON INIT)
    ///////////////////////////////////////////////////////

    private fun load(): List<CartItem> {
        val raw =
            settings.getStringOrNull(key)
                ?: return emptyList()

        return runCatching {
            json.decodeFromString<List<CartItem>>(raw)
        }.getOrDefault(emptyList())
    }

    ///////////////////////////////////////////////////////
    // SAVE
    ///////////////////////////////////////////////////////

    private fun persist(list: List<CartItem>) {
        settings.putString(
            key,
            json.encodeToString(list)
        )
    }

    ///////////////////////////////////////////////////////
    // PUBLIC API (FLOW = SOURCE OF TRUTH)
    ///////////////////////////////////////////////////////

    fun getCart(): List<CartItem> =
        _cartFlow.value

    fun add(item: CartItem) {

        val updated =
            _cartFlow.value.toMutableList().apply {

                if (any {
                        it.noticeNumber == item.noticeNumber
                    }) return

                add(item)
            }

        persist(updated)
        _cartFlow.value = updated
    }

    fun remove(noticeNumber: String) {

        val updated =
            _cartFlow.value.filterNot {
                it.noticeNumber == noticeNumber
            }

        persist(updated)
        _cartFlow.value = updated
    }

    fun clear() {

        settings.remove(key)
        _cartFlow.value = emptyList()
    }

    fun totalCents(): Int =
        _cartFlow.value.sumOf {
            it.amountInCents
        }
}
