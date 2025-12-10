package com.example.paymyfinesstep.cart

import android.content.Context
import com.example.paymyfinesstep.api.ApiBackend
import com.example.paymyfinesstep.api.CartApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CartManager {

    private const val KEY = "cart_items"

    fun getCart(context: Context): MutableList<CartItem> {
        val prefs = context.getSharedPreferences("cart", Context.MODE_PRIVATE)
        val json = prefs.getString(KEY, "[]")!!
        return Gson().fromJson(json, object : TypeToken<MutableList<CartItem>>() {}.type)
    }

    fun saveCart(context: Context, list: List<CartItem>) {
        val prefs = context.getSharedPreferences("cart", Context.MODE_PRIVATE)
        prefs.edit().putString(KEY, Gson().toJson(list)).apply()
    }

    fun add(context: Context, item: CartItem) {
        val list = getCart(context)
        if (list.none { it.noticeNumber == item.noticeNumber }) {
            list.add(item)
            saveCart(context, list)
        }
    }

    fun remove(context: Context, notice: String) {
        val list = getCart(context)
        saveCart(context, list.filter { it.noticeNumber != notice })
    }

    fun clear(context: Context) {
        saveCart(context, emptyList())
    }
}

