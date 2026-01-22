package com.example.paymyfinesstep.ui.family

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.FinesAdapter
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.FamilyMember
import com.example.paymyfinesstep.api.IForceItem

class FamilyAdapter(
    private var items: MutableList<FamilyMember>,
    private var allFines: List<IForceItem>,
    private val onFineClick: (IForceItem) -> Unit,
    private val onMemberClick: (FamilyMember) -> Unit,
    private val onDelete: (FamilyMember) -> Unit,
    private val onEdit: (FamilyMember) -> Unit
) : RecyclerView.Adapter<FamilyAdapter.FamilyVH>() {

    private var expandedLinkId: String? = null

    inner class FamilyVH(view: View) : RecyclerView.ViewHolder(view) {
        val userHeader: View = view.findViewById(R.id.userHeader)
        val name: TextView = view.findViewById(R.id.textFamilyName)
        val idNumber: TextView = view.findViewById(R.id.textFamilyIdNumber)
        val badge: TextView = view.findViewById(R.id.textHasAccountBadge)
        val arrow: ImageView = view.findViewById(R.id.iconExpandArrow)

        val expandContainer: View = view.findViewById(R.id.expandContainer)
        val noFinesText: TextView = view.findViewById(R.id.textNoFines)
        val recyclerFines: RecyclerView = view.findViewById(R.id.recyclerUserFines)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_family_user, parent, false)
        return FamilyVH(view)
    }

    override fun onBindViewHolder(holder: FamilyVH, position: Int) {
        val member = items[position]

        val displayName = if (!member.nickname.isNullOrBlank()) {
            member.nickname
        } else {
            "${member.fullName} ${member.surname}"
        }

        holder.name.text = displayName
        holder.idNumber.text = "ID: ${member.idNumber} • ${member.relationship}"
        holder.badge.visibility = if (member.hasAccount) View.VISIBLE else View.GONE

        val isExpanded = expandedLinkId == member.linkId
        setExpansionState(holder, isExpanded)

        val fines = allFines.filter { fine ->
            fine.userIdNumber == member.idNumber
        }

        if (isExpanded) {
            if (fines.isEmpty()) {
                holder.noFinesText.visibility = View.VISIBLE
                holder.recyclerFines.visibility = View.GONE
            } else {
                holder.noFinesText.visibility = View.GONE
                holder.recyclerFines.visibility = View.VISIBLE

                holder.recyclerFines.layoutManager = LinearLayoutManager(holder.itemView.context)
                holder.recyclerFines.adapter = FinesAdapter(fines, onFineClick)
            }
        }

        holder.userHeader.setOnClickListener {
            expandedLinkId = if (isExpanded) null else member.linkId
            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener {
            onMemberClick(member)
        }
    }

    override fun getItemCount(): Int = items.size

    fun getItem(position: Int): FamilyMember = items[position]

    // ✅ FIX: this is required because your FamilyFragment calls adapter.getItems()
    fun getItems(): List<FamilyMember> = items

    fun update(newMembers: MutableList<FamilyMember>, newFines: List<IForceItem> = emptyList()) {
        items = newMembers
        allFines = newFines
        notifyDataSetChanged()
    }

    fun removeMember(member: FamilyMember) {
        val index = items.indexOfFirst { it.linkId == member.linkId }
        if (index >= 0) {
            items.removeAt(index)
            notifyItemRemoved(index)
        } else {
            notifyDataSetChanged()
        }
    }

    private fun setExpansionState(holder: FamilyVH, expand: Boolean) {
        if (expand) {
            holder.expandContainer.visibility = View.VISIBLE
            holder.expandContainer.alpha = 0f
            holder.expandContainer.animate().alpha(1f).setDuration(200).start()
            holder.arrow.animate().rotation(180f).setDuration(200).start()
        } else {
            holder.expandContainer.animate().alpha(0f)
                .setDuration(150)
                .withEndAction { holder.expandContainer.visibility = View.GONE }
                .start()
            holder.arrow.animate().rotation(0f).setDuration(200).start()
        }
    }
}
