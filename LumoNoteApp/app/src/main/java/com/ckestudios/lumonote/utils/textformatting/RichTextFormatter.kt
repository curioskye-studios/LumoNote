package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.util.Log
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

    fun fixLineHeight() {

        val oldSelectionStart = editTextView.selectionStart
        val oldSelectionEnd = editTextView.selectionEnd

        editTextView.setLineSpacing(0f, 1f)

        // Re-assign to trigger span re-evaluation
        editTextView.text = editTextView.text

        editTextView.invalidate()
        editTextView.requestLayout()

        editTextView.setLineSpacing(0f, 1.3f)

        // Restore selection
        editTextView.setSelection(oldSelectionStart, oldSelectionEnd)
    }

    fun quickSortSpans(spans: Array<T>): Array<T> {

        if (spans.size < 2) return spans

        val pivot = spans[0]
        val pivotSpanStart = etvSpannableContent.getSpanStart(spans[0])
        val pivotSpanEnd = etvSpannableContent.getSpanEnd(spans[0])

        val less =
            spans.drop(1).filter {
                etvSpannableContent.getSpanStart(it) <= pivotSpanStart &&
                        etvSpannableContent.getSpanEnd(it) <= pivotSpanEnd
            } as Array<T>

        val greater =
            spans.drop(1).filter {
                etvSpannableContent.getSpanStart(it) > pivotSpanStart &&
                        etvSpannableContent.getSpanEnd(it) > pivotSpanEnd
            } as Array<T>

        return quickSortSpans(less) + pivot + quickSortSpans(greater)
    }

    fun fixOverlappingSpans(sortedSpans: Array<T>) {

        for (spanIndex in sortedSpans.indices) {

            val currentSpanStart = etvSpannableContent.getSpanStart(sortedSpans[spanIndex])
            val currentSpanEnd = etvSpannableContent.getSpanEnd(sortedSpans[spanIndex])

            val previousSpanIndex =
                if (spanIndex > 0) spanIndex - 1 else null
            val nextSpanIndex =
                if (spanIndex < sortedSpans.size - 1) spanIndex + 1 else null


            if (previousSpanIndex != null) {

                val prevSpanStart =
                    etvSpannableContent.getSpanStart(sortedSpans[previousSpanIndex])
                val prevSpanEnd =
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

                val nextSpanStart =
                    etvSpannableContent.getSpanStart(sortedSpans[nextSpanIndex])
                val nextSpanEnd =
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

    fun removeIfEmptySpan(currentSpanEnd: Int, currentSpanStart: Int,
                          currentSpan: T) {

        if (currentSpanEnd == currentSpanStart) {

            etvSpannableContent.removeSpan(currentSpan)
        }
    }


    fun clearFormatting(selectStart: Int, selectEnd: Int) {

        val allStyleSpans =
            etvSpannableContent.getSpans(selectStart, selectEnd,
                StyleSpan::class.java)
        val allUnderlineSpans =
            etvSpannableContent.getSpans(selectStart, selectEnd,
                UnderlineTextFormatter.CustomUnderlineSpan::class.java)

        val list = mutableListOf<CharacterStyle>()
        list.addAll(allStyleSpans)
        list.addAll(allUnderlineSpans)

        val allSpans = list.toTypedArray()

        if (allSpans.isNotEmpty()) {

            for (span in allSpans) {
                etvSpannableContent.removeSpan(span)
            }
        }
    }

}