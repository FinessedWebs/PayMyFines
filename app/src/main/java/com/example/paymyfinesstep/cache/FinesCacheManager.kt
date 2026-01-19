package com.example.paymyfinesstep.cache

import android.content.Context
import com.example.paymyfinesstep.api.IForceItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object FinesCacheManager {

    private const val PREF_NAME = "fines_cache"
    private const val KEY_FINE_LIST = "cached_fines_json"
    private const val KEY_LAST_FETCH_TIME = "cached_fines_last_fetch"

    // ✅ Cache duration (example: 10 minutes)
    private const val CACHE_TTL_MS = 10 * 60 * 1000L

    private val gson = Gson()

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveFines(context: Context, fines: List<IForceItem>) {
        val json = gson.toJson(fines)
        prefs(context).edit()
            .putString(KEY_FINE_LIST, json)
            .putLong(KEY_LAST_FETCH_TIME, System.currentTimeMillis())
            .apply()
    }

    fun getCachedFines(context: Context): List<IForceItem> {
        val json = prefs(context).getString(KEY_FINE_LIST, null) ?: return emptyList()

        return try {
            val type = object : TypeToken<List<IForceItem>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun isCacheValid(context: Context): Boolean {
        val lastFetch = prefs(context).getLong(KEY_LAST_FETCH_TIME, 0L)
        val now = System.currentTimeMillis()
        return lastFetch != 0L && (now - lastFetch) <= CACHE_TTL_MS
    }

    fun clearCache(context: Context) {
        prefs(context).edit()
            .remove(KEY_FINE_LIST)
            .remove(KEY_LAST_FETCH_TIME)
            .apply()
    }
}