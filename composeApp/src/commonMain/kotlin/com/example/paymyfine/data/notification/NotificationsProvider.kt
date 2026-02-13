package com.example.paymyfine.data.notification

import com.example.paymyfine.data.network.BaseUrlProvider
import com.example.paymyfine.data.network.HttpClientFactory
import com.example.paymyfine.data.session.SessionStore

object NotificationsProvider {

    fun createVM(
        sessionStore: SessionStore
    ): NotificationsViewModel {

        val client =
            HttpClientFactory.create(sessionStore)

        val api = NotificationsApi(
            client,
            BaseUrlProvider.get()
        )

        val repo = NotificationsRepository(api)

        return NotificationsViewModel(repo)
    }
}

object NotificationsBadgeProvider {

    fun create(sessionStore: SessionStore): NotificationsBadgeManager {
        val client = HttpClientFactory.create(sessionStore)
        val api = NotificationsApi(client, BaseUrlProvider.get())
        val repo = NotificationsRepository(api)

        return NotificationsBadgeManager(repo)
    }
}
