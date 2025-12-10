package com.example.paymyfinesstep.ui.notifications

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.R

class NotificationsFragment : Fragment(R.layout.fragment_notifications) {

    private lateinit var tabNotifications: TextView
    private lateinit var tabPayments: TextView
    private lateinit var recycler: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabNotifications = view.findViewById(R.id.tabNotifications)
        tabPayments = view.findViewById(R.id.tabPayments)
        recycler = view.findViewById(R.id.recyclerNotifications)

        loadNotifications()

        tabNotifications.setOnClickListener {
            activateNotificationsTab()
            loadNotifications()
        }

        tabPayments.setOnClickListener {
            activatePaymentsTab()
            findNavController().navigate(R.id.paymentHistoryFragment)
        }
    }

    private fun activateNotificationsTab() {
        tabNotifications.setBackgroundResource(R.drawable.tab_left_active)
        tabNotifications.setTextColor(Color.WHITE)

        tabPayments.setBackgroundResource(R.drawable.tab_right_inactive)
        tabPayments.setTextColor(Color.DKGRAY)
    }

    private fun activatePaymentsTab() {
        tabPayments.setBackgroundResource(R.drawable.tab_right_active)
        tabPayments.setTextColor(Color.WHITE)

        tabNotifications.setBackgroundResource(R.drawable.tab_left_inactive)
        tabNotifications.setTextColor(Color.DKGRAY)
    }

    private fun loadNotifications() {
        // TODO: your notifications logic goes here
    }
}
