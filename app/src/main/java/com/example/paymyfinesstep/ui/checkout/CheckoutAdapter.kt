package com.example.paymyfinesstep.ui.checkout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.IForceItem
import com.example.paymyfinesstep.cart.CartItem

class CheckoutAdapter(
    private var items: List<CartItem>
) : RecyclerView.Adapter<CheckoutAdapter.CheckoutVH>() {

    inner class CheckoutVH(view: View) : RecyclerView.ViewHolder(view) {
        val textDesc: TextView = view.findViewById(R.id.textCheckoutFineDesc)
        val textAmount: TextView = view.findViewById(R.id.textCheckoutFineAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_checkout_fine, parent, false)
        return CheckoutVH(v)
    }

    override fun onBindViewHolder(holder: CheckoutVH, position: Int) {
        val fine = items[position]

        holder.textDesc.text = fine.noticeNumber ?: "Fine"
        val r = (fine.amountInCents ?: 0) / 100.0
        holder.textAmount.text = "R%.2f".format(r)
    }

    override fun getItemCount() = items.size
}