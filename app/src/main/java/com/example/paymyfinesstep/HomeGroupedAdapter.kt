package com.example.paymyfinesstep

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.api.IForceItem
import com.example.paymyfinesstep.ui.home.FineSelectionManager
import com.example.paymyfinesstep.ui.home.HomeListItem
import com.example.paymyfinesstep.ui.home.UserProfile
import com.google.android.material.card.MaterialCardView

class HomeGroupedAdapter(
    private var items: MutableList<HomeListItem>,
    private val listener: Listener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface Listener {
        fun onUserHeaderClicked(user: UserProfile)
        fun onUserSelectAllToggled(user: UserProfile, selectAll: Boolean)
        fun onFineClicked(fine: IForceItem)
        fun onSelectionChanged()
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_SUMMARY = 1
        private const val TYPE_FINE = 2
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is HomeListItem.UserHeader -> TYPE_HEADER
        is HomeListItem.UserSummary -> TYPE_SUMMARY
        is HomeListItem.FineRow -> TYPE_FINE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> {
                val v = inflater.inflate(R.layout.item_user_header, parent, false)
                HeaderVH(v)
            }
            TYPE_SUMMARY -> {
                val v = inflater.inflate(R.layout.item_user_summary, parent, false)
                SummaryVH(v)
            }
            TYPE_FINE -> {
                val v = inflater.inflate(R.layout.item_fine, parent, false)
                FineVH(v)
            }
            else -> throw IllegalArgumentException("Unknown viewType $viewType")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HomeListItem.UserHeader -> (holder as HeaderVH).bind(item)
            is HomeListItem.UserSummary -> (holder as SummaryVH).bind(item)
            is HomeListItem.FineRow -> (holder as FineVH).bind(item)
        }
    }

    fun updateItems(newItems: List<HomeListItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged() // simple for now; can be improved with DiffUtil
    }

    inner class HeaderVH(view: View) : RecyclerView.ViewHolder(view) {
        private val name: TextView = view.findViewById(R.id.textUserName)
        private val id: TextView = view.findViewById(R.id.textUserId)
        private val expand: ImageView = view.findViewById(R.id.imageExpand)

        fun bind(item: HomeListItem.UserHeader) {
            name.text = item.user.displayName
            val idNumber = item.user.idNumber ?: "—"
            id.text = "ID: $idNumber"

            expand.rotation = if (item.expanded) 180f else 0f

            itemView.setOnClickListener {
                listener.onUserHeaderClicked(item.user)
            }
        }
    }

    inner class SummaryVH(view: View) : RecyclerView.ViewHolder(view) {
        private val textFineCount: TextView = view.findViewById(R.id.textFineCount)
        private val checkSelectAll: CheckBox = view.findViewById(R.id.checkSelectAll)

        fun bind(item: HomeListItem.UserSummary) {
            val countText = "${item.unpaidCount} unpaid fines found"
            textFineCount.text = countText
            checkSelectAll.isChecked = item.allSelected

            checkSelectAll.setOnCheckedChangeListener(null)
            checkSelectAll.isChecked = item.allSelected
            checkSelectAll.setOnCheckedChangeListener { _, isChecked ->
                listener.onUserSelectAllToggled(item.user, isChecked)
                listener.onSelectionChanged()
            }
        }
    }

    inner class FineVH(view: View) : RecyclerView.ViewHolder(view) {
        private val icon: ImageView = view.findViewById(R.id.iconInfringement)
        private val description: TextView = view.findViewById(R.id.textDescription)
        private val location: TextView = view.findViewById(R.id.textLocation)
        private val amountNotice: TextView = view.findViewById(R.id.textAmountNotice)
        private val date: TextView = view.findViewById(R.id.textDate)
        private val card: MaterialCardView = view as MaterialCardView

        fun bind(item: HomeListItem.FineRow) {
            val fine = item.fine

            val fullDesc = fine.chargeDescriptions?.firstOrNull() ?: "No description"
            description.text = getShortDescription(fullDesc)

            location.text = fine.offenceLocation ?: "Unknown location"

            val rands = (fine.amountDueInCents ?: 0) / 100.0
            val notice = fine.noticeNumber ?: "---"
            amountNotice.text = "R%.2f • Notice: %s".format(rands, notice)

            date.text = fine.offenceDate ?: "--"

            val desc = fine.chargeDescriptions?.joinToString(" ")?.lowercase() ?: ""
            when {
                "speed" in desc -> icon.setImageResource(R.drawable.ic_speed)
                "parking" in desc || "park" in desc -> icon.setImageResource(R.drawable.ic_parking)
                "camera" in desc -> icon.setImageResource(R.drawable.ic_redlight)
                else -> icon.setImageResource(R.drawable.ic_report)
            }

            val isSelected = FineSelectionManager.isSelected(fine.noticeNumber)
            card.strokeWidth = if (isSelected) 4 else 0
            card.strokeColor = if (isSelected)
                itemView.context.getColor(R.color.accentColor)
            else
                itemView.context.getColor(android.R.color.transparent)

            // TAP = open details
            itemView.setOnClickListener {
                listener.onFineClicked(fine)
            }

            // LONG PRESS = select/deselect
            itemView.setOnLongClickListener {
                FineSelectionManager.toggleSelected(fine.noticeNumber)
                notifyItemChanged(bindingAdapterPosition)
                listener.onSelectionChanged()
                true
            }
        }

        private fun getShortDescription(full: String): String {
            val lower = full.lowercase()
            return when {
                "speed" in lower -> "Speeding"
                "park" in lower -> "Parking"
                "red" in lower && "light" in lower -> "Red Light"
                "towed" in lower || "tow" in lower -> "Towing Violation"
                "hazard" in lower || "waste" in lower -> "Hazard / Waste"
                else -> if (full.length > 30) full.take(30) + "..." else full
            }
        }
    }

    fun testInsert() {
        items.clear()
        items.add(
            HomeListItem.FineRow(
                userKey = "TEST",
                fine = IForceItem(
                    requestId = null,
                    dataSource = null,
                    issuingAuthority = "TEST Authority",
                    noticeNumber = "12345",
                    offenceDate = "2025-01-01",
                    offenceLocation = "Testville",
                    offenceDemeritPoints = null,
                    vehicleLicenseNumber = null,
                    infringerIdNumber = null,
                    infringerDemeritPointBalance = null,
                    chargeDescriptions = listOf("Speeding test"),
                    amountDueInCents = 12300,
                    paymentAllowed = true,
                    paymentNotAllowedReason = null,
                    status = "unpaid",
                    images = null,
                    caseNumber = null,
                    courtDate = null,
                    contemptAmountPaid = null,
                    summonsNumber = null
                )
            )
        )
        notifyDataSetChanged()
    }


}