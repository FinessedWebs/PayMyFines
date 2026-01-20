package com.example.paymyfinesstep.ui.notifications

data class NotificationItem(
    val id: String? = null,
    val title: String,
    val date: String,
    val message: String,
    val meta: String,
    val isRead: Boolean = true,      // ✅ add this
    val type: String = ""            // ✅ useful for UI icons/filtering
)


