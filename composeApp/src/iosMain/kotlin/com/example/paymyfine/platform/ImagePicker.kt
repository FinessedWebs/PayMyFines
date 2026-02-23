package com.example.paymyfine.platform

import androidx.compose.runtime.*
import platform.UIKit.*
import platform.Foundation.*
import platform.darwin.NSObject
import kotlinx.cinterop.refTo
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.posix.memcpy

class IOSImagePicker : NSObject(),
    ImagePicker,
    UIImagePickerControllerDelegateProtocol,
    UINavigationControllerDelegateProtocol {

    private var callback: ((ByteArray?) -> Unit)? = null

    override fun pickImage(onResult: (ByteArray?) -> Unit) {
        callback = onResult

        val picker = UIImagePickerController().apply {
            sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
            delegate = this@IOSImagePicker
        }

        UIApplication.sharedApplication
            .keyWindow
            ?.rootViewController
            ?.presentViewController(picker, true, null)
    }

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {

        val image =
            didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage

        val data = image?.let { UIImagePNGRepresentation(it) }

        @OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
        val bytes = data?.let { nsData ->

            val length = nsData.length.toInt()
            val byteArray = ByteArray(length)

            val rawPointer = nsData.bytes
            if (rawPointer != null) {
                byteArray.usePinned { pinned ->
                    platform.posix.memcpy(
                        pinned.addressOf(0),
                        rawPointer,
                        nsData.length
                    )
                }
            }

            byteArray
        }

        callback?.invoke(bytes)

        picker.dismissViewControllerAnimated(true, null)
    }

    override fun imagePickerControllerDidCancel(
        picker: UIImagePickerController
    ) {
        callback?.invoke(null)
        picker.dismissViewControllerAnimated(true, null)
    }
}

@Composable
actual fun rememberPlatformImagePicker(): ImagePicker {
    return remember { IOSImagePicker() }
}