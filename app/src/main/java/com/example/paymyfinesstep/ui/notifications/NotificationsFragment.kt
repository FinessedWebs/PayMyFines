package com.example.paymyfinesstep.ui.notifications

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.MainActivity
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.ApiBackend
import com.example.paymyfinesstep.api.NotificationDto
import com.example.paymyfinesstep.api.NotificationsApi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private lateinit var tabNotifications: TextView
    private lateinit var tabHistory: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: NotificationsAdapter
    private lateinit var emptyText: TextView

    private val notificationsApi by lazy {
        ApiBackend.create(requireContext(), NotificationsApi::class.java)
    }

    private var showingNotifications = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabNotifications = view.findViewById(R.id.tabNotifications)
        tabHistory = view.findViewById(R.id.tabHistory)
        recycler = view.findViewById(R.id.recyclerNotifications)
        emptyText = view.findViewById(R.id.textEmptyNotifications) // ✅ add this in XML

        adapter = NotificationsAdapter(emptyList()) { clicked ->
            onItemClicked(clicked)
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        tabNotifications.setOnClickListener {
            showingNotifications = true
            activateTab(true)
            loadInboxNotifications()
        }

        tabHistory.setOnClickListener {
            showingNotifications = false
            activateTab(false)
            loadHistoryFromDb()
        }

        activateTab(true)
        loadInboxNotifications()
    }

    // ✅ INBOX = NEW_FINE + PAYMENT_REMINDER
    private fun loadInboxNotifications() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = notificationsApi.getNotifications(unreadOnly = false, take = 80)

                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show()
                        adapter.update(emptyList())
                        showEmptyState("No notifications")
                    }
                    return@launch
                }

                val data = response.body().orEmpty()

                val items = data
                    .filter { it.type == "NEW_FINE" || it.type == "PAYMENT_REMINDER" }
                    .sortedBy { it.isRead } // ✅ unread first
                    .map { it.toUiItem() }

                withContext(Dispatchers.Main) {
                    adapter.update(items)
                    updateEmptyStateForInbox(items.size)

                    // ✅ update badge after loading
                    (activity as? MainActivity)?.refreshNotificationsBadge()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    adapter.update(emptyList())
                    showEmptyState("No notifications")
                }
            }
        }
    }

    // ✅ HISTORY = FINE_PAID (from DB)  [Option A]
    private fun loadHistoryFromDb() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = notificationsApi.getNotifications(unreadOnly = false, take = 120)

                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to load history", Toast.LENGTH_SHORT).show()
                        adapter.update(emptyList())
                        showEmptyState("No history yet")
                    }
                    return@launch
                }

                val data = response.body().orEmpty()

                val items = data
                    .filter { it.type == "FINE_PAID" }
                    .map { it.toUiItem() }

                withContext(Dispatchers.Main) {
                    adapter.update(items)
                    updateEmptyStateForHistory(items.size)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to load history: ${e.message}", Toast.LENGTH_LONG).show()
                    adapter.update(emptyList())
                    showEmptyState("No history yet")
                }
            }
        }
    }

    private fun onItemClicked(item: NotificationItem) {

        // ✅ Show popup/dialog for BOTH inbox & history
        showNotificationDialog(item)

        // ✅ Mark as read only if this is a real notification row (has id)
        val id = item.id ?: return

        if (item.isRead) return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val resp = notificationsApi.markRead(id)
                if (resp.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        // reload correct list
                        if (showingNotifications) loadInboxNotifications()
                        else loadHistory()

                        (activity as? MainActivity)?.refreshNotificationsBadge()
                    }
                }
            } catch (_: Exception) {}
        }
    }



    private fun activateTab(notifications: Boolean) {
        if (notifications) {
            tabNotifications.setBackgroundResource(R.drawable.bg_toggle_active)
            tabNotifications.setTextColor(Color.WHITE)

            tabHistory.setBackgroundResource(R.drawable.bg_toggle_inactive)
            tabHistory.setTextColor(Color.DKGRAY)
        } else {
            tabHistory.setBackgroundResource(R.drawable.bg_toggle_active)
            tabHistory.setTextColor(Color.WHITE)

            tabNotifications.setBackgroundResource(R.drawable.bg_toggle_inactive)
            tabNotifications.setTextColor(Color.DKGRAY)
        }
    }

    private fun NotificationDto.toUiItem(): NotificationItem {
        return NotificationItem(
            id = id,
            title = title,
            date = formatCreatedAt(createdAt),
            message = message,
            meta = noticeNumber ?: type,
            isRead = isRead,
            type = type
        )
    }

    private fun formatCreatedAt(createdAt: String): String {
        return try {
            createdAt.substring(0, 19).replace("T", " ")
        } catch (_: Exception) {
            createdAt
        }
    }

    private fun updateEmptyStateForInbox(count: Int) {
        if (count <= 0) showEmptyState("No new notifications")
        else hideEmptyState()
    }

    private fun updateEmptyStateForHistory(count: Int) {
        if (count <= 0) showEmptyState("No payment history yet")
        else hideEmptyState()
    }

    private fun showEmptyState(msg: String) {
        emptyText.text = msg
        emptyText.visibility = View.VISIBLE
        recycler.visibility = View.GONE
    }

    private fun hideEmptyState() {
        emptyText.visibility = View.GONE
        recycler.visibility = View.VISIBLE
    }

    private fun showNotificationDialog(item: NotificationItem) {

        val title = item.title
        val msg = """
        ${item.message}

        Fine: ${item.meta}
        Date: ${item.date}
    """.trimIndent()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(msg)
            .setPositiveButton("Mark as read") { _, _ ->
                markAsRead(item)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun markAsRead(item: NotificationItem) {
        val id = item.id ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val resp = notificationsApi.markRead(id)

                if (resp.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        // ✅ reload whichever tab you're on
                        if (showingNotifications) loadNotifications()
                        else loadHistory()

                        // ✅ update bottom badge
                        (activity as? MainActivity)?.refreshNotificationsBadge()
                    }
                }

            } catch (_: Exception) {
            }
        }
    }

    private fun loadHistory() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = notificationsApi.getNotifications(unreadOnly = false, take = 100)

                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        adapter.update(emptyList())
                    }
                    return@launch
                }

                val data = response.body().orEmpty()

                val items = data
                    .filter { it.type == "FINE_PAID" }
                    .map { dto ->
                        NotificationItem(
                            id = dto.id,
                            title = dto.title,
                            date = formatCreatedAt(dto.createdAt),
                            message = dto.message,
                            meta = dto.noticeNumber ?: "Fine Paid",
                            isRead = dto.isRead,
                            type = dto.type
                        )
                    }

                withContext(Dispatchers.Main) {
                    adapter.update(items)
                    (activity as? MainActivity)?.refreshNotificationsBadge()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                    adapter.update(emptyList())
                }
            }
        }
    }

    private fun loadNotifications() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = notificationsApi.getNotifications(unreadOnly = false, take = 100)

                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        adapter.update(emptyList())
                    }
                    return@launch
                }

                val data = response.body().orEmpty()

                val items = data
                    .filter { it.type == "NEW_FINE" || it.type == "PAYMENT_REMINDER" }
                    .map { dto ->
                        NotificationItem(
                            id = dto.id,
                            title = dto.title,
                            date = formatCreatedAt(dto.createdAt),
                            message = dto.message,
                            meta = dto.noticeNumber ?: dto.type,
                            isRead = dto.isRead,
                            type = dto.type
                        )
                    }

                withContext(Dispatchers.Main) {
                    adapter.update(items)
                    (activity as? MainActivity)?.refreshNotificationsBadge()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    adapter.update(emptyList())
                }
            }
        }
    }


}
