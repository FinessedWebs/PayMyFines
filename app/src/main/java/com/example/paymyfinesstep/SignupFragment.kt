package com.example.paymyfinesstep

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.paymyfinesstep.api.ApiBackend
import com.example.paymyfinesstep.api.AuthApi
import com.example.paymyfinesstep.api.SignupRequest
import com.example.paymyfinesstep.databinding.FragmentSignupBinding
import kotlinx.coroutines.launch

class SignupFragment : Fragment(R.layout.fragment_signup) {

    private lateinit var binding: FragmentSignupBinding

    private val api: AuthApi by lazy { ApiBackend.create(requireContext(),AuthApi::class.java) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentSignupBinding.bind(view)

        binding.buttonSignup.setOnClickListener {

            val name = binding.editFullName.text.toString()
            val email = binding.editEmail.text.toString()
            val id = binding.editIdNumber.text.toString()
            val password = binding.editPassword.text.toString()
            val repeatPassword = binding.editRepeatPassword.text.toString()

            // 🔒 Mandatory validations
            if (name.isEmpty() || email.isEmpty() || id.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != repeatPassword) {
                Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2️⃣ Email format validation
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.editEmail.error = "Invalid email format"
                binding.editEmail.requestFocus()
                return@setOnClickListener
            }

            // 3️⃣ South African ID Validation — must be exactly 13 digits
            if (id.length != 13 || !id.all { it.isDigit() }) {
                binding.editIdNumber.error = "ID number must be exactly 13 digits"
                binding.editIdNumber.requestFocus()
                return@setOnClickListener
            }


            val request = SignupRequest(
                fullName = name,
                email = email,
                idNumber = id,
                password = password
            )

            lifecycleScope.launch {
                try {
                    val response = api.signup(request)

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Signup successful", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Signup failed: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.textGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
    }
}