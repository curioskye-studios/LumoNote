package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.utils.helpers.TextFormatHelper

class UnderlineTextFormatter(override val editTextView: EditText)
    : RichTextFormatter<UnderlineTextFormatter.CustomUnderlineSpan> {

    class CustomUnderlineSpan : UnderlineSpan()

    override lateinit var etvSpannableContent: Editable
    private val textFormatHelper = TextFormatHelper()


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

        etvSpannableContent.setSpan(
            CustomUnderlineSpan(),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
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
                ::applyFormatting)
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