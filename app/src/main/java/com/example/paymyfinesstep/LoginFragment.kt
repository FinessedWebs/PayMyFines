package com.example.paymyfinesstep

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.paymyfinesstep.api.ApiBackend
import com.example.paymyfinesstep.api.AuthApi
import com.example.paymyfinesstep.api.LoginRequest
import com.example.paymyfinesstep.api.ReactivateRequest
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {

    private val api: AuthApi by lazy {
        ApiBackend.create(requireContext(), AuthApi::class.java)
    }


    private val prefs by lazy {
        requireContext().getSharedPreferences("paymyfines_prefs", 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.fragment_login, container, false)

        val email = v.findViewById<TextInputEditText>(R.id.editTextEmail)
        val password = v.findViewById<TextInputEditText>(R.id.editTextPassword)
        val loginBtn = v.findViewById<MaterialButton>(R.id.buttonLogin)

        v.findViewById<TextView>(R.id.textGoToSignup).setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        loginBtn.setOnClickListener {
            val emailStr = email.text.toString().trim()
            val passStr = password.text.toString().trim()

            if (emailStr.isEmpty() || passStr.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(emailStr, passStr)
        }

        return v



    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = api.login(LoginRequest(email, password))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    // Save token + user info
                    prefs.edit()
                        .putString("jwt_token", body.token)
                        .putString("fullName", body.fullName)
                        .putString("email", body.email)
                        .putString("idNumber", body.idNumber)
                        .apply()

                    requireActivity().invalidateOptionsMenu()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Welcome back!", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                }
                else {
                    // read backend message
                    val errorBody = response.errorBody()?.string()
                    println("LOGIN ERROR BODY = $errorBody")

                    // ⭐ Check deactivated account BEFORE showing invalid credentials
                    if (errorBody?.contains("Account deactivated", ignoreCase = true) == true) {
                        withContext(Dispatchers.Main) {
                            showReactivateOption(email)
                        }
                        return@launch
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "Invalid credentials",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    private fun showReactivateOption(email: String) {
        val btnReactivate =
            view?.findViewById<MaterialButton>(R.id.btnReactivate) ?: return

        btnReactivate.visibility = View.VISIBLE

        btnReactivate.setOnClickListener {
            reactivateUser(email)
        }

        Toast.makeText(requireContext(),
            "Your account is deactivated. Tap REACTIVATE.",
            Toast.LENGTH_LONG
        ).show()
    }


    private fun reactivateUser(email: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = api.reactivate(ReactivateRequest(email))

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        response.message ?: "Account reactivated!",
                        Toast.LENGTH_LONG
                    ).show()

                    view?.findViewById<MaterialButton>(R.id.btnReactivate)
                        ?.visibility = View.GONE
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }




}