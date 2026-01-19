package com.example.paymyfinesstep.ui.details

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.ApiBackend

class EvidenceImageDialog(private val evidenceToken: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)

        val imageView = ImageView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(resources.getColor(android.R.color.black))
            scaleType = ImageView.ScaleType.FIT_CENTER
            setOnClickListener { dismiss() }
        }

        // ✅ 1) Read JWT from SharedPreferences
        val prefs = requireContext().getSharedPreferences("paymyfines_prefs", Context.MODE_PRIVATE)
        val jwt = prefs.getString("jwt_token", null)

        Log.d("IMG_AUTH", "JWT exists? ${!jwt.isNullOrBlank()} length=${jwt?.length ?: 0}")

        // ✅ 2) Build backend URL
        val url = "${ApiBackend.baseUrl()}infringements/image/stream?evidenceToken=$evidenceToken"

        // ✅ 3) Add Authorization header for Glide request
        val glideUrl = GlideUrl(
            url,
            LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer $jwt")
                .build()
        )

        Glide.with(requireContext())
            .load(glideUrl)
            .placeholder(R.drawable.ic_report)
            .error(R.drawable.ic_report)
            .into(imageView)

        dialog.setContentView(imageView)
        return dialog



    }
}
