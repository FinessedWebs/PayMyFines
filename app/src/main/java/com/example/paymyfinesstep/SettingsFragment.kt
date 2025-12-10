package com.example.paymyfinesstep

import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.paymyfinesstep.api.ApiBackend
import com.example.paymyfinesstep.api.AuthApi
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    // AuthInterceptor now handles JWT automatically — context must be passed
    private val authApi: AuthApi by lazy {
        ApiBackend.create(requireContext(), AuthApi::class.java)
    }

   /* private val api by lazy { ApiBackend.create(requireContext(), InfringementsApi::class.java) }*/
   private val api: AuthApi by lazy {
       ApiBackend.create(requireContext(), AuthApi::class.java)
   }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textName = view.findViewById<TextView>(R.id.textUserName)
        val textEmail = view.findViewById<TextView>(R.id.textUserEmail)
        val textId = view.findViewById<TextView>(R.id.textUserId)
        /*val btnDeactivate = view.findViewById<MaterialButton>(R.id.btnDeactivate)*/


        val prefs = requireContext().getSharedPreferences("paymyfines_prefs", Context.MODE_PRIVATE)

        // Load cached values first
        textName.text = prefs.getString("fullName", "Loading...")
        textEmail.text = prefs.getString("email", "Loading...")
        textId.text = "ID Number: ${prefs.getString("idNumber", "--------")}"

        // Now fetch updated profile via API
        lifecycleScope.launch {
            try {
                println("DEBUG_SETTINGS: Calling /auth/me (token handled by interceptor)")
                val user = authApi.getCurrentUser()   // <-- Token auto-injected

                // Update UI
                textName.text = user.fullName
                textEmail.text = user.email
                textId.text = "ID Number: ${user.idNumber}"

                // Save new cache
                prefs.edit()
                    .putString("fullName", user.fullName)
                    .putString("email", user.email)
                    .putString("idNumber", user.idNumber)
                    .apply()

            } catch (e: Exception) {
                println("DEBUG_SETTINGS_ERROR: ${e.localizedMessage}")
            }
        }

        // Navigation buttons
        view.findViewById<MaterialButton>(R.id.btnAccountSettings)
            .setOnClickListener { findNavController().navigate(R.id.accountSettingsFragment) }

        view.findViewById<MaterialButton>(R.id.btnNotificationPreferences)
            .setOnClickListener { findNavController().navigate(R.id.notificationPreferencesFragment) }

        view.findViewById<MaterialButton>(R.id.btnHelp)
            .setOnClickListener { findNavController().navigate(R.id.helpFragment) }

        view.findViewById<MaterialButton>(R.id.btnTerms)
            .setOnClickListener { findNavController().navigate(R.id.termsFragment) }

        view.findViewById<MaterialButton>(R.id.btnAbout)
            .setOnClickListener { findNavController().navigate(R.id.aboutFragment) }

        /*btnDeactivate.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Deactivate Account")
                .setMessage("Are you sure you want to deactivate your account?")
                .setPositiveButton("Yes") { _, _ ->
                    deactivateAccount()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }*/

    }

    private fun deactivateAccount() {
        lifecycleScope.launch {
            try {
                val response = api.deactivateAccount()

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Account deactivated", Toast.LENGTH_LONG).show()

                    // Logout the user locally
                    val prefs = requireContext().getSharedPreferences("paymyfines_prefs", MODE_PRIVATE)
                    prefs.edit().clear().apply()

                    // Navigate to login
                    findNavController().navigate(
                        R.id.loginFragment,
                        null,
                        NavOptions.Builder()
                            .setPopUpTo(R.id.nav_graph, true)
                            .build()
                    )

                } else {
                    Toast.makeText(requireContext(), "Failed to deactivate", Toast.LENGTH_LONG).show()
                }

            } catch (ex: Exception) {
                Toast.makeText(requireContext(), ex.message, Toast.LENGTH_LONG).show()
            }
        }
    }


}