package com.example.paymyfinesstep

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.api.IForceItem

class FinesAdapter(
    private var fines: List<IForceItem>,
    private val onClick: (IForceItem) -> Unit
) : RecyclerView.Adapter<FinesAdapter.FineVH>() {


    inner class FineVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val icon: ImageView = itemView.findViewById(R.id.iconInfringement)
        val textDescription: TextView = itemView.findViewById(R.id.textDescription)
        val textLocation: TextView = itemView.findViewById(R.id.textLocation)
        val textAmountNotice: TextView = itemView.findViewById(R.id.textAmountNotice)
        val textDate: TextView = itemView.findViewById(R.id.textDate)
        val textTicketNumber: TextView = itemView.findViewById(R.id.textTicketNumber)
        val textRegNumber: TextView = itemView.findViewById(R.id.textRegNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FineVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_fine, parent, false)
        return FineVH(view)
    }

    override fun getItemCount(): Int = fines.size

    override fun onBindViewHolder(holder: FineVH, position: Int) {
        val fine = fines[position]

        holder.textDescription.text = fine.chargeDescriptions?.firstOrNull() ?: "No description"
        holder.textLocation.text = fine.offenceLocation ?: "Unknown"
        holder.textDate.text = fine.offenceDate ?: "--"
        holder.textRegNumber.text = fine.vehicleLicenseNumber ?: "---"



        val rands = (fine.amountDueInCents ?: 0) / 100.0
        val notice = fine.noticeNumber ?: "---"
        holder.textAmountNotice.text = "R%.2f".format(rands)
        holder.textTicketNumber.text =  " Notice %s".format( notice)

        val descLower = holder.textDescription.text.toString().lowercase()

        when {
            "speed" in descLower -> holder.icon.setImageResource(R.drawable.ic_speed)
            "park" in descLower -> holder.icon.setImageResource(R.drawable.ic_parking)
            "red" in descLower && "light" in descLower -> holder.icon.setImageResource(R.drawable.ic_redlight)
            else -> holder.icon.setImageResource(R.drawable.ic_report)
        }

        holder.itemView.setOnClickListener {
            onClick(fine)}
    }

    fun update(newFines: List<IForceItem>) {
        fines = newFines
        notifyDataSetChanged()
    }
}
