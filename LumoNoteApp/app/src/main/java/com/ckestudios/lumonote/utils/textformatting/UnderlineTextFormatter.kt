package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.widget.EditText

class UnderlineTextFormatter(override val editTextView: EditText)
    : RichTextFormatter<UnderlineTextFormatter.CustomUnderlineSpan> {

    class CustomUnderlineSpan : UnderlineSpan()

    override lateinit var etvSpannableContent: Editable


    override fun updateSpannableContent() {

        etvSpannableContent = editTextView.text
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

        val allStyleSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                CustomUnderlineSpan::class.java)

        // Ensure only within range
        return allStyleSpans.filter {

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

            val sortedSpans = quickSortSpans(newUnderlineSpans)

            // Combine adjacent or overlapping spans
            fixOverlappingSpans(sortedSpans)
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