package com.ckestudios.lumonote.utils.textformatting

import android.graphics.Typeface
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.data.models.TextStyle

class BasicTextFormatter(override val editTextView: EditText) : RichTextFormatter {

    fun applyFormatting(selectStart: Int, selectEnd: Int, spanType: TextStyle) {

        val setSpan =
            when (spanType) {

                TextStyle.BOLD -> StyleSpan(Typeface.BOLD)
                TextStyle.ITALICS -> StyleSpan(Typeface.ITALIC)
                else -> {}
            }

        editTextView.text.setSpan(
            setSpan,
            selectStart,
            selectEnd,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )

        val output = isSelectionFullySpanned(selectStart, selectEnd, spanType)

        Log.d("basicTextFormatter", "Fully Spanned: $output")
    }

    override fun normalizeFormatting() {

        TODO("Not yet implemented")
    }


    fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int, spanType: TextStyle): Boolean {

        //check characters in a given range and return whether they all have the desired span

        val spanFoundTracker = mutableListOf<Boolean>()
        val editableString: Editable? = editTextView.text


        val formatTypeToCheck =
            when (spanType) {

                TextStyle.BOLD -> Typeface.BOLD
                TextStyle.ITALICS -> Typeface.ITALIC
                else -> {}
            }


        if (editableString != null && selectStart != selectEnd) {

            val selectedText = SpannableString(editableString.subSequence(selectStart, selectEnd))

            val spans =
                editableString.getSpans(selectStart, selectEnd, StyleSpan::class.java)
            Log.d("basicTextFormatter", "spans: ${spans.contentToString()}")

            val desiredSpans =
                spans.filter { it is StyleSpan && it.style == formatTypeToCheck}
            Log.d("basicTextFormatter", "desiredSpans: $desiredSpans")

            if (desiredSpans.isNotEmpty()) {

                // go through each character and check for the desired span

                for (charIndex in selectedText.indices) {

                    //check if char at charIndex falls in the range of a span and flag as having the span

                    for (span in desiredSpans) {

                        val spanRange =
                            selectedText.getSpanStart(span)..selectedText.getSpanEnd(span)

                        if (charIndex in spanRange) {

                            spanFoundTracker.add(true)
                            Log.d("basicTextFormatter",
                                "'${selectedText[charIndex]}' at $charIndex has $formatTypeToCheck.")
                        }
                        else {
                            spanFoundTracker.add(false)
                            Log.d("basicTextFormatter",
                                "'${selectedText[charIndex]}' at $charIndex has $formatTypeToCheck.")
                        }

                    }
                }

            }
        }

        return spanFoundTracker.all { it } && spanFoundTracker.isNotEmpty()
    }

}