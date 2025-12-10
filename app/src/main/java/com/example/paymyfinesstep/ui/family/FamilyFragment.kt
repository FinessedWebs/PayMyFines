package com.example.paymyfinesstep.ui.family

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.ApiBackend
import com.example.paymyfinesstep.api.FamilyApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class FamilyFragment : Fragment(R.layout.fragment_family) {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: FamilyAdapter
    private lateinit var btnAdd: FloatingActionButton

    private val api by lazy { ApiBackend.create(requireContext(),FamilyApi::class.java) }

    private val prefs by lazy {
        requireContext().getSharedPreferences("paymyfines_prefs", 0)
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = view.findViewById(R.id.recyclerFamily)
        btnAdd = view.findViewById(R.id.btnAddFamily)

        adapter = FamilyAdapter(emptyList()) { member ->
            val action = FamilyFragmentDirections
                .actionFamilyFragmentToFamilyFinesFragment(member.idNumber)
            findNavController().navigate(action)
        }


        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_familyFragment_to_addFamilyFragment)
        }

        loadFamilyMembers()
    }

    private fun loadFamilyMembers() {
        val jwt = requireContext()
            .getSharedPreferences("paymyfines_prefs", 0)
            .getString("jwt_token", null)

        if (jwt.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Session expired", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val familyList = api.getFamilyMembers("Bearer $jwt")
                adapter.update(familyList)

            } catch (e: Exception) {
                Toast.makeText(requireContext(),
                    "Failed to load family: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }*/

}
