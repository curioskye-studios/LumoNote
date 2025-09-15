package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.EditText

class UnderlineTextFormatter(override val editTextView: EditText) : RichTextFormatter {

    class CustomUnderlineSpan : UnderlineSpan()

    private lateinit var etvSpannableContent: Editable

    fun processFormatting(selectStart: Int, selectEnd: Int) {

        etvSpannableContent = editTextView.text

        val allSelectionSpans =
            getSelectionSpans(selectStart, selectEnd)

        if (allSelectionSpans.isEmpty()) {

            applyFormatting(selectStart, selectEnd)
        } else {

            removeFormatting(selectStart, selectEnd, allSelectionSpans)
        }

        normalizeFormatting()

        isSelectionFullySpanned(selectStart, selectEnd)
    }


    private fun getSelectionSpans(selectStart: Int, selectEnd: Int): Array<CustomUnderlineSpan> {

        val allStyleSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                CustomUnderlineSpan::class.java)

        // Ensure only bold amd italic spans
        return allStyleSpans.filter {

            val start = etvSpannableContent.getSpanStart(it)
            val end = etvSpannableContent.getSpanEnd(it)
            start < selectEnd && end > selectStart
        }.toTypedArray()
    }


    private fun applyFormatting(applyStart: Int, applyEnd: Int) {

        etvSpannableContent.setSpan(
            CustomUnderlineSpan(),
            applyStart,
            applyEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun removeFormatting(selectStart: Int, selectEnd: Int,
                                 spansList: Array<CustomUnderlineSpan>?) {

        if (spansList != null) {

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
    }


    private fun normalizeFormatting() {

        val newUnderlineSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                CustomUnderlineSpan::class.java)

        if (newUnderlineSpans != null) {

            val sortedSpans = quickSortSpans(newUnderlineSpans)

            Log.d("basicTextFormatter", "styleSpans: ${sortedSpans.contentToString()}")

            // Combine adjacent or overlapping spans
            fixOverlappingSpans(sortedSpans)
        }
    }


    private fun quickSortSpans(styleSpans: Array<CustomUnderlineSpan>): Array<CustomUnderlineSpan> {

        if (styleSpans.size < 2) return styleSpans

        val pivot = styleSpans[0]
        val pivotSpanStart = etvSpannableContent.getSpanStart(styleSpans[0])
        val pivotSpanEnd = etvSpannableContent.getSpanEnd(styleSpans[0])

        val less =
            styleSpans.drop(1).filter {
                etvSpannableContent.getSpanStart(it) <= pivotSpanStart &&
                        etvSpannableContent.getSpanEnd(it) <= pivotSpanEnd
            }.toTypedArray()

        val greater =
            styleSpans.drop(1).filter {
                etvSpannableContent.getSpanStart(it) > pivotSpanStart &&
                        etvSpannableContent.getSpanEnd(it) > pivotSpanEnd
            }.toTypedArray()

        return quickSortSpans(less) + pivot + quickSortSpans(greater)
    }



    private fun fixOverlappingSpans(sortedSpans: Array<CustomUnderlineSpan>) {

        for (spanIndex in sortedSpans.indices) {

            val currentSpanStart = etvSpannableContent.getSpanStart(sortedSpans[spanIndex])
            val currentSpanEnd = etvSpannableContent.getSpanEnd(sortedSpans[spanIndex])

            val previousSpanIndex =
                if (spanIndex > 0) spanIndex - 1 else null
            val nextSpanIndex =
                if (spanIndex < sortedSpans.size - 1) spanIndex + 1 else null


            if (previousSpanIndex != null) {

                var prevSpanStart =
                    etvSpannableContent.getSpanStart(sortedSpans[previousSpanIndex])
                var prevSpanEnd =
                    etvSpannableContent.getSpanEnd(sortedSpans[previousSpanIndex])

                if (prevSpanEnd >= currentSpanStart && (prevSpanStart != -1 ||
                            prevSpanEnd != -1)) {

                    Log.d("basicTextFormatter", "Previous spanIndex overlaps.")

                    applyFormatting(prevSpanStart, currentSpanEnd)

                    etvSpannableContent.removeSpan(sortedSpans[previousSpanIndex])
                    etvSpannableContent.removeSpan(sortedSpans[spanIndex])
                }
            }

            if (nextSpanIndex != null) {

                var nextSpanStart =
                    etvSpannableContent.getSpanStart(sortedSpans[nextSpanIndex])
                var nextSpanEnd =
                    etvSpannableContent.getSpanEnd(sortedSpans[nextSpanIndex])

                if (nextSpanStart <= currentSpanEnd && (nextSpanStart != -1 ||
                            nextSpanEnd != -1)) {

                    Log.d("basicTextFormatter", "Next spanIndex overlaps.")

                    applyFormatting(currentSpanStart, nextSpanEnd)

                    etvSpannableContent.removeSpan(sortedSpans[spanIndex])
                    etvSpannableContent.removeSpan(sortedSpans[nextSpanIndex])
                }
            }

            // remove empty spans
            removeIfEmptySpan(currentSpanEnd, currentSpanStart, sortedSpans[spanIndex])
        }
    }


    private fun removeIfEmptySpan(currentSpanEnd: Int, currentSpanStart: Int,
                                  currentSpan: CustomUnderlineSpan) {

        if (currentSpanEnd == currentSpanStart) {

            etvSpannableContent.removeSpan(currentSpan)
        }
    }


    // Check characters in a given range and return whether they all have the desired span
    private fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int): Boolean {

        etvSpannableContent = editTextView.text

        val spanFoundTracker = mutableListOf<Boolean>()

        if (etvSpannableContent != null && selectStart != selectEnd) {

            val selectedText = SpannableString(etvSpannableContent.subSequence(selectStart, selectEnd))

            val spans =
                etvSpannableContent.getSpans(selectStart, selectEnd, CustomUnderlineSpan::class.java)
            Log.d("basicTextFormatter", "spans: ${spans.contentToString()}")


            if (spans.isNotEmpty()) {

                // go through each character and check for the desired span

                for (charIndex in selectedText.indices) {

                    // check if char at charIndex falls in the range of a span
                    // and flag as having the span

                    for (span in spans) {

                        val spanRange =
                            selectedText.getSpanStart(span)..selectedText.getSpanEnd(span)

                        if (charIndex in spanRange) {

                            spanFoundTracker.add(true)
                            Log.d("basicTextFormatter",
                                "'${selectedText[charIndex]}' at $charIndex " +
                                        "has underlines.")
                        }
                        else {
                            spanFoundTracker.add(false)
                            Log.d("basicTextFormatter",
                                "'${selectedText[charIndex]}' at $charIndex " +
                                        "has underlines.")
                        }

                    }
                }

            }
        }

        Log.d("basicTextFormatter",
            "isFullySpanned: " + (spanFoundTracker.all { it } && spanFoundTracker.isNotEmpty()))
        return spanFoundTracker.all { it } && spanFoundTracker.isNotEmpty()
    }


    fun checkIfFullyUnderlined(selectStart: Int, selectEnd: Int) : Boolean {

        return isSelectionFullySpanned(selectStart, selectEnd)
    }
    
}