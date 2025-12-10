package com.example.paymyfinesstep.cart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.IForceItem

class CartAdapter(
    private var items: MutableList<CartItem>,
    private val onRemove: (String) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartVH>() {

    inner class CartVH(view: View) : RecyclerView.ViewHolder(view) {
        val textDesc: TextView = view.findViewById(R.id.textCartDescription)
        val textAmount: TextView = view.findViewById(R.id.textCartAmount)
        val btnRemove: ImageButton = view.findViewById(R.id.btnRemoveFromCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_fine, parent, false)
        return CartVH(v)
    }

    override fun onBindViewHolder(holder: CartVH, position: Int) {
        val item = items[position]

        holder.textDesc.text = item.description
        holder.textAmount.text = "R%.2f".format(item.amountInCents / 100.0)

        holder.btnRemove.setOnClickListener {
            println("REMOVE CLICKED → '${item.noticeNumber}'")
            onRemove(item.noticeNumber)
        }
    }

    override fun getItemCount() = items.size

    fun update(newItems: MutableList<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
