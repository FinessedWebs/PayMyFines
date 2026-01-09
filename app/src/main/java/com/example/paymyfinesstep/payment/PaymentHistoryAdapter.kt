package com.example.paymyfinesstep.payment

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.PaymentHistoryItem

class PaymentHistoryAdapter/*(
    private var items: List<PaymentHistoryItem>
) : RecyclerView.Adapter<PaymentHistoryAdapter.VH>() */{/*

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val notice: TextView = view.findViewById(R.id.textNotice)
        val amount: TextView = view.findViewById(R.id.textAmount)
        val status: TextView = view.findViewById(R.id.textStatus)
        val date: TextView? = view.findViewById(R.id.textDate) // optional if exists
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_history, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.notice.text = "Notice: ${item.noticeNumber}"
        holder.amount.text = "R%.2f".format(item.amountCents / 100.0)

        if (item.success) {
            holder.status.text = "Paid"
            holder.status.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.successGreen)
            )
        } else {
            holder.status.text = "Failed"
            holder.status.setTextColor(Color.RED)
        }

        holder.date?.text = item.date   // if your model supports it
    }

    override fun getItemCount(): Int = items.size

    *//** 🔁 Allows switching between notification & history data *//*
    fun updateData(newItems: List<PaymentHistoryItem>) {
        items = newItems
        notifyDataSetChanged()
    }*/
}
