package com.example.paymyfinesstep.ui.family

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.FamilyMember
import com.example.paymyfinesstep.api.IForceItem
import com.example.paymyfinesstep.FinesAdapter

class FamilyAdapter(
    private var items: List<FamilyMember>,
    private var allFines: List<IForceItem>,
    private val onFineClick: (IForceItem) -> Unit,
    private val onMemberClick: (FamilyMember) -> Unit
) : RecyclerView.Adapter<FamilyAdapter.FamilyVH>() {

    private var expandedUserId: String? = null

    inner class FamilyVH(view: View) : RecyclerView.ViewHolder(view) {

        // HEADER
        val userHeader: View = view.findViewById(R.id.userHeader)
        val name: TextView = view.findViewById(R.id.textFamilyName)
        val idNumber: TextView = view.findViewById(R.id.textFamilyIdNumber)
        val badge: TextView = view.findViewById(R.id.textHasAccountBadge)
        val arrow: ImageView = view.findViewById(R.id.iconExpandArrow)

        // EXPANDED
        val expandContainer: View = view.findViewById(R.id.expandContainer)
        val noFinesText: TextView = view.findViewById(R.id.textNoFines)
        val recyclerFines: RecyclerView = view.findViewById(R.id.recyclerUserFines)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_family_user, parent, false)
        return FamilyVH(v)
    }

    override fun onBindViewHolder(holder: FamilyVH, position: Int) {
        val item = items[position]

        // HEADER TEXT
        holder.name.text = "${item.fullName} ${item.surname}"
        holder.idNumber.text = "ID: ${item.idNumber}"
        holder.badge.visibility = if (item.hasAccount) View.VISIBLE else View.GONE

        // IS EXPANDED?
        val isExpanded = expandedUserId == item.id
        setExpansionState(holder, isExpanded)

        // GET FINES FOR USER
        val userFines = allFines.filter { it.userIdNumber == item.idNumber }

        if (isExpanded) {
            if (userFines.isEmpty()) {
                holder.noFinesText.visibility = View.VISIBLE
                holder.recyclerFines.visibility = View.GONE
            } else {
                holder.noFinesText.visibility = View.GONE
                holder.recyclerFines.visibility = View.VISIBLE

                holder.recyclerFines.layoutManager =
                    LinearLayoutManager(holder.itemView.context)

                holder.recyclerFines.adapter = FinesAdapter(userFines) { fine ->
                    onFineClick(fine)   // ← opens FineDetailsFragment
                }

            }
        }

        // CLICK TO EXPAND/COLLAPSE
        holder.userHeader.setOnClickListener {

            // COLLAPSE OTHERS
            expandedUserId = if (isExpanded) null else item.id

            notifyDataSetChanged()
        }

        holder.itemView.setOnClickListener { onMemberClick(item) }

    }

    override fun getItemCount() = items.size

    fun update(newMembers: List<FamilyMember>, newFines: List<IForceItem>) {
        items = newMembers
        allFines = newFines
        notifyDataSetChanged()
    }

    // --------------------------------------------------------
    // SMOOTH EXPANDING / COLLAPSING ANIMATION 🎉
    // --------------------------------------------------------
    private fun setExpansionState(holder: FamilyVH, expand: Boolean) {

        if (expand) {
            holder.expandContainer.visibility = View.VISIBLE
            holder.expandContainer.alpha = 0f
            holder.expandContainer.animate().alpha(1f).setDuration(200).start()

            holder.arrow.animate().rotation(180f).setDuration(200).start()
        } else {
            holder.expandContainer.animate().alpha(0f)
                .setDuration(150)
                .withEndAction {
                    holder.expandContainer.visibility = View.GONE
                }
                .start()

            holder.arrow.animate().rotation(0f).setDuration(200).start()
        }
    }
}
