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
import com.example.paymyfinesstep.api.InfringementsApi
import com.example.paymyfinesstep.api.NotificationsApi
import com.example.paymyfinesstep.api.NotificationDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private lateinit var tabNotifications: TextView
    private lateinit var tabHistory: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: NotificationsAdapter

    private val notificationsApi by lazy {
        ApiBackend.create(requireContext(), NotificationsApi::class.java)
    }

    private val infringementsApi by lazy {
        ApiBackend.create(requireContext(), InfringementsApi::class.java)
    }

    private var showingNotifications = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabNotifications = view.findViewById(R.id.tabNotifications)
        tabHistory = view.findViewById(R.id.tabHistory)
        recycler = view.findViewById(R.id.recyclerNotifications)

        adapter = NotificationsAdapter(emptyList()) { clicked ->
            onItemClicked(clicked)
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        tabNotifications.setOnClickListener {
            showingNotifications = true
            activateTab(true)
            loadNotifications()
        }

        tabHistory.setOnClickListener {
            showingNotifications = false
            activateTab(false)
            loadHistory()
        }

        activateTab(true)
        loadNotifications()
    }

    private fun loadNotifications() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = notificationsApi.getNotifications(unreadOnly = false, take = 50)

                if (!response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show()
                        adapter.update(emptyList())
                    }
                    return@launch
                }

                val data = response.body().orEmpty()

                val items = data
                    .filter { it.type == "NEW_FINE" || it.type == "PAYMENT_REMINDER" }
                    .map { it.toUiItem() }

                withContext(Dispatchers.Main) {
                    adapter.update(items)
                    (activity as? MainActivity)?.refreshNotificationsBadge()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    adapter.update(emptyList())
                }
            }
        }
    }

    private fun loadHistory() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // ✅ InfringementsApi returns InfringementResponse directly (not Response<>)
                val data = infringementsApi.getClosedInfringements()
                val closed = data.iForce.orEmpty()

                val items = closed.map { fine ->
                    NotificationItem(
                        id = null,
                        title = "Fine Paid",
                        date = formatOffenceDate(fine.offenceDate ?: ""),
                        message = fine.issuingAuthority ?: "Payment completed",
                        meta = fine.noticeNumber ?: "Paid"
                    )
                }

                withContext(Dispatchers.Main) {
                    adapter.update(items)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Failed to load history: ${e.message}", Toast.LENGTH_LONG).show()
                    adapter.update(emptyList())
                }
            }
        }
    }

    private fun onItemClicked(item: NotificationItem) {
        if (!showingNotifications) return

        val id = item.id ?: return

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val resp = notificationsApi.markRead(id)

                if (resp.isSuccessful) {
                    loadNotifications()

                    withContext(Dispatchers.Main) {
                        (activity as? MainActivity)?.refreshNotificationsBadge()
                    }
                }

            } catch (_: Exception) {
            }
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
            meta = noticeNumber ?: type
        )
    }

    private fun formatCreatedAt(createdAt: String): String {
        return try {
            createdAt.substring(0, 19).replace("T", " ")
        } catch (_: Exception) {
            createdAt
        }
    }

    private fun formatOffenceDate(offenceDate: String): String {
        return try {
            if (offenceDate.length != 8) return offenceDate
            val yyyy = offenceDate.substring(0, 4)
            val mm = offenceDate.substring(4, 6)
            val dd = offenceDate.substring(6, 8)
            "$dd-$mm-$yyyy"
        } catch (_: Exception) {
            offenceDate
        }
    }
}
