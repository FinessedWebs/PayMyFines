package com.example.paymyfine.data.cart

import com.example.paymyfine.data.session.SessionStore

object CartProvider {

    private var manager: CartManager? = null
    private var currentUser: String? = null

    fun get(sessionStore: SessionStore): CartManager {

        val userId =
            sessionStore.getIdNumber() ?: "guest"

        if (manager == null || currentUser != userId) {

            // ✅ USE SAME SETTINGS AS SESSION
            manager = CartManager(
                sessionStore.settings,
                userId
            )

            currentUser = userId
        }

        return manager!!
    }

    fun clear() {
        manager = null
        currentUser = null
    }
}
