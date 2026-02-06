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

    fun logout() = clear()

    fun clear() {
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_FULLNAME)
        settings.remove(KEY_EMAIL)
        settings.remove(KEY_IDNUMBER)
        settings.remove(KEY_PROFILE_MODE)
    }

    fun requireToken(): String =
        getToken() ?: error("User not logged in")

    fun getFullName(): String? =
        settings.getStringOrNull("fullName")

    fun getEmail(): String? =
        settings.getStringOrNull("email")

    fun saveFullName(name: String) {
        settings.putString("fullName", name)
    }

    fun saveEmail(email: String) {
        settings.putString("email", email)
    }



}

