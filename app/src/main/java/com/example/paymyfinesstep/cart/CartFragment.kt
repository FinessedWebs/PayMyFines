package com.example.paymyfinesstep.cart

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.R
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: CartAdapter
    private lateinit var textTotal: TextView
    private lateinit var btnCheckout: MaterialButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.recyclerCart)
        textTotal = view.findViewById(R.id.textCartTotal)
        btnCheckout = view.findViewById(R.id.btnCheckout)

        adapter = CartAdapter(mutableListOf()) { notice ->
            lifecycleScope.launch {
                CartManager.remove(requireContext(), notice)
                refresh()
            }
        }


        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        btnCheckout.setOnClickListener {
            findNavController().navigate(R.id.checkoutFragment)
        }
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        lifecycleScope.launch {
            val items = CartManager.getCart(requireContext())
            adapter.update(items.toMutableList())

            val total = items.sumOf { it.amountInCents } / 100.0
            textTotal.text = "Total: R%.2f".format(total)
        }



    }

}
