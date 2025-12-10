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

class PaymentHistoryAdapter(
    private val items: List<PaymentHistoryItem>
) : RecyclerView.Adapter<PaymentHistoryAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val notice: TextView = view.findViewById(R.id.textNotice)
        val amount: TextView = view.findViewById(R.id.textAmount)
        val status: TextView = view.findViewById(R.id.textStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_history, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]

        holder.notice.text = item.noticeNumber
        holder.amount.text = "R%.2f".format(item.amountCents / 100.0)
        holder.status.text = if (item.success) "Paid" else "Failed"

        holder.status.setTextColor(
            if (item.success)
                ContextCompat.getColor(holder.itemView.context, R.color.successGreen)
            else
                Color.RED
        )
    }

    override fun getItemCount() = items.size
}
