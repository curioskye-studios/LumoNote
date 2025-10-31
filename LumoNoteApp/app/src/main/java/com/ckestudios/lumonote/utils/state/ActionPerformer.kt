package com.ckestudios.lumonote.utils.state

import android.graphics.Bitmap
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.EditText
import com.ckestudios.lumonote.data.models.BulletType
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.ui.noteview.other.ChecklistSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomImageSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import com.ckestudios.lumonote.utils.textformatting.UnderlineTextFormatter

class ActionPerformer {

    fun addStyleSpan(spanType: SpanType, spanStart: Int, spanEnd: Int,
                             editTextView: EditText){

        val setSpan: Any? = when (spanType) {

            SpanType.BOLD_SPAN -> StyleSpan(Typeface.BOLD)

            SpanType.ITALICS_SPAN -> StyleSpan(Typeface.ITALIC)

            SpanType.UNDERLINE_SPAN -> UnderlineTextFormatter.CustomUnderlineSpan()

            SpanType.BULLET_SPAN -> {
                removeStyleSpan(spanType, spanStart, spanEnd, editTextView)
                CustomBulletSpan(30, 6f, BulletType.DEFAULT, null)
            }

            SpanType.IMAGE_SPAN -> null

            SpanType.CHECKLIST_SPAN -> ChecklistSpan(editTextView.context)
        }


        if (setSpan != null) {

            editTextView.text.setSpan(
                setSpan,
                spanStart,
                spanEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    fun removeStyleSpan(spanType: SpanType, targetSpanStart: Int, targetSpanEnd: Int,
                                editTextView: EditText){

        val spanList = getDesiredStyleSpans(spanType, editTextView)

        if (spanList != null) {

            for (span in spanList) {

                val spanStart = editTextView.text.getSpanStart(span)

                val spanEnd = editTextView.text.getSpanEnd(span)


                if ((spanStart == targetSpanStart && spanEnd == targetSpanEnd) ||
                    (spanType == SpanType.CHECKLIST_SPAN && spanEnd == targetSpanEnd)) {

                    editTextView.text.removeSpan(span)
                }
            }
        }

    }


    fun addImageSpan(spanStart: Int, spanEnd: Int, editTextView: EditText,
                             imageBitmap: Bitmap){

        val imageSpan = CustomImageSpan(imageBitmap)
        val objectCharacter = '\uFFFC'

        val imageText = SpannableStringBuilder("$objectCharacter ")

        // Apply the CustomImageSpan to the object character
        imageText.setSpan(
            imageSpan,
            0,
            1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        editTextView.text.replace(spanStart, spanEnd, imageText)

        (editTextView as CustomSelectionET).triggerSelectionChanged()
    }


    fun addCustomBullet(spanStart: Int, spanEnd: Int, editTextView: EditText,
                                customBullet: String){

        editTextView.text.setSpan(
            CustomBulletSpan(30, 6f, BulletType.CUSTOM, customBullet),
            spanStart,
            spanEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }



    private fun getDesiredStyleSpans(spanType: SpanType, editTextView: EditText): Array<out Any>? {

        val spanClass = when(spanType) {

            SpanType.BOLD_SPAN, SpanType.ITALICS_SPAN  -> StyleSpan::class.java

            SpanType.UNDERLINE_SPAN -> UnderlineTextFormatter.CustomUnderlineSpan::class.java

            SpanType.BULLET_SPAN -> CustomBulletSpan::class.java

            SpanType.IMAGE_SPAN -> CustomImageSpan::class.java

            SpanType.CHECKLIST_SPAN -> ChecklistSpan::class.java
        }


        val allSpans =
            editTextView.text.getSpans(0, editTextView.text.length, spanClass)

        return when (spanType) {

            SpanType.BOLD_SPAN -> {
                val styleSpans = allSpans as Array<StyleSpan>
                styleSpans.filter { it.style == Typeface.BOLD }.toTypedArray()
            }

            SpanType.ITALICS_SPAN -> {
                val styleSpans = allSpans as Array<StyleSpan>
                styleSpans.filter { it.style == Typeface.ITALIC }.toTypedArray()
            }

            else -> allSpans
        }
    }

}