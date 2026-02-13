package com.example.paymyfine.data.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationsViewModel(
    private val repo: NotificationsRepository
) {

    private val _state =
        MutableStateFlow<NotificationsState>(NotificationsState.Loading)
    val state: StateFlow<NotificationsState> = _state

    var isInbox = true

    suspend fun load() {
        _state.value = NotificationsState.Loading

        try {
            val data = if (isInbox)
                repo.loadInbox()
            else
                repo.loadHistory()

            _state.value = NotificationsState.Data(data, isInbox)
        } catch (e: Exception) {
            _state.value =
                NotificationsState.Error("Failed to load notifications")
        }
    }

    suspend fun switchTab(inbox: Boolean) {
        isInbox = inbox
        load()
    }

    suspend fun markRead(id: String) {
        repo.markRead(id)
        load()
    }
}
