package com.example.paymyfinesstep.ui.profile

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.ApiBackend
import com.example.paymyfinesstep.api.AuthApi
import com.example.paymyfinesstep.api.UpdateProfileRequest
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.io.File

class ProfileDetailsFragment : Fragment() {

    // ---------------- UI ----------------
    private lateinit var inputFullName: TextInputEditText
    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputIdNumber: TextInputEditText
    private lateinit var layoutFullName: TextInputLayout
    private lateinit var layoutEmail: TextInputLayout
    private lateinit var imageProfileAvatar: ShapeableImageView
    private lateinit var btnChangePhoto: ImageButton
    private lateinit var btnSave: Button
    private lateinit var textChangePassword: TextView

    // ---------------- STATE ----------------
    private var originalName = ""
    private var originalEmail = ""
    private var isSaving = false

    // ✅ PER-USER KEY (idNumber-based)
    private lateinit var profileKey: String

    private val api by lazy {
        ApiBackend.create(requireContext(), AuthApi::class.java)
    }

    // ---------------- IMAGE PICKER ----------------
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null && ::profileKey.isInitialized) {
                saveProfileImage(uri, profileKey)
                loadProfileImage(profileKey)?.let { setCircularImage(it) }

                // 🔔 Notify HomeFragment
                parentFragmentManager.setFragmentResult(
                    "profile_image_updated",
                    Bundle.EMPTY
                )
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_profile_details, container, false)

        // Bind views
        inputFullName = view.findViewById(R.id.inputFullName)
        inputEmail = view.findViewById(R.id.inputEmail)
        inputIdNumber = view.findViewById(R.id.inputIdNumber)
        layoutFullName = view.findViewById(R.id.layoutFullName)
        layoutEmail = view.findViewById(R.id.layoutEmail)
        imageProfileAvatar = view.findViewById(R.id.imageProfileAvatar)
        btnChangePhoto = view.findViewById(R.id.btnChangePhoto)
        btnSave = view.findViewById(R.id.btnSaveProfile)
        textChangePassword = view.findViewById(R.id.textChangePassword)

        loadProfile()
        setupChangeDetection()

        btnSave.setOnClickListener {
            if (validateInput()) saveProfile()
        }

        btnChangePhoto.setOnClickListener {
            showPhotoMenu(it)
        }

        parentFragmentManager.setFragmentResult(
            "profile_image_updated",
            Bundle.EMPTY
        )


        textChangePassword.setOnClickListener {
            findNavController().navigate(
                R.id.action_profileDetailsFragment_to_changePasswordFragment
            )
        }

        return view
    }

    // ---------------- PHOTO MENU ----------------
    private fun showPhotoMenu(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)

        popup.menu.add("Change photo")
        popup.menu.add("Remove photo")

        popup.setOnMenuItemClickListener {
            when (it.title.toString()) {
                "Change photo" -> {
                    imagePicker.launch("image/*")
                    true
                }
                "Remove photo" -> {
                    removeProfileImage()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    // ---------------- IMAGE STORAGE ----------------

    private fun saveProfileImage(uri: Uri, key: String) {
        val context = requireContext()
        val inputStream = context.contentResolver.openInputStream(uri) ?: return

        val file = File(context.filesDir, "profile_avatar_$key.jpg")
        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }
    }


    /*private fun saveProfileImage(uri: Uri, key: String) {
        val context = requireContext()
        val inputStream = context.contentResolver.openInputStream(uri) ?: return

        val file = File(context.filesDir, "profile_avatar_$key.jpg")
        file.outputStream().use { inputStream.copyTo(it) }

        context.getSharedPreferences("paymyfines_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("profile_image_path_$key", file.absolutePath)
            .apply()
    }*/

    private fun loadProfileImage(key: String): File? {
        val file = File(requireContext().filesDir, "profile_avatar_$key.jpg")
        return if (file.exists()) file else null
    }


    /*private fun loadProfileImage(key: String): File? {
        val prefs = requireContext()
            .getSharedPreferences("paymyfines_prefs", Context.MODE_PRIVATE)

        return prefs.getString("profile_image_path_$key", null)?.let { File(it) }
    }*/

    private fun removeProfileImage() {
        val prefs = requireContext()
            .getSharedPreferences("paymyfines_prefs", Context.MODE_PRIVATE)

        val path = prefs.getString("profile_image_path_$profileKey", null)
        path?.let { File(it).delete() }

        prefs.edit()
            .remove("profile_image_path_$profileKey")
            .apply()

        imageProfileAvatar.setImageDrawable(null)
        applyProfileIconFromId(inputIdNumber.text.toString())
        imageProfileAvatar.invalidate()
    }

    private fun setupProfileImage(key: String, idNumber: String) {
        val file = loadProfileImage(key)
        if (file != null) {
            setCircularImage(file)
        } else {
            applyProfileIconFromId(idNumber)
        }
    }


    /*private fun setupProfileImage(key: String, idNumber: String) {
        val file = loadProfileImage(key)
        if (file != null && file.exists()) {
            setCircularImage(file)
        } else {
            applyProfileIconFromId(idNumber)
        }
    }*/

    private fun setCircularImage(file: File) {
        imageProfileAvatar.apply {
            setImageDrawable(null)
            setImageURI(Uri.fromFile(file))
            clearColorFilter()
            invalidate()
        }
    }

    // ---------------- FALLBACK AVATAR ----------------
    private fun applyProfileIconFromId(idNumber: String) {
        imageProfileAvatar.setImageResource(R.drawable.ic_person)
    }

    // ---------------- VALIDATION ----------------
    private fun validateInput(): Boolean {
        var valid = true
        layoutFullName.error = null
        layoutEmail.error = null

        if (inputFullName.text.toString().trim().isEmpty()) {
            layoutFullName.error = "Full name is required"
            valid = false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(inputEmail.text.toString().trim())
                .matches()
        ) {
            layoutEmail.error = "Enter a valid email address"
            valid = false
        }
        return valid
    }

    // ---------------- LOAD PROFILE ----------------
    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                val user = api.getCurrentUser()

                originalName = user.fullName
                originalEmail = user.email
                profileKey = user.idNumber   // ✅ single source of truth

                inputFullName.setText(originalName)
                inputEmail.setText(originalEmail)
                inputIdNumber.setText(user.idNumber)

                setupProfileImage(profileKey, user.idNumber)
                btnSave.isEnabled = false

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_LONG).show()
            }
        }
    }

    // ---------------- CHANGE DETECTION ----------------
    private fun setupChangeDetection() {
        inputFullName.addTextChangedListener { detectChanges() }
        inputEmail.addTextChangedListener { detectChanges() }
    }

    private fun detectChanges() {
        btnSave.isEnabled =
            !isSaving &&
                    (inputFullName.text.toString().trim() != originalName ||
                            inputEmail.text.toString().trim() != originalEmail)
    }

    // ---------------- SAVE PROFILE ----------------
    private fun saveProfile() {
        isSaving = true
        btnSave.isEnabled = false
        btnSave.text = "Saving..."

        val newName = inputFullName.text.toString().trim()
        val newEmail = inputEmail.text.toString().trim()

        lifecycleScope.launch {
            try {
                api.updateProfile(UpdateProfileRequest(newName, newEmail))
                originalName = newName
                originalEmail = newEmail

                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_LONG).show()
            } finally {
                isSaving = false
                btnSave.text = "Save changes"
                detectChanges()
            }
        }
    }
}
