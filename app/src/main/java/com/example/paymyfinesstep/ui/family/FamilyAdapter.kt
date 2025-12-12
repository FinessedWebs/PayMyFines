package com.example.paymyfinesstep.ui.family

import android.util.Log
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


    private var expandedUserId: String? = null

    inner class FamilyVH(view: View) : RecyclerView.ViewHolder(view) {
        val userHeader: View = view.findViewById(R.id.userHeader)
        val name: TextView = view.findViewById(R.id.textFamilyName)
        val idNumber: TextView = view.findViewById(R.id.textFamilyIdNumber)
        val badge: TextView = view.findViewById(R.id.textHasAccountBadge)
        val arrow: ImageView = view.findViewById(R.id.iconExpandArrow)

        val expandContainer: View = view.findViewById(R.id.expandContainer)
        val noFinesText: TextView = view.findViewById(R.id.textNoFines)
        val recyclerFines: RecyclerView = view.findViewById(R.id.recyclerUserFines)

        /*val deleteBtn: ImageView = view.findViewById(R.id.btnDeleteFamilyMember)
        val editBtn: ImageView = view.findViewById(R.id.iconEditMember)*/

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_family_user, parent, false)
        return FamilyVH(view)
    }

    override fun onBindViewHolder(holder: FamilyVH, position: Int) {
        val member = items[position]

        holder.name.text = "${member.fullName} ${member.surname}"
        holder.idNumber.text = "ID: ${member.idNumber}"
        holder.badge.visibility = if (member.hasAccount) View.VISIBLE else View.GONE

        val isExpanded = expandedUserId == member.id
        setExpansionState(holder, isExpanded)

        val fines = allFines.filter { it.userIdNumber == member.idNumber }

        if (isExpanded) {
            if (fines.isEmpty()) {
                holder.noFinesText.visibility = View.VISIBLE
                holder.recyclerFines.visibility = View.GONE
            } else {
                holder.noFinesText.visibility = View.GONE
                holder.recyclerFines.apply {
                    visibility = View.VISIBLE
                    layoutManager = LinearLayoutManager(context)
                    adapter = FinesAdapter(fines, onFineClick)
                }
            }
        }

        holder.userHeader.setOnClickListener {
            expandedUserId = if (isExpanded) null else member.id
            notifyDataSetChanged()
        }



        /*holder.deleteBtn.setOnClickListener { onDelete(member) }
        holder.editBtn.setOnClickListener { onEdit(member) }*/

        holder.itemView.setOnClickListener { onMemberClick(member) }

        /*holder.deleteBtn.setOnClickListener {
            Log.d("DELETE_TEST", "Delete button clicked for ${member.fullName}")
            onDelete(member)
        }*/

    }
    fun getItem(position: Int): FamilyMember = items[position]
    fun getItems(): List<FamilyMember> = items


    override fun getItemCount(): Int = items.size

    fun update(newMembers: MutableList<FamilyMember>, newFines: List<IForceItem> = emptyList()) {
        items = newMembers
        allFines = newFines
        notifyDataSetChanged()
    }

    fun removeMember(member: FamilyMember) {
        val index = items.indexOf(member)
        if (index >= 0) {
            items.removeAt(index)
            notifyItemRemoved(index)
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
