package com.example.paymyfinesstep.ui.details

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.paymyfinesstep.R
import com.example.paymyfinesstep.api.IForceItem
import com.example.paymyfinesstep.cart.CartAddedBottomSheet
import com.example.paymyfinesstep.cart.CartItem
import com.example.paymyfinesstep.cart.CartManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class FineDetailsFragment : Fragment(R.layout.fragment_fine_details) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Receive the IForceItem passed via arguments (Serializable)
        val fine = arguments?.getSerializable("fine") as? IForceItem
        if (fine == null) {
            Toast.makeText(requireContext(), "No fine data", Toast.LENGTH_SHORT).show()
            return
        }

        // UI references
        val tvNotice = view.findViewById<TextView>(R.id.textNoticeNumber)
        val tvStatus = view.findViewById<TextView>(R.id.textStatus)
        val tvCharges = view.findViewById<TextView>(R.id.textCharges)
        val tvLocation = view.findViewById<TextView>(R.id.textLocation)
        val tvVehicle = view.findViewById<TextView>(R.id.textVehicle)
        val tvCourtDate = view.findViewById<TextView>(R.id.textCourtDate)
        val tvAmount = view.findViewById<TextView>(R.id.textAmount)

        val btnAddToCart = view.findViewById<MaterialButton>(R.id.btnAddToCart)
        val btnDownload = view.findViewById<MaterialButton>(R.id.btnDownload)
        val recyclerEvidence = view.findViewById<RecyclerView>(R.id.recyclerEvidenceImages)
        val textNoEvidence = view.findViewById<TextView>(R.id.textNoEvidence)


        // Populate
        tvNotice.text = "Notice: ${fine.noticeNumber ?: "—"}"
        tvStatus.text = "Status: ${fine.status ?: "—"}"
        tvCharges.text = fine.chargeDescriptions?.joinToString("\n") ?: "No description"
        tvLocation.text = "Location: ${fine.offenceLocation ?: "—"}"
        tvVehicle.text = "Vehicle: ${fine.vehicleLicenseNumber ?: "—"}"
        tvCourtDate.text = "Court Date: ${formatCourtDate(fine.courtDate)}"
        tvAmount.text = "Amount: ${formatAmount(fine.amountDueInCents)}"

        // ✅ Evidence Images (Carousel)
        // ✅ Evidence Images (Only show for UUID tokens = unpaid evidence)
        val rawTokens = fine.images.orEmpty()

// Paid fines often have tokens like "19560991" which will never work on stream endpoint
        val tokens = rawTokens.filter { isUuidToken(it) }

        if (tokens.isEmpty()) {
            recyclerEvidence.visibility = View.GONE
            textNoEvidence.visibility = View.VISIBLE
        } else {
            textNoEvidence.visibility = View.GONE
            recyclerEvidence.visibility = View.VISIBLE

            val evidenceAdapter = EvidenceImagesAdapter(tokens) { clickedToken ->
                EvidenceImageDialog(clickedToken)
                    .show(parentFragmentManager, "evidence_fullscreen")
            }

            recyclerEvidence.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            recyclerEvidence.adapter = evidenceAdapter
        }



        // ✅ Add to cart using CartManager + CartItem
        btnAddToCart.setOnClickListener {
            addToCart(fine)

            // SHOW BOTTOM SHEET CONFIRMATION
            CartAddedBottomSheet(
                onGoToCart = {
                    val action = FineDetailsFragmentDirections
                        .actionFineDetailsFragmentToCartFragment()
                    findNavController().navigate(action)
                }
            ).show(parentFragmentManager, "cart_added_sheet")
        }

        // Download -> create PDF and save to Downloads
        btnDownload.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {

                // Create preview bitmap + PDF bytes
                val (previewBitmap, pdfBytes) = createPdfPreview(requireContext(), fine)

                withContext(Dispatchers.Main) {
                    PdfPreviewDialog(
                        pdfBitmap = previewBitmap,
                        pdfBytes = pdfBytes,
                        fileName = "Fine_${fine.noticeNumber ?: "document"}.pdf"
                    ).show(parentFragmentManager, "pdf_preview")
                }
            }
        }


    }

    private fun isUuidToken(token: String): Boolean {
        return token.matches(
            Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
        )
    }


    // ✅ NEW IMPLEMENTATION – uses CartItem instead of SharedPreferences string set
    private fun addToCart(fine: IForceItem) {
        val item = CartItem(
            noticeNumber = fine.noticeNumber ?: UUID.randomUUID().toString(),
            description = fine.chargeDescriptions?.firstOrNull()
                ?: "Traffic fine",
            amountInCents = fine.amountDueInCents ?: 0
        )

        lifecycleScope.launch {
            CartManager.add(requireContext(), item)
        }

        Toast.makeText(requireContext(), "Added to cart", Toast.LENGTH_SHORT).show()
    }

    private fun formatAmount(cents: Int?): String {
        val r = (cents ?: 0) / 100.0
        return "R%.2f".format(r)
    }

    private fun formatCourtDate(raw: String?): String {
        if (raw.isNullOrBlank()) return "--"
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val dt = LocalDateTime.parse(raw, DateTimeFormatter.ISO_DATE_TIME)
                dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            } else raw
        } catch (e: Exception) {
            raw
        }
    }

    private fun createPdfPreview(context: Context, fine: IForceItem): Pair<Bitmap, ByteArray> {

        val width = 595
        val height = 842

        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(width, height, 1).create()
        val page = doc.startPage(pageInfo)
        val pdfCanvas = page.canvas

        // Bitmap preview canvas
        val previewBitmap =
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val bmpCanvas = Canvas(previewBitmap)

        // Draw onto both PDF and bitmap
        drawPdfContent(context, fine, pdfCanvas)
        drawPdfContent(context, fine, bmpCanvas)

        doc.finishPage(page)

        // Convert PDF to byte array
        val output = ByteArrayOutputStream()
        doc.writeTo(output)
        doc.close()

        return previewBitmap to output.toByteArray()
    }

    private fun drawPdfContent(context: Context, fine: IForceItem, canvas: Canvas) {
        val paint = Paint().apply { textSize = 12f }
        var y = 40f

        // ---- Logo ----
        val logoBmp = BitmapFactory.decodeResource(context.resources, R.drawable.paymyfines_text_logo)
        val scaledLogo = Bitmap.createScaledBitmap(logoBmp, 250, 80, true)
        canvas.drawBitmap(scaledLogo, 40f, y, null)
        y += 110f

        fun write(str: String) {
            canvas.drawText(str, 40f, y, paint)
            y += 18f
        }

        write("Infringement Notice")
        write("--------------------------------")
        write("Notice: ${fine.noticeNumber ?: "—"}")
        write("Status: ${fine.status ?: "—"}")
        write("Amount: ${formatAmount(fine.amountDueInCents)}")
        write("Location: ${fine.offenceLocation ?: "—"}")
        write("Vehicle: ${fine.vehicleLicenseNumber ?: "—"}")
        write("Court Date: ${formatCourtDate(fine.courtDate)}")
        write("")
        write("Charge(s):")

        fine.chargeDescriptions?.forEach { write("- $it") }
    }

    private fun savePdf(context: Context, bytes: ByteArray, fileName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }

            val uri = context.contentResolver.insert(
                MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                values
            )!!

            context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }

            values.clear()
            values.put(MediaStore.Downloads.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)

            Toast.makeText(context, "Saved to Downloads", Toast.LENGTH_LONG).show()
        }
    }


}