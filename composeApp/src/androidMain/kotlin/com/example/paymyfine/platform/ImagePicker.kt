package com.example.paymyfine.platform

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.io.InputStream

class AndroidImagePicker(
    private val launcher: (String) -> Unit,
    private val setCallback: ((ByteArray?) -> Unit) -> Unit
) : ImagePicker {

    override fun pickImage(onResult: (ByteArray?) -> Unit) {
        setCallback(onResult)
        launcher("image/*")
    }
}

@Composable
actual fun rememberPlatformImagePicker(): ImagePicker {

    val context = LocalContext.current
    var callback by remember { mutableStateOf<((ByteArray?) -> Unit)?>(null) }

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->

            if (uri == null) {
                callback?.invoke(null)
                return@rememberLauncherForActivityResult
            }

            try {
                val stream: InputStream? =
                    context.contentResolver.openInputStream(uri)

                val bytes = stream?.readBytes()
                stream?.close()

                callback?.invoke(bytes)

            } catch (e: Exception) {
                callback?.invoke(null)
            }
        }

    return remember {
        AndroidImagePicker(
            launcher = { launcher.launch(it) },
            setCallback = { callback = it }
        )
    }
}