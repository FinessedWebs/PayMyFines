package com.example.paymyfine.data.notification

sealed class NotificationsState {
    object Loading : NotificationsState()
    data class Data(
        val items: List<NotificationDto>,
        val isInbox: Boolean
    ) : NotificationsState()
    data class Error(val message: String) : NotificationsState()
}
