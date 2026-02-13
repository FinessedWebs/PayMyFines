package com.example.paymyfine.data.notification

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post

class NotificationsApi(
    private val client: HttpClient,
    private val baseUrl: String
) {

    suspend fun getNotifications(
        unreadOnly: Boolean = false,
        take: Int = 80
    ): List<NotificationDto> =
        client.get("$baseUrl/api/notifications") {
            parameter("unreadOnly", unreadOnly)
            parameter("take", take)
        }.body()

    suspend fun getUnreadCount(): Int =
        client.get("$baseUrl/api/notifications/unread-count")
            .body<UnreadCountResponse>()
            .unread

    suspend fun markRead(id: String) {
        client.post("$baseUrl/api/notifications/mark-read/$id")
    }

    suspend fun markAllRead() {
        client.post("$baseUrl/api/notifications/mark-all-read")
    }

    suspend fun sync() {
        client.post("$baseUrl/api/notifications/sync")
    }
}
