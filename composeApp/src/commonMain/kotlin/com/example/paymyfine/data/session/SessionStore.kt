package com.example.paymyfine.data.session

import com.example.paymyfine.data.cart.CartProvider
import com.russhwolf.settings.Settings
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class SessionStore(
    val settings: Settings
) {

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_FULLNAME = "fullName"
        private const val KEY_EMAIL = "email"
        private const val KEY_IDNUMBER = "idNumber"
        private const val KEY_PROFILE_MODE = "profile_mode"
    }

    fun saveSession(
        token: String?,
        fullName: String?,
        email: String?,
        idNumber: String?
    ) {
        token?.let { settings.putString(KEY_TOKEN, it) }
        fullName?.let { settings.putString(KEY_FULLNAME, it) }
        email?.let { settings.putString(KEY_EMAIL, it) }
        idNumber?.let { settings.putString(KEY_IDNUMBER, it) }

        settings.putString(KEY_PROFILE_MODE, "INDIVIDUAL")
    }

    fun getToken(): String? =
        settings.getStringOrNull(KEY_TOKEN)

    fun isLoggedIn(): Boolean =
        !getToken().isNullOrBlank()

    fun getIdNumber(): String? =
        settings.getStringOrNull(KEY_IDNUMBER)

    fun clear() {
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_FULLNAME)
        settings.remove(KEY_EMAIL)
        settings.remove(KEY_IDNUMBER)
        settings.remove(KEY_PROFILE_MODE)
        CartProvider.clear()
    }

    fun getFullName(): String? =
        settings.getStringOrNull(KEY_FULLNAME)

    fun getEmail(): String? =
        settings.getStringOrNull(KEY_EMAIL)

    fun saveFullName(name: String) {
        settings.putString(KEY_FULLNAME, name)
    }

    fun saveEmail(email: String) {
        settings.putString(KEY_EMAIL, email)
    }

    // ✅ PROFILE IMAGE LOCAL STORAGE
    @OptIn(ExperimentalEncodingApi::class)
    fun saveProfileImage(userId: String, bytes: ByteArray) {
        val base64 = Base64.encode(bytes)
        settings.putString("profile_image_$userId", base64)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun getProfileImage(userId: String): ByteArray? {
        val base64 = settings.getStringOrNull("profile_image_$userId")
            ?: return null
        return Base64.decode(base64)
    }

    fun clearProfileImage(userId: String) {
        settings.remove("profile_image_$userId")
    }

    fun requireToken(): String {
        return getToken() ?: error("User not logged in")
    }
}