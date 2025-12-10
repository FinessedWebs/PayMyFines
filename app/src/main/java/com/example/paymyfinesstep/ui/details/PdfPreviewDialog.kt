package com.example.paymyfinesstep.ui.details

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.DialogFragment
import com.example.paymyfinesstep.R
import com.google.android.material.button.MaterialButton

class PdfPreviewDialog(
    private val pdfBitmap: Bitmap,
    private val pdfBytes: ByteArray,
    private val fileName: String
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        Dialog(requireContext()).apply {
            setContentView(R.layout.dialog_pdf_preview)

            val img = findViewById<ImageView>(R.id.imgPdfPreview)
            val btnDownload = findViewById<MaterialButton>(R.id.btnDownload)

            img.setImageBitmap(pdfBitmap)

            btnDownload.setOnClickListener {
                savePdf(requireContext(), pdfBytes, fileName)
                dismiss()
            }
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
