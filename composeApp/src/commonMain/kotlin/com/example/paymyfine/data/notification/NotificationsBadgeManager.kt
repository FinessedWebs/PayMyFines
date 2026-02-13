package com.example.paymyfine.data.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationsBadgeManager(
    private val repo: NotificationsRepository
) {

    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count

    suspend fun refresh() {
        _count.value = repo.unreadCount()
    }
}
