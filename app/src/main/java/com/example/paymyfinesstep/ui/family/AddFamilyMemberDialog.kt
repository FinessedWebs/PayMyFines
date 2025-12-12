package com.example.paymyfinesstep.ui.family

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.*
import com.example.paymyfinesstep.databinding.DialogAddFamilyMemberBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddFamilyMemberDialogFragment(
    private val onMemberAdded: (() -> Unit)? = null
) : DialogFragment() {

    private var _binding: DialogAddFamilyMemberBinding? = null
    private val binding get() = _binding!!

    private val familyApi: FamilyApi by lazy {
        ApiBackend.create(requireContext(), FamilyApi::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogAddFamilyMemberBinding.inflate(LayoutInflater.from(context))
        val view = binding.root

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.btnAddFamilyMember.setOnClickListener {
            submitMember()
        }

        return dialog
    }

    private fun submitMember() {
        val fullName = binding.editFullName.text.toString().trim()
        val surname = binding.editSurname.text.toString().trim()
        val idNumber = binding.editIdNumber.text.toString().trim()
        val email = binding.editEmail.text.toString().trim()
        val cell = binding.editCell.text.toString().trim()

        if (fullName.isEmpty() || surname.isEmpty() || idNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            try {
                val request = FamilyAddRequest(fullName, surname, idNumber, email, cell)

                val response = withContext(Dispatchers.IO) {
                    familyApi.addFamily(request)
                }

                if (response.error != null) {
                    Toast.makeText(requireContext(), response.error, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), response.message ?: "Member added", Toast.LENGTH_SHORT).show()
                    onMemberAdded?.invoke()     // Tell HomeFragment to refresh list
                    dismiss()
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog_round)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
