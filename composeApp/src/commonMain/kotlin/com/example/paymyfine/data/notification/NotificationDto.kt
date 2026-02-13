package com.example.paymyfine.data.notification

import kotlinx.serialization.Serializable

@Serializable
data class NotificationDto(
    val id: String,
    val type: String,
    val noticeNumber: String? = null,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: String // ISO from backend
)

@Serializable
data class UnreadCountResponse(
    val unread: Int
)
