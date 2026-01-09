package com.example.paymyfinesstep.ui.notifications

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.R

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private lateinit var tabNotifications: TextView
    private lateinit var tabHistory: TextView
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: NotificationsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabNotifications = view.findViewById(R.id.tabNotifications)
        tabHistory = view.findViewById(R.id.tabHistory)
        recycler = view.findViewById(R.id.recyclerNotifications)

        adapter = NotificationsAdapter(emptyList())
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        showNotifications()

        tabNotifications.setOnClickListener { showNotifications() }
        tabHistory.setOnClickListener { showHistory() }
    }

    private fun showNotifications() {
        activateTab(true)

        adapter.update(
            listOf(
                NotificationItem("New Fine Issued", "22 Nov 2025", "R500 • Unpaid"),
                NotificationItem("Payment Reminder", "21 Nov 2025", "Due in 3 days")
            )
        )
    }

    private fun showHistory() {
        activateTab(false)

        adapter.update(
            listOf(
                NotificationItem("Fine Paid", "18 Nov 2025", "R300 • Paid"),
                NotificationItem("Fine Paid", "10 Nov 2025", "R800 • Paid")
            )
        )
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
}
