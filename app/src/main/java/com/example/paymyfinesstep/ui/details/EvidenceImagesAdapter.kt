package com.example.paymyfinesstep.ui.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.ApiBackend

class EvidenceImagesAdapter(
    private var tokens: List<String>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<EvidenceImagesAdapter.VH>() {


    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageEvidence)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_evidence_image, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = tokens.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val token = tokens[position]

        val prefs = holder.itemView.context.getSharedPreferences("paymyfines_prefs", Context.MODE_PRIVATE)
        val jwt = prefs.getString("jwt_token", null)

        val url = "${ApiBackend.baseUrl()}infringements/image/stream?evidenceToken=$token"

        val glideUrl = GlideUrl(
            url,
            LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer $jwt")
                .build()
        )

        Glide.with(holder.itemView.context)
            .load(glideUrl)
            .placeholder(R.drawable.ic_report)
            .error(R.drawable.ic_report)
            .into(holder.image)

        holder.itemView.setOnClickListener { onClick(token) }
    }


    fun update(newTokens: List<String>) {
        tokens = newTokens
        notifyDataSetChanged()
    }
}
