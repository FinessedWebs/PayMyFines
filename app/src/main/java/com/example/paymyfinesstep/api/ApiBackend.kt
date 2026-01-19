package com.example.paymyfinesstep.api

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiBackend {

    private const val BASE_URL_EMULATOR = "http://10.0.2.2:5089/"           // Emulator
    private const val BASE_URL_PHONE = "http://10.249.129.33:5089/"         // Your PC work WiFi IP
    /*private const val BASE_URL_PHONE = "http://192.168.3.109:5089/"*/     // Your PC home WiFi IP

    fun baseUrl(): String {
        return if (android.os.Build.FINGERPRINT.contains("generic")) {
            BASE_URL_EMULATOR
        } else {
            BASE_URL_PHONE
        }
    }

    fun <T> create(context: Context, service: Class<T>): T {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(logging)
            .retryOnConnectionFailure(true)
            .connectTimeout(40, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        val retrofitInstance = Retrofit.Builder()
            .baseUrl(baseUrl())
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofitInstance.create(service)
    }
}
