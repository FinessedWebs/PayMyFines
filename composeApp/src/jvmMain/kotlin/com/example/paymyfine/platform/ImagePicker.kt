package com.example.paymyfine.platform

import androidx.compose.runtime.*
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

class DesktopImagePicker : ImagePicker {

    override fun pickImage(onResult: (ByteArray?) -> Unit) {

        val dialog = FileDialog(Frame(), "Select Image")
        dialog.isVisible = true

        val file = dialog.file ?: run {
            onResult(null)
            return
        }

        val selected = File(dialog.directory, file)
        onResult(selected.readBytes())
    }
}

@Composable
actual fun rememberPlatformImagePicker(): ImagePicker {
    return remember { DesktopImagePicker() }
}