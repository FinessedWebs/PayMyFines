package com.example.paymyfinesstep.ui.notifications

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.R

class NotificationsAdapter(
    private var items: List<NotificationItem>,
    private val onClick: (NotificationItem) -> Unit
) : RecyclerView.Adapter<NotificationsAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textTitle)
        val date: TextView = view.findViewById(R.id.textDate)
        val message: TextView = view.findViewById(R.id.textMessage)
        val meta: TextView = view.findViewById(R.id.textMeta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.date.text = item.date
        holder.message.text = item.message
        holder.meta.text = item.meta

        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<NotificationItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
