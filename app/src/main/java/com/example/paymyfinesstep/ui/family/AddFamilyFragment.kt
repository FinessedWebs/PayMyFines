package com.example.paymyfinesstep.ui.family

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.ApiBackend
import com.example.paymyfinesstep.api.FamilyAddRequest

import com.example.paymyfinesstep.api.FamilyApi

import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class AddFamilyFragment : Fragment(R.layout.dialog_add_family_member) {

    private val api by lazy { ApiBackend.create(requireContext(),FamilyApi::class.java) }

    private val prefs by lazy {
        requireContext().getSharedPreferences("paymyfines_prefs", 0)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fullName = view.findViewById<TextInputEditText>(R.id.editFullName)
        val surname = view.findViewById<TextInputEditText>(R.id.editSurname)
        val idNumber = view.findViewById<TextInputEditText>(R.id.editIdNumber)
        val email = view.findViewById<TextInputEditText>(R.id.editEmail)
        val cell = view.findViewById<TextInputEditText>(R.id.editCell)
        val btnAdd = view.findViewById<MaterialButton>(R.id.btnAddFamilyMember)

        btnAdd.setOnClickListener {
            val req = FamilyAddRequest(
                fullName = fullName.text.toString().trim(),
                surname = surname.text.toString().trim(),
                idNumber = idNumber.text.toString().trim(),
                email = email.text.toString().trim(),
                cell = cell.text.toString().trim()
            )

            validateAndSubmit(req)
        }
    }

    private fun validateAndSubmit(req: FamilyAddRequest) {
        if (req.fullName.isEmpty() || req.surname.isEmpty() ||
            req.idNumber.isEmpty() || req.email.isEmpty() || req.cell.isEmpty()
        ) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val jwt = prefs.getString("jwt_token", null)
        if (jwt.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Session expired", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = api.addFamily(req)


                Toast.makeText(requireContext(),
                    response.message ?: "Family member added successfully!",
                    Toast.LENGTH_LONG
                ).show()

                findNavController().navigateUp()

            } catch (e: Exception) {
                Toast.makeText(requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
