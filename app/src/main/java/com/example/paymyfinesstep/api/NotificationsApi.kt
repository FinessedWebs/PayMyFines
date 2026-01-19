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

    @POST("api/Notifications/mark-read/{id}")
    suspend fun markRead(@Path("id") id: String): Response<MarkReadResponse>
}

data class UnreadCountResponse(val unread: Int)
data class MarkReadResponse(val success: Boolean)

data class NotificationDto(
    val id: String,
    val type: String,
    val noticeNumber: String?,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: String
)
