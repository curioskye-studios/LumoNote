package com.ckestudios.lumonote.utils.textformatting

import android.graphics.Typeface
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.EditText
import com.ckestudios.lumonote.data.models.TextStyle

class BasicTextFormatter(override val editTextView: EditText) : RichTextFormatter<StyleSpan> {

    override lateinit var etvSpannableContent: Editable
    private var spanType: TextStyle? = null

    override fun updateSpannableContent() {

        etvSpannableContent = editTextView.text
    }

    fun setBasicSpanType(basicSpanType: TextStyle) {

        spanType = basicSpanType
    }

    override fun processFormatting(selectStart: Int, selectEnd: Int) {

        updateSpannableContent()

        val allSelectionStyleSpans =
            getSelectionSpans(selectStart, selectEnd)
        val desiredSpans = getDesiredSpans(allSelectionStyleSpans)

        if (allSelectionStyleSpans.isEmpty() || desiredSpans.isNullOrEmpty()) {

            applyFormatting(selectStart, selectEnd)
        } else {

            removeFormatting(selectStart, selectEnd, desiredSpans)
        }

        normalizeFormatting()

        fixLineHeight() // Keep line spacing consistent
    }


    override fun getSelectionSpans(selectStart: Int, selectEnd: Int): Array<StyleSpan> {

        val allStyleSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                StyleSpan::class.java)
        
        val filteredSpans = allStyleSpans.filter {
            
            val start = etvSpannableContent.getSpanStart(it)
            val end = etvSpannableContent.getSpanEnd(it)
            start < selectEnd && end > selectStart
        }.toTypedArray()
        
        // Ensure only bold amd italic spans
        return filteredSpans.filter { it is StyleSpan && (it.style == Typeface.BOLD ||
                it.style == Typeface.ITALIC) }.toTypedArray()
    }

    private fun getDesiredSpans(allStyleSpans: Array<StyleSpan>)
        : Array<StyleSpan>? {

        return when (spanType) {
                TextStyle.BOLD ->
                    allStyleSpans.filter { it.style == Typeface.BOLD }.toTypedArray()
                TextStyle.ITALICS ->
                    allStyleSpans.filter { it.style == Typeface.ITALIC }.toTypedArray()
                else -> null
            }
    }


    override fun applyFormatting(start: Int, end: Int) {

        val setSpan = when (spanType) {
                TextStyle.BOLD -> StyleSpan(Typeface.BOLD)
                TextStyle.ITALICS -> StyleSpan(Typeface.ITALIC)
                else -> null
            }

        if (setSpan != null) {

            etvSpannableContent.setSpan(
                setSpan,
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }


    override fun normalizeFormatting() {

        val newStyleSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                StyleSpan::class.java)

        val newDesiredSpans = getDesiredSpans(newStyleSpans)

        if (newDesiredSpans != null) {

            val sortedSpans = quickSortSpans(newDesiredSpans)

            // Combine adjacent or overlapping spans
            fixOverlappingSpans(sortedSpans)
        }
    }

    override fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int): Boolean? {
        return null
    }

    fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int,
                                         spanType: TextStyle): Boolean {

        updateSpannableContent()

        // Ensure safe use only
        if (selectStart == selectEnd || selectStart < 0 || selectEnd < 0 ||
            selectStart > selectEnd || selectEnd > etvSpannableContent.length) {

            return false
        }


        val spanFoundTracker = mutableListOf<Boolean>()

        val formatTypeToCheck = when (spanType) {
                TextStyle.BOLD -> Typeface.BOLD
                TextStyle.ITALICS -> Typeface.ITALIC
                else -> null
            }


        val selectedText =
            SpannableString(etvSpannableContent.subSequence(selectStart, selectEnd))


        val spans =
            etvSpannableContent.getSpans(selectStart, selectEnd, StyleSpan::class.java)

        val desiredSpans =
            spans.filter { it is StyleSpan && it.style == formatTypeToCheck}

        if (desiredSpans.isEmpty()) return false


        // Go through each character and check for the desired span
        for (charIndex in selectedText.indices) {

            for (span in desiredSpans) {

                val spanRange =
                    selectedText.getSpanStart(span)..selectedText.getSpanEnd(span)

                if (charIndex in spanRange) {

                    spanFoundTracker.add(true)
                }
                else {
                    spanFoundTracker.add(false)
                }
            }
        }

        return spanFoundTracker.all { it } && spanFoundTracker.isNotEmpty()
    }

}