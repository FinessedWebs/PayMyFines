package com.example.paymyfine.screens.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.skia.Image as SkiaImage
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

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
            val bytes: ByteArray =
                client.get(
                    "$baseUrl/infringements/image/stream?evidenceToken=$token"
                ).body()

            bitmap =
                SkiaImage.makeFromEncoded(bytes)
                    .toComposeImageBitmap()

        } catch (e: Exception) {
            println("iOS image error: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier.size(110.dp),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> CircularProgressIndicator()

            bitmap != null -> Image(
                bitmap = bitmap!!,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showPreview = true }
            )
        }
    }

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
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


