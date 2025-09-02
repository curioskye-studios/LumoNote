package com.curioskyestudios.lumonote.utils.texthelper

import android.text.Editable
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.widget.EditText
import com.curioskyestudios.lumonote.data.models.TextSize

class TextSizeHelper(private val editTextView: EditText) {

    private val textStyleHelper: TextStyleHelper = TextStyleHelper(editTextView)

    class CustomRelativeSizeSpan(proportion: Float): RelativeSizeSpan(proportion)

    fun formatAsHeader(type: TextSize) {

        val stringBuilder: Editable? = editTextView.text
        var setSpan: CharacterStyle? = null

        val noteContentEnd = stringBuilder?.length ?: 0 // if text is null, set as 0
        val cursorIndex = editTextView.selectionStart

        // Subtract 1 from the cursor index because upon pressing Enter, the newline ("\n")
        // is inserted at the cursor, then the cursor moves after it. This ensures the search
        // start position is before the newly created \n and doesn't include it
        val newLinePrevIndex = stringBuilder.toString().lastIndexOf("\n", cursorIndex - 1)
        val newLineNextIndex = stringBuilder.toString().indexOf("\n", cursorIndex)

        val selectionStart = if (newLinePrevIndex != -1)  newLinePrevIndex + 1 else 0
        val selectionEnd = if (newLineNextIndex != -1) newLineNextIndex else noteContentEnd

        //Log.d("SelectionNewLine", "$newLinePrevIndex")
        Log.d("SelectionStart", "$selectionStart")
        Log.d("SelectionEnd", "$selectionEnd")


        val relativeSizeSpans = editTextView.text?.getSpans(selectionStart,
            selectionEnd, RelativeSizeSpan::class.java)


        Log.d("Header", "Point 1")

        when (type) {
            TextSize.NORMAL -> {
                // check if heading size has already been applied
                if (!relativeSizeSpans.isNullOrEmpty()) {
                    // if so, remove it
                    for (span in relativeSizeSpans) {
                        stringBuilder?.removeSpan(span)
                    }
                }
            }

            TextSize.H1 -> setSpan = toggleHeader(relativeSizeSpans, TextSize.H1.scaleFactor)

            TextSize.H2 -> setSpan = toggleHeader(relativeSizeSpans, TextSize.H2.scaleFactor)
        }


        stringBuilder?.setSpan(
            setSpan,
            selectionStart,
            selectionEnd,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )

        textStyleHelper.removeUnintendedUnderlines(stringBuilder)

        editTextView.text = stringBuilder
        editTextView.setSelection(cursorIndex)


    }



    private fun toggleHeader(spanList: Array<RelativeSizeSpan>?, scaleFactor: Float)
        : CharacterStyle? {

        val stringBuilder: Editable? = editTextView.text

        Log.d("relativeSizeSpan", spanList.contentToString())

        if (!spanList.isNullOrEmpty()) {
            for (span in spanList) {
                Log.d("Relative Spans", "Span class: ${span::class.java.name}")
            }
        } else {
            Log.d("Relative Spans", "None")
        }



        // check if heading size has already been applied
        if (spanList.isNullOrEmpty()) {
            // If not, add it
            return RelativeSizeSpan(scaleFactor)
        }
        else if (spanList.isNotEmpty()) {
            // If different header was applied, replace it
            for (span in spanList) {
                stringBuilder?.removeSpan(span)
            }
            return RelativeSizeSpan(scaleFactor)
        }
        else {
            // if so, remove it (toggle)
            for (span in spanList) {
                stringBuilder?.removeSpan(span)
            }
            return null
        }

    }


}