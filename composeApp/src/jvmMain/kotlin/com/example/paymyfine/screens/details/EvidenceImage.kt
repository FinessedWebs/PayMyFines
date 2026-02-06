package com.example.paymyfine.screens.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
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
    var bitmap by remember { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }

    LaunchedEffect(token) {

        val bytes: ByteArray =
            client
                .get("$baseUrl/infringements/image/stream?evidenceToken=$token")
                .body()

        bitmap = withContext(Dispatchers.Default) {
            SkiaImage
                .makeFromEncoded(bytes)
                .toComposeImageBitmap()
        }
    }

    bitmap?.let {
        Image(
            bitmap = it,
            contentDescription = null,
            modifier = Modifier.size(110.dp)
        )
    }
}
