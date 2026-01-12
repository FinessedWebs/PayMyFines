package com.example.paymyfinesstep.cart

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartManager {

    // SharedPreferences file for carts
    const val PREF_NAME = "cart"

    // --------------------------------------------------
    // INTERNAL: resolve current user ID safely
    // --------------------------------------------------
    private fun getUserId(context: Context): String? {
        val prefs = context.getSharedPreferences(
            "paymyfines_prefs",
            Context.MODE_PRIVATE
        )
        return prefs.getString("idNumber", null)
    }

    // --------------------------------------------------
    // PUBLIC: cart key (nullable-safe)
    // --------------------------------------------------
    fun cartKey(context: Context): String? {
        val userId = getUserId(context) ?: return null
        return "cart_items_$userId"
    }

    // --------------------------------------------------
    // GET CART (SAFE)
    // --------------------------------------------------
    fun getCart(context: Context): MutableList<CartItem> {
        val key = cartKey(context) ?: return mutableListOf()

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(key, "[]") ?: "[]"

        return Gson().fromJson(
            json,
            object : TypeToken<MutableList<CartItem>>() {}.type
        )
    }

    // --------------------------------------------------
    // SAVE CART (SAFE)
    // --------------------------------------------------
    private fun saveCart(context: Context, list: List<CartItem>) {
        val key = cartKey(context) ?: return

        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(key, Gson().toJson(list))
            .apply()
    }

    // --------------------------------------------------
    // ADD ITEM
    // --------------------------------------------------
    fun add(context: Context, item: CartItem) {
        val list = getCart(context)

        if (list.none { it.noticeNumber == item.noticeNumber }) {
            list.add(item)
            saveCart(context, list)
        }
    }

    // --------------------------------------------------
    // REMOVE ITEM
    // --------------------------------------------------
    fun remove(context: Context, noticeNumber: String) {
        val list = getCart(context)
        saveCart(context, list.filter { it.noticeNumber != noticeNumber })
    }

    // --------------------------------------------------
    // CLEAR CART (CURRENT USER ONLY)
    // --------------------------------------------------
    fun clear(context: Context) {
        saveCart(context, emptyList())
    }
}
