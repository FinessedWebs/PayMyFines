package com.example.paymyfinesstep.ui.family

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog   // ✅ IMPORTANT
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.ApiBackend
import com.example.paymyfinesstep.api.FamilyApi
import com.example.paymyfinesstep.api.FamilyMember
import com.example.paymyfinesstep.api.IForceItem
import com.example.paymyfinesstep.databinding.FragmentFamilyBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FamilyFragment : Fragment(R.layout.fragment_family) {

    private var _binding: FragmentFamilyBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: FamilyAdapter

    private val familyApi: FamilyApi by lazy {
        ApiBackend.create(requireContext(), FamilyApi::class.java)
    }

    private var allFines: List<IForceItem> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentFamilyBinding.bind(view)

        setupRecycler()
        setupAdapter()
        enableSwipeToDelete()

        loadFamilyMembers()
    }

    private fun setupRecycler() {
        binding.recyclerFamily.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupAdapter() {
        adapter = FamilyAdapter(
            items = mutableListOf(),
            allFines = allFines,
            onFineClick = { /* TODO navigate */ },
            onMemberClick = { /* TODO open member */ },
            onDelete = { member -> confirmDelete(member) },
            onEdit = { /* TODO edit */ }
        )
        binding.recyclerFamily.adapter = adapter
    }

    private fun loadFamilyMembers() {
        lifecycleScope.launch {
            try {
                val members = withContext(Dispatchers.IO) {
                    familyApi.getFamilyMembers()
                }
                adapter.update(members.toMutableList(), allFines)

            } catch (ex: Exception) {
                Toast.makeText(requireContext(), "Failed to load members: ${ex.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun confirmDelete(member: FamilyMember) {
        AlertDialog.Builder(requireContext())
            .setTitle("Remove Family Member")
            .setMessage("Remove ${member.fullName}?")
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
                // If swipe triggered it, restore item position
                loadFamilyMembers()
            }
            .setPositiveButton("Delete") { _, _ ->
                executeDelete(member)
            }
            .show()
    }

    private fun executeDelete(member: FamilyMember) {
        Log.d("DELETE_TEST", "Deleting linkId: ${member.linkId}")

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    // ✅ IMPORTANT: DELETE by linkId
                    familyApi.deleteFamilyMember(member.linkId)
                }

                if (response.isSuccessful) {
                    adapter.removeMember(member)
                    Toast.makeText(requireContext(), "Member removed", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Delete failed (${response.code()})",
                        Toast.LENGTH_LONG
                    ).show()
                    loadFamilyMembers()
                }

            } catch (ex: Exception) {
                Toast.makeText(requireContext(), "Error: ${ex.message}", Toast.LENGTH_LONG).show()
                loadFamilyMembers()
            }
        }
    }

    private fun enableSwipeToDelete() {
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val member = adapter.getItem(viewHolder.adapterPosition)
                confirmDelete(member)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val background = ColorDrawable(Color.parseColor("#E53935"))
                val itemView = viewHolder.itemView

                background.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )
                background.draw(c)

                super.onChildDraw(
                    c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }
        }

        ItemTouchHelper(callback).attachToRecyclerView(binding.recyclerFamily)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
