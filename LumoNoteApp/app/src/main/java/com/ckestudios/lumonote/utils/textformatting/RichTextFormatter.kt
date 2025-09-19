package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.widget.EditText

interface RichTextFormatter<T> {

    val editTextView: EditText
    var etvSpannableContent: Editable

    fun updateSpannableContent()

    fun processFormatting(selectStart: Int, selectEnd: Int)

    fun getSelectionSpans(selectStart: Int, selectEnd: Int): Array<T>

    fun applyFormatting(start: Int, end: Int)

    fun removeFormatting(selectStart: Int, selectEnd: Int, spansList: Array<T>){

        for (span in spansList) {

            val spanStart = etvSpannableContent.getSpanStart(span)
            val spanEnd = etvSpannableContent.getSpanEnd(span)

            // eg. span: 0-7, selection: 4-9
            if (spanStart < selectStart) {

                val excludeRemovalStart = spanStart
                val excludeRemovalEnd = selectStart
                applyFormatting(excludeRemovalStart, excludeRemovalEnd)
            }

            // eg. span: 5-8, selection 2-6
            if (spanEnd > selectEnd) {

                val excludeRemovalStart = selectEnd
                val excludeRemovalEnd = spanEnd
                applyFormatting(excludeRemovalStart, excludeRemovalEnd)
            }

            // eg. span: 1-9, selection 3-6, runs both

            etvSpannableContent.removeSpan(span)
        }
    }

    fun normalizeFormatting()

    // Check characters in a given range and return whether they all have the desired span
    fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int): Boolean?


}