package com.ckestudios.lumonote.utils.basichelpers

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object GeneralImageHelper {

    fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, fileName: String): String {

        val file = File(context.filesDir, "$fileName.jpg")
        val outputStream = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()

        return file.absolutePath // Return the path to save in DB
    }

    fun deleteImageFromInternalStorage(context: Context, fileName: String): Boolean {
        val file = File(context.filesDir, "$fileName.jpg")
        return if (file.exists()) {
            file.delete()
        } else {
            false // File not found
        }
    }

}