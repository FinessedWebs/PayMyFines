package com.example.paymyfine.screens.details

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

@Composable
actual fun EvidenceImage(
    client: HttpClient,
    baseUrl: String,
    token: String
) {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showPreview by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        try {
            isLoading = true

            val bytes: ByteArray =
                client.get(
                    "$baseUrl/infringements/image/stream?evidenceToken=$token"
                ).body()

            val androidBitmap =
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

            bitmap = androidBitmap?.asImageBitmap()

        } catch (e: Exception) {
            println("IMAGE LOAD ERROR: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // ⭐ Thumbnail
    Box(
        modifier = Modifier
            .size(110.dp),
        contentAlignment = Alignment.Center
    ) {

        when {
            isLoading -> {
                CircularProgressIndicator()
            }

            bitmap != null -> {
                Image(
                    bitmap = bitmap!!,
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp)
                        .clickable { showPreview = true }
                )
            }
        }
    }

    // ⭐ Fullscreen Preview
    if (showPreview && bitmap != null) {
        Dialog(onDismissRequest = { showPreview = false }) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { showPreview = false },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = bitmap!!,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
