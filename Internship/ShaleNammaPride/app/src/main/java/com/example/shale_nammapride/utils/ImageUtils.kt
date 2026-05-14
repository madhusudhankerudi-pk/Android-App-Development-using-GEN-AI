package com.example.shale_nammapride.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtils {
    fun compressImage(context: Context, imageUri: Uri): ByteArray {
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        
        val outputStream = ByteArrayOutputStream()
        // Compress to 70% quality to save space while maintaining quality
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return outputStream.toByteArray()
    }
}