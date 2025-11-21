package com.ckestudios.lumonote.utils.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.utils.state.SpanProcessor
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object GeneralImageHelper {


    fun saveImageToInternalStorage(context: Context, bitmap: Bitmap): String? {

        return try {

            // Avoid illegal characters like ":" from LocalDateTime
            val safeTime = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))

            val fullFileName = "${safeTime}_LumoNote.jpg"
            val file = File(context.filesDir, fullFileName)

            FileOutputStream(file).use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
            }

            file.absolutePath

        } catch (e: Exception) {
            Log.e("SaveSpans", "Failed to save image: ${e.message}")
            null
        }
    }


    fun loadImageFromInternalStorage(path: String): Bitmap? {

        return try {
            BitmapFactory.decodeFile(path)
        } catch (e: Exception) {
            Log.e("SaveSpans", "Failed to load image at $path: ${e.message}")
            null
        }
    }


    fun deleteImageFromInternalStorage(filePath: String): Boolean {

        return try {
            val file = File(filePath)

            if (file.exists()) file.delete() else false

        } catch (e: Exception) {
            Log.e("SaveSpans", "Failed to delete image: ${e.message}")
            false
        }
    }

    fun removeUnusedImageFiles(notes: List<Note>, context: Context) {

        val allFilePaths = getAllSavedImages(context)

        val usedFilePaths = extractNoteImageFilePaths(notes)

        for (filePath in allFilePaths) {

            if (filePath !in usedFilePaths) deleteImageFromInternalStorage(filePath)
        }
    }


    private fun getAllSavedImages(context: Context): List<String> {

        val dir = context.filesDir

        return dir.listFiles()
            ?.filter { file ->
                file.isFile && file.name.endsWith("_LumoNote.jpg")
            }
            ?.map { it.absolutePath }
            ?: emptyList()
    }

    private fun extractNoteImageFilePaths(notes: List<Note>): List<String> {

        val filePathList = mutableListOf<String>()

        for (note in notes) {

            val noteSpanString = note.noteSpans
            if (noteSpanString.isBlank()) continue

            val spanRecordList = SpanProcessor.convertSpanDataToList(noteSpanString)

            for (spanRecord in spanRecordList) {

                val spanRecordDict =
                    SpanProcessor.getSpanRecordInfoPairs(spanRecord).toMap()

                if (spanRecordDict.containsKey("path")) {

                    val path = spanRecordDict["path"].toString()
                    if (path != "null")  filePathList.add(path)
                }
            }

        }

        return filePathList
    }

}
