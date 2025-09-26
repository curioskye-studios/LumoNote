package com.ckestudios.lumonote.utils.textformatting

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.*
import android.widget.EditText
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.ui.noteview.other.CustomImageSpan
import com.ckestudios.lumonote.ui.noteview.other.ImageLineTextWatcher
import com.ckestudios.lumonote.utils.state.StateManager

class SimpleImageFormatter(private val editTextView: EditText) {

    // Special invisible character (used by Android to represent embedded objects like images)
    private val objectCharacter = '\uFFFC'

    private var etvSpannableContent: Editable = editTextView.text

    private val textFormatHelper = TextFormatHelper()
    private val stateManager = StateManager(editTextView)

    init {

        updateSpannableContent()

        editTextView.addTextChangedListener(ImageLineTextWatcher(editTextView))
    }


    private fun updateSpannableContent() {

        etvSpannableContent = editTextView.text
    }

    fun processFormatting(imageUri: Uri) {

        updateSpannableContent()

        val (lineStart, lineEnd) = textFormatHelper.getCurrentLineIndices(editTextView)

        // Remove existing image in this line
        removeImageInRange(lineStart, lineEnd)

        // Insert the new image
        insertImage(imageUri)

        textFormatHelper.fixLineHeight(editTextView)
    }


    private fun insertImage(imageUri: Uri) {

        val context = editTextView.context

        val cursorPos = editTextView.selectionStart.coerceAtLeast(0)

        val bitmap: Bitmap =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                val source =
                    ImageDecoder.createSource(context.contentResolver, imageUri)

                ImageDecoder.decodeBitmap(source)
            } else {

                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            }


        val adjustedBitmap = keepBitmapInMaxBounds(bitmap)

        val imageSpan = CustomImageSpan(adjustedBitmap)


        val imageText = SpannableStringBuilder("\n$objectCharacter\n")


        // Apply the CustomImageSpan to the object character
        imageText.setSpan(imageSpan, 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        stateManager.addSpan(imageSpan, SpanType.IMAGE_SPAN)

        etvSpannableContent.insert(cursorPos, imageText)
    }


    private fun removeImageInRange(start: Int, end: Int) {

        val imageSpans =
            etvSpannableContent.getSpans(start, end, CustomImageSpan::class.java)

        for (span in imageSpans) {
            
            stateManager.removeSpan(span, SpanType.IMAGE_SPAN)

            etvSpannableContent.removeSpan(span)
        }


    }


    private fun keepBitmapInMaxBounds(bitmap: Bitmap): Bitmap {

        val maxWidth = 800
        val maxHeight = 700

        if (bitmap.width <= maxWidth && bitmap.height <= maxHeight) {

            return bitmap // Already in bounds
        }

        // Figure out how much we need to shrink to fit within the bounds
        val widthScale = maxWidth.toFloat() / bitmap.width
        val heightScale = maxHeight.toFloat() / bitmap.height

        // Pick the smaller scale so both dimensions fit
        val scale = minOf(widthScale, heightScale)

        val newWidth = (bitmap.width * scale).toInt()
        val newHeight = (bitmap.height * scale).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
