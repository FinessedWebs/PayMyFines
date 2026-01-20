package com.example.paymyfinesstep.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationsApi {

    @GET("api/Notifications")
    suspend fun getNotifications(
        @Query("unreadOnly") unreadOnly: Boolean = false,
        @Query("take") take: Int = 50
    ): Response<List<NotificationDto>>

    @GET("api/Notifications/unread-count")
    suspend fun getUnreadCount(): Response<UnreadCountResponse>

    // ✅ NEW: inbox unread count (only NEW_FINE + PAYMENT_REMINDER)
    @GET("api/Notifications/unread-count/inbox")
    suspend fun getUnreadInboxCount(): Response<UnreadCountResponse>

    @POST("api/Notifications/mark-read/{id}")
    suspend fun markRead(@Path("id") id: String): Response<MarkReadResponse>

    // ✅ NEW: mark all read
    @POST("api/Notifications/mark-all-read")
    suspend fun markAllRead(): Response<MarkAllReadResponse>
}

data class UnreadCountResponse(val unread: Int)
data class MarkReadResponse(val success: Boolean)
data class MarkAllReadResponse(val marked: Int)

data class NotificationDto(
    val id: String,
    val type: String,
    val noticeNumber: String?,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: String
)
