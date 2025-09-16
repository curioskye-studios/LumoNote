package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.widget.EditText
import com.ckestudios.lumonote.data.models.TextSize

class SizeTextFormatter(override val editTextView: EditText)
    : RichTextFormatter<RelativeSizeSpan> {

    override lateinit var etvSpannableContent: Editable
    private var sizeType: TextSize? = null


    fun setSizeSpanType(sizeSpanType: TextSize) {

        sizeType = sizeSpanType
    }


    override fun processFormatting(selectStart: Int, selectEnd: Int) {

        etvSpannableContent = editTextView.text

        val relativeSizeSpans =
            getSelectionSpans(selectStart, selectEnd)

        if (relativeSizeSpans.isEmpty()) {

            applyFormatting(selectStart, selectEnd)
        } else {

            removeFormatting(selectStart, selectEnd, relativeSizeSpans)
        }
    }

    override fun getSelectionSpans(selectStart: Int, selectEnd: Int)
        : Array<RelativeSizeSpan> {

        val relativeSizeSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                RelativeSizeSpan::class.java)

        // Ensure only current paragraph
        return relativeSizeSpans.filter {

            val start = etvSpannableContent.getSpanStart(it)
            val end = etvSpannableContent.getSpanEnd(it)
            start < selectEnd && end > selectStart
        }.toTypedArray()
    }


    override fun applyFormatting(start: Int, end: Int) {

        val etvContentString = etvSpannableContent.toString()

        val noteContentEnd = etvSpannableContent.length
        val cursorIndex = start

        // Subtract 1 from the cursor index because upon pressing Enter, the newline ("\n")
        // is inserted at the cursor, then the cursor moves after it. This ensures the search
        // start position is before the newly created \n and doesn't include it
        val newLinePosBeforeCursor = etvContentString.lastIndexOf("\n",
            cursorIndex - 1)
        val newLinePosAfterCursor = etvContentString.indexOf("\n",
            cursorIndex)

        val applyStart =
            if (newLinePosBeforeCursor != -1)  newLinePosBeforeCursor + 1
            else 0
        val applyEnd =
            if (newLinePosAfterCursor != -1) newLinePosAfterCursor
            else noteContentEnd

//        Log.d("sizetextformatter", "SelectionStart: $applyStart")
//        Log.d("sizetextformatter", "SelectionEnd: $applyEnd")

        var relativeSpan= when (sizeType) {
            TextSize.H1 -> RelativeSizeSpan(TextSize.H1.scaleFactor)
            TextSize.H2 -> RelativeSizeSpan(TextSize.H2.scaleFactor)
            else -> null
        }

//        Log.d("sizetextformatter", "relativeSpan: $relativeSpan")

        if (relativeSpan != null) {

            etvSpannableContent.setSpan(
                relativeSpan,
                applyStart,
                applyEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    }

    override fun removeFormatting(selectStart: Int, selectEnd: Int,
        spansList: Array<RelativeSizeSpan>) {

        for (span in spansList) {

            etvSpannableContent.removeSpan(span)
        }

        if (sizeType == TextSize.H1 || sizeType == TextSize.H2) {

            applyFormatting(selectStart, selectEnd)
        }
    }


    override fun normalizeFormatting() {}

    override fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int): Boolean {

        return getSelectionSpans(selectStart, selectEnd).isNotEmpty()
    }
}