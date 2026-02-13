package com.example.paymyfine.data.notification

class NotificationsRepository(
    private val api: NotificationsApi
) {

    suspend fun loadInbox(): List<NotificationDto> {
        api.sync()

        return api.getNotifications(unreadOnly = false)
            .filter {
                it.type == NotificationTypes.NEW_FINE ||
                        it.type == NotificationTypes.PAYMENT_REMINDER
            }
            .sortedBy { it.isRead } // unread first
    }

    suspend fun loadHistory(): List<NotificationDto> =
        api.getNotifications(unreadOnly = false)
            .filter { it.type == NotificationTypes.FINE_PAID }
            .sortedByDescending { it.createdAt }

    suspend fun markRead(id: String) =
        api.markRead(id)

    suspend fun unreadCount(): Int =
        api.getUnreadCount()
}
