package com.example.paymyfinesstep.payment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.PaymentHistoryItem

class PaymentHistoryFragment : Fragment(R.layout.fragment_payment_history) {/*

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerPaymentHistory)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        // Fake for now — will hook API later
        val sample = listOf(
            PaymentHistoryItem("92/00131/652/001006", "2025-12-03", 30000, true),
            PaymentHistoryItem("92/02088/730/004998", "2025-12-01", 100000, false)
        )

        recycler.adapter = PaymentHistoryAdapter(sample)
    }*/
}
