package com.example.paymyfinesstep.ui.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.ApiBackend
import com.example.paymyfinesstep.api.AuthApi
import com.example.paymyfinesstep.databinding.FragmentAccountSettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountSettingsFragment : Fragment(R.layout.fragment_account_settings) {

    private lateinit var binding: FragmentAccountSettingsBinding

    private val api: AuthApi by lazy {
        ApiBackend.create(requireContext(), AuthApi::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAccountSettingsBinding.bind(view)

        binding.btnDeactivateAccount.setOnClickListener {
            showDeactivateDialog()
        }
    }


    private fun showDeactivateDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Deactivate Account")
            .setMessage("Are you sure you want to deactivate your account? This will log you out.")
            .setPositiveButton("Yes") { _, _ ->
                deactivateAccount()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deactivateAccount() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = api.deactivateAccount()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Account deactivated", Toast.LENGTH_LONG).show()

                        logoutUser()
                    } else {
                        Toast.makeText(requireContext(), "Failed to deactivate account", Toast.LENGTH_LONG).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    private fun logoutUser() {
        val prefs = requireContext().getSharedPreferences("paymyfines_prefs", 0)
        prefs.edit().clear().apply()

        findNavController().navigate(
            R.id.loginFragment,
            null,
            NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build()
        )
    }
}