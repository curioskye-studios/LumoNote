package com.ckestudios.lumonote.utils.edittexthelper

import android.text.Editable
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.CharacterStyle
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.widget.EditText


class TextBulletHelper(private val editTextView: EditText) {

    fun formatBullet() {

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

        stringBuilder?.setSpan(

            BulletSpan(30),
            selectionStart,
            selectionEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        editTextView.text = stringBuilder
        editTextView.setSelection(cursorIndex)
    }



}