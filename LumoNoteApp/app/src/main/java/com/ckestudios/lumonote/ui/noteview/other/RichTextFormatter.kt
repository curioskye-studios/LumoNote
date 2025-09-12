package com.ckestudios.lumonote.ui.noteview.other

import android.graphics.Typeface
import android.text.Editable
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.EditText
import com.ckestudios.lumonote.data.models.TextStyle

class RichTextFormatter(private val editTextView: EditText) {

    class CustomUnderlineSpan : UnderlineSpan()

    fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int, spanType: TextStyle) {

        //check characters in a given range and return whether they all have the desired span

        val editableString: Editable? = editTextView.text
        val spanClassToCheck =
            when (spanType) {

                TextStyle.BOLD, TextStyle.ITALICS -> StyleSpan::class.java
                TextStyle.UNDERLINE -> CustomUnderlineSpan::class.java
            }

        if (editableString != null && selectStart != selectEnd) {

            //val selectedText = SpannableString(editableString.subSequence(selectStart, selectEnd))

            val spans =
                editableString.getSpans(selectStart, selectEnd, spanClassToCheck)

            //go through each character and check for the desired span



//            for (index in selectedText.indices) {
//                val character = selectedText[index]
//                val desiredSpan =
//                    editableString.getSpans(index, index, spanClassToCheck)
//
//                Log.d("RichTextFormatter", "$character: ${desiredSpan.contentToString()}")
//            }

        }







    }

    fun applyRichText(selectStart: Int, selectEnd: Int) {


        editTextView.text.setSpan(
            StyleSpan(Typeface.BOLD),
            selectStart,
            selectEnd,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )

        isSelectionFullySpanned(selectStart, selectEnd, TextStyle.BOLD)
    }

    fun removeBold(selectStart: Int, selectEnd: Int) {


    }
}