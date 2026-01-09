package com.example.paymyfinesstep.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.ApiBackend
import com.example.paymyfinesstep.api.AuthApi
import com.example.paymyfinesstep.api.ChangePasswordRequest
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private lateinit var inputCurrent: TextInputEditText
    private lateinit var inputNew: TextInputEditText
    private lateinit var layoutCurrent: TextInputLayout
    private lateinit var layoutNew: TextInputLayout
    private lateinit var btnSubmit: Button
    private lateinit var inputConfirm: TextInputEditText
    private lateinit var layoutConfirm: TextInputLayout



    private val api by lazy {
        ApiBackend.create(requireContext(), AuthApi::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputCurrent = view.findViewById(R.id.inputCurrentPassword)
        inputNew = view.findViewById(R.id.inputNewPassword)
        inputConfirm = view.findViewById(R.id.inputConfirmPassword)

        layoutCurrent = view.findViewById(R.id.layoutCurrentPassword)
        layoutNew = view.findViewById(R.id.layoutNewPassword)
        layoutConfirm = view.findViewById(R.id.layoutConfirmPassword)

        btnSubmit = view.findViewById(R.id.btnChangePassword)

        // Submit
        btnSubmit.setOnClickListener {
            if (validate()) {
                changePassword()
            }
        }

        // 🔹 Clear errors while typing
        inputCurrent.addTextChangedListener {
            layoutCurrent.error = null
        }

        inputNew.addTextChangedListener {
            layoutNew.error = null
        }

        inputConfirm.addTextChangedListener {
            layoutConfirm.error = null
        }
    }


    private fun validate(): Boolean {
        layoutCurrent.error = null
        layoutNew.error = null
        layoutConfirm.error = null

        val current = inputCurrent.text.toString()
        val newPass = inputNew.text.toString()
        val confirmPass = inputConfirm.text.toString()

        if (current.isBlank()) {
            layoutCurrent.error = "Enter current password"
            return false
        }

        if (newPass.length < 8) {
            layoutNew.error = "Minimum 8 characters"
            return false
        }

        if (newPass != confirmPass) {
            layoutConfirm.error = "Passwords do not match"
            return false
        }

        if (current == newPass) {
            layoutNew.error = "New password must be different"
            return false
        }

        return true
    }


    private fun changePassword() {
        btnSubmit.isEnabled = false

        lifecycleScope.launch {
            try {
                val response = api.changePassword(
                    ChangePasswordRequest(
                        inputCurrent.text.toString(),
                        inputNew.text.toString()
                    )
                )

                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Password changed. Please log in again.",
                        Toast.LENGTH_LONG
                    ).show()

                    forceLogout()

                } else if (response.code() == 401) {
                    layoutCurrent.error = "Incorrect current password"
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to change password",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } finally {
                btnSubmit.isEnabled = true
            }
        }
    }

    private fun forceLogout() {
        requireContext()
            .getSharedPreferences("paymyfines_prefs", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        findNavController().navigate(
            R.id.loginFragment,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true)
                .build()
        )
    }
}
