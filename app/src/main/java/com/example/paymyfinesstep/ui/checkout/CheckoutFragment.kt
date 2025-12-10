package com.example.paymyfinesstep.ui.checkout

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.cart.CartManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CheckoutAdapter
    private lateinit var textTotal: TextView
    private lateinit var btnCancel: MaterialButton
    private lateinit var btnConfirm: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.recyclerCheckout)
        textTotal = view.findViewById(R.id.textCheckoutTotal)
        btnCancel = view.findViewById(R.id.btnCheckoutCancel)
        btnConfirm = view.findViewById(R.id.btnProceedToPay)

        // ⭐ Load cart inside coroutine
        lifecycleScope.launch {
            val items = CartManager.getCart(requireContext())

            adapter = CheckoutAdapter(items)
            recycler.layoutManager = LinearLayoutManager(requireContext())
            recycler.adapter = adapter

            val totalCents = items.sumOf { it.amountInCents }
            textTotal.text = "Total: R%.2f".format(totalCents / 100.0)
        }

        btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        btnConfirm.setOnClickListener {
            findNavController().navigate(R.id.paymentFragment)
        }
    }

}
