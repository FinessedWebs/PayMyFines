package com.example.paymyfine.screens.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image as SkiaImage

@Composable
actual fun EvidenceImage(
    client: HttpClient,
    baseUrl: String,
    token: String
) {
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showPreview by remember { mutableStateOf(false) }

    // ⭐ Load image
    LaunchedEffect(token) {
        try {
            println("DESKTOP loading token: $token")

            val bytes: ByteArray =
                client.get("$baseUrl/infringements/image/stream?evidenceToken=$token")
                    .body()

            println("Bytes size: ${bytes.size}")

            // Decode off main thread
            bitmap = withContext(Dispatchers.Default) {
                SkiaImage
                    .makeFromEncoded(bytes)
                    .toComposeImageBitmap()
            }

        } catch (e: Exception) {
            println("DESKTOP IMAGE ERROR: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    // ⭐ Thumbnail
    Box(
        modifier = Modifier
            .size(120.dp),
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
                        .fillMaxSize()
                        .clickable { showPreview = true }
                )
            }

            else -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Text("No image")
                }
            }
        }
    }

    // ⭐ Fullscreen preview
    if (showPreview && bitmap != null) {
        Dialog(onCloseRequest = { showPreview = false }) {
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
