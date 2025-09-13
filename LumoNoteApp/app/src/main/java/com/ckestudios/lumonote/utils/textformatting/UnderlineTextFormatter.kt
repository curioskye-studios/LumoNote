package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.EditText

class UnderlineTextFormatter(override val editTextView: EditText) : RichTextFormatter {

    class CustomUnderlineSpan : UnderlineSpan()

    fun applyFormatting(selectStart: Int, selectEnd: Int) {

        editTextView.text.setSpan(
            CustomUnderlineSpan(),
            selectStart,
            selectEnd,
            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
        )

        val output = isSelectionFullyUnderlined(selectStart, selectEnd)

        Log.d("basicTextFormatter", "Fully Spanned: $output")
    }

    override fun normalizeFormatting() {

        TODO("Not yet implemented")
    }


    fun isSelectionFullyUnderlined(selectStart: Int, selectEnd: Int): Boolean {

        //check characters in a given range and return whether they all have the desired span

        val spanFoundTracker = mutableListOf<Boolean>()
        val editableString: Editable? = editTextView.text


        if (editableString != null && selectStart != selectEnd) {

            val selectedText = SpannableString(editableString.subSequence(selectStart, selectEnd))

            val spans =
                editableString.getSpans(selectStart, selectEnd, CustomUnderlineSpan::class.java)
            Log.d("basicTextFormatter", "spans: ${spans.contentToString()}")

            if (spans.isNotEmpty()) {

                // go through each character and check for the desired span

                for (charIndex in selectedText.indices) {

                    //check if char at charIndex falls in the range of a span and flag as having the span

                    for (span in spans) {

                        val spanRange =
                            selectedText.getSpanStart(span)..selectedText.getSpanEnd(span)

                        if (charIndex in spanRange) {

                            spanFoundTracker.add(true)
                            Log.d("basicTextFormatter",
                                "'${selectedText[charIndex]}' at $charIndex has underline.")
                        }
                        else {
                            spanFoundTracker.add(false)
                            Log.d("basicTextFormatter",
                                "'${selectedText[charIndex]}' at $charIndex has no underline.")
                        }

                    }
                }

            }
        }

        return spanFoundTracker.all { it } && spanFoundTracker.isNotEmpty()
    }

}