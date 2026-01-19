package com.example.paymyfinesstep.ui.notifications

data class NotificationItem(
    val id: String? = null,       // for mark-read (only for notifications)
    val title: String,
    val date: String,
    val message: String,
    val meta: String
)

