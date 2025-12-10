package com.example.paymyfinesstep.ui.family

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paymyfinesstep.HomeGroupedAdapter
import com.example.paymyfinesstep.InfringementsApi
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.ApiBackend
import kotlinx.coroutines.launch

class FamilyFinesFragment : Fragment(R.layout.fragment_family_fines) {/*

    private val api by lazy { ApiBackend.create(InfringementsApi::class.java) }

    private val args by navArgs<FamilyFinesFragmentArgs>()

    private val prefs by lazy {
        requireContext().getSharedPreferences("paymyfines_prefs", 0)
    }

    private lateinit var recycler: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var adapter: HomeGroupedAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        *//*requireActivity().title = "${args.familyMemberIdNumber}'s Fines"*//*


        recycler = view.findViewById(R.id.recyclerFamilyFines)
        progress = view.findViewById(R.id.progressBarFamilyFines)

        adapter = HomeGroupedAdapter(emptyList()) { fine ->
            val action = FamilyFinesFragmentDirections
                .actionFamilyFinesFragmentToFineDetailsFragment(fine)
            findNavController().navigate(action)
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        val jwt = prefs.getString("jwt_token", null) ?: return

        loadFines(jwt)
    }

    private fun loadFines(jwt: String) {
        progress.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val result = api.getFamilyInfringements(
                    token = "Bearer $jwt",
                    idNumber = args.familyMemberIdNumber
                )

                progress.visibility = View.GONE
                adapter.updateData(result.iForce ?: emptyList())

            } catch (e: Exception) {
                progress.visibility = View.GONE
                Toast.makeText(requireContext(),
                    "Failed to load fines: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }*/
}