package com.example.paymyfine.platform

import androidx.compose.runtime.Composable

interface ImagePicker {
    fun pickImage(onResult: (ByteArray?) -> Unit)
}

@Composable
expect fun rememberPlatformImagePicker(): com.example.paymyfine.platform.ImagePicker