package com.example.paymyfine.data.session

import com.russhwolf.settings.Settings

class SessionStore(
    private val settings: Settings
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
        if (!token.isNullOrBlank()) settings.putString(KEY_TOKEN, token)
        if (!fullName.isNullOrBlank()) settings.putString(KEY_FULLNAME, fullName)
        if (!email.isNullOrBlank()) settings.putString(KEY_EMAIL, email)
        if (!idNumber.isNullOrBlank()) settings.putString(KEY_IDNUMBER, idNumber)

        // Default to INDIVIDUAL after login
        settings.putString(KEY_PROFILE_MODE, "INDIVIDUAL")
    }

    fun getToken(): String? =
        settings.getStringOrNull(KEY_TOKEN)

    fun getIdNumber(): String? =
        settings.getStringOrNull(KEY_IDNUMBER)

    fun clear() {
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_FULLNAME)
        settings.remove(KEY_EMAIL)
        settings.remove(KEY_IDNUMBER)
        settings.remove(KEY_PROFILE_MODE)
    }
}
