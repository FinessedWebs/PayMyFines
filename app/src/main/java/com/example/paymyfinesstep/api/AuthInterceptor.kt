package com.example.paymyfinesstep.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        // Read FROM THE SAME PREF FILE used at login
        val prefs = context.getSharedPreferences("paymyfines_prefs", Context.MODE_PRIVATE)

        // Read token using the SAME KEY used at login
        val token = prefs.getString("jwt_token", null)

        val requestBuilder = chain.request().newBuilder()

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
