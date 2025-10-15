package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.utils.state.SpanStateWatcher
import com.ckestudios.lumonote.utils.state.StateManager

class UnderlineTextFormatter(
    override val editTextView: EditText, private val stateManager: StateManager)
    : RichTextFormatter<UnderlineTextFormatter.CustomUnderlineSpan> {

    class CustomUnderlineSpan : UnderlineSpan()

    override lateinit var etvSpannableContent: Editable
    private val textFormatHelper = TextFormatHelper()
    private val spanStateWatcher = SpanStateWatcher(editTextView, stateManager)


    override fun updateSpannableContent() {

        etvSpannableContent = editTextView.text

        removeUnintendedUnderlines()
    }


    override fun processFormatting(selectStart: Int, selectEnd: Int) {

        updateSpannableContent()

        val allSelectionSpans =
            getSelectionSpans(selectStart, selectEnd)

        if (allSelectionSpans.isEmpty()) {

            applyFormatting(selectStart, selectEnd)
        } else {

            removeFormatting(selectStart, selectEnd, allSelectionSpans)
        }

        normalizeFormatting()

        removeUnintendedUnderlines()
    }


    override fun getSelectionSpans(selectStart: Int, selectEnd: Int): Array<CustomUnderlineSpan> {

        val allUnderlineSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                CustomUnderlineSpan::class.java)

        // Ensure only within range
        return allUnderlineSpans.filter {

            val start = etvSpannableContent.getSpanStart(it)
            val end = etvSpannableContent.getSpanEnd(it)
            start < selectEnd && end > selectStart
        }.toTypedArray()
    }


    override fun applyFormatting(start: Int, end: Int) {

        val span = CustomUnderlineSpan()

        etvSpannableContent.setSpan(
            span,
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spanStateWatcher.addSpan(span, SpanType.UNDERLINE_SPAN)
    }

    override fun removeFormatting(selectStart: Int, selectEnd: Int,
                                  spansList: Array<CustomUnderlineSpan>){

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

            spanStateWatcher.removeSpan(span, SpanType.UNDERLINE_SPAN)

            etvSpannableContent.removeSpan(span)
        }
    }


    override fun normalizeFormatting() {

        val newUnderlineSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                CustomUnderlineSpan::class.java)

        if (newUnderlineSpans != null) {

            val sortedSpans = textFormatHelper.sortSpans(newUnderlineSpans,
                etvSpannableContent)

            // Combine adjacent or overlapping spans
            textFormatHelper.fixOverlappingSpans(sortedSpans, etvSpannableContent,
                spanStateWatcher, SpanType.UNDERLINE_SPAN, ::applyFormatting)
        }
    }


    override fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int): Boolean {

        updateSpannableContent()

        if (selectStart == selectEnd || selectStart < 0 || selectEnd < 0 ||
            selectStart > selectEnd || selectEnd > etvSpannableContent.length)
            return false


        val spanFoundTracker = mutableListOf<Boolean>()

        val selectedText =
            SpannableString(etvSpannableContent.subSequence(selectStart, selectEnd))

        val underlineSpans =
            etvSpannableContent.getSpans(selectStart, selectEnd, CustomUnderlineSpan::class.java)

        Log.d("underlineSpans", underlineSpans.contentToString())


        if (underlineSpans.isEmpty()) return false


        // Go through each character and check for the desired span
        for (charIndex in selectedText.indices) {

            for (span in underlineSpans) {

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

    private fun removeUnintendedUnderlines() {

        val allUnderlines =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                UnderlineSpan::class.java)

        for (span in allUnderlines) {

            if (span !is CustomUnderlineSpan) {

                etvSpannableContent.removeSpan(span)
            }
        }
    }

}