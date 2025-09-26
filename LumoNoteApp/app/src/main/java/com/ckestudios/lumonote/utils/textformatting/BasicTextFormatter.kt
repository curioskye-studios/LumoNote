package com.ckestudios.lumonote.utils.textformatting

import android.graphics.Typeface
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.EditText
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.utils.state.StateManager

class BasicTextFormatter(override val editTextView: EditText) : RichTextFormatter<StyleSpan> {

    override lateinit var etvSpannableContent: Editable
    private var spanType: SpanType? = null
    private val textFormatHelper = TextFormatHelper()
    private val stateManager = StateManager(editTextView)

    override fun updateSpannableContent() {

        etvSpannableContent = editTextView.text
    }

    fun setBasicSpanType(basicSpanType: SpanType, selectStart: Int, selectEnd: Int) {

        spanType = basicSpanType

        processFormatting(selectStart, selectEnd)
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

        textFormatHelper.fixLineHeight(editTextView) // Keep line spacing consistent
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
                SpanType.BOLD_SPAN ->
                    allStyleSpans.filter { it.style == Typeface.BOLD }.toTypedArray()
                SpanType.ITALICS_SPAN ->
                    allStyleSpans.filter { it.style == Typeface.ITALIC }.toTypedArray()
                else -> null
            }
    }


    override fun applyFormatting(start: Int, end: Int) {

        val setSpan = when (spanType) {
                SpanType.BOLD_SPAN -> StyleSpan(Typeface.BOLD)
                SpanType.ITALICS_SPAN -> StyleSpan(Typeface.ITALIC)
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

        stateManager.addSpan(setSpan!!, spanType!!)
    }

    override fun removeFormatting(selectStart: Int, selectEnd: Int, spansList: Array<StyleSpan>){

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

            stateManager.removeSpan(span, spanType!!)

            etvSpannableContent.removeSpan(span)

        }
    }


    override fun normalizeFormatting() {

        val newStyleSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                StyleSpan::class.java)

        val newDesiredSpans = getDesiredSpans(newStyleSpans)

        if (newDesiredSpans != null) {

            val sortedSpans = textFormatHelper.sortSpans(newDesiredSpans,
                etvSpannableContent)

            // Combine adjacent or overlapping spans
            textFormatHelper.fixOverlappingSpans(sortedSpans, etvSpannableContent,
                stateManager, spanType!!, ::applyFormatting)
        }
    }

    override fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int): Boolean? {
        return null
    }

    fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int,
                                         spanType: SpanType): Boolean {

        updateSpannableContent()

        // Ensure safe use only
        if (selectStart == selectEnd || selectStart < 0 || selectEnd < 0 ||
            selectStart > selectEnd || selectEnd > etvSpannableContent.length) {

            return false
        }


        val spanFoundTracker = mutableListOf<Boolean>()

        val formatTypeToCheck = when (spanType) {
                SpanType.BOLD_SPAN -> Typeface.BOLD
                SpanType.ITALICS_SPAN -> Typeface.ITALIC
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