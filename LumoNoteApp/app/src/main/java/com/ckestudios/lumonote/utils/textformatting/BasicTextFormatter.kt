package com.ckestudios.lumonote.utils.textformatting

import android.graphics.Typeface
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.data.models.TextStyle

class BasicTextFormatter(override val editTextView: EditText) : RichTextFormatter {

    private lateinit var etvSpannableContent: Editable

    fun processFormatting(selectStart: Int, selectEnd: Int, spanType: TextStyle) {

        etvSpannableContent = editTextView.text

        val allSelectionStyleSpans = getSelectionStyleSpans(selectStart, selectEnd)
        val desiredSpans = getDesiredSpans(allSelectionStyleSpans, spanType)

        if (allSelectionStyleSpans.isEmpty() || desiredSpans.isNullOrEmpty()) {

            applyFormatting(selectStart, selectEnd, spanType)
        } else {

            removeFormatting(selectStart, selectEnd, spanType, desiredSpans)
        }

        normalizeFormatting(spanType)

        fixLineHeight() // Keep line spacing consistent

        isSelectionFullySpanned(selectStart, selectEnd, spanType)
    }


    private fun getSelectionStyleSpans(selectStart: Int, selectEnd: Int): Array<StyleSpan> {

        val allStyleSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length, StyleSpan::class.java)
        
        val filteredSpans = allStyleSpans.filter {
            
            val start = etvSpannableContent.getSpanStart(it)
            val end = etvSpannableContent.getSpanEnd(it)
            start < selectEnd && end > selectStart
        }.toTypedArray()
        
        // Ensure only bold amd italic spans
        return filteredSpans.filter { it is StyleSpan && (it.style == Typeface.BOLD ||
                it.style == Typeface.ITALIC) }.toTypedArray()
    }

    private fun getDesiredSpans(allStyleSpans: Array<StyleSpan>, spanType: TextStyle)
        : Array<StyleSpan>? {

        return when (spanType) {

                TextStyle.BOLD ->
                    allStyleSpans.filter { it.style == Typeface.BOLD }.toTypedArray()
                TextStyle.ITALICS ->
                    allStyleSpans.filter { it.style == Typeface.ITALIC }.toTypedArray()
                else -> null
            }
    }


    private fun applyFormatting(applyStart: Int, applyEnd: Int, spanType: TextStyle) {

        val setSpan =
            when (spanType) {

                TextStyle.BOLD -> StyleSpan(Typeface.BOLD)
                TextStyle.ITALICS -> StyleSpan(Typeface.ITALIC)
                else -> null
            }

        if (setSpan != null) {

            etvSpannableContent.setSpan(
                setSpan,
                applyStart,
                applyEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun removeFormatting(selectStart: Int, selectEnd: Int, spanType: TextStyle,
                                 spansList: Array<StyleSpan>?) {

        if (spansList != null) {

            for (span in spansList) {

                val spanStart = etvSpannableContent.getSpanStart(span)
                val spanEnd = etvSpannableContent.getSpanEnd(span)

                // eg. span: 0-7, selection: 4-9
                if (spanStart < selectStart) {

                    val excludeRemovalStart = spanStart
                    val excludeRemovalEnd = selectStart
                    applyFormatting(excludeRemovalStart, excludeRemovalEnd, spanType)
                }

                // eg. span: 5-8, selection 2-6
                if (spanEnd > selectEnd) {

                    val excludeRemovalStart = selectEnd
                    val excludeRemovalEnd = spanEnd
                    applyFormatting(excludeRemovalStart, excludeRemovalEnd, spanType)
                }

                // eg. span: 1-9, selection 3-6, runs both

                etvSpannableContent.removeSpan(span)
            }
        }
    }


    private fun normalizeFormatting(spanType: TextStyle) {

        val newStyleSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                StyleSpan::class.java)
        val newDesiredSpans = getDesiredSpans(newStyleSpans, spanType)

        if (newDesiredSpans != null) {

            val sortedStyleSpans = quickSortStyleSpans(newDesiredSpans)

            Log.d("basicTextFormatter", "styleSpans: ${sortedStyleSpans.contentToString()}")

            // Combine adjacent or overlapping spans
            fixOverlappingSpans(sortedStyleSpans, spanType)
        }
    }


    private fun quickSortStyleSpans(styleSpans: Array<StyleSpan>): Array<StyleSpan> {

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

        return quickSortStyleSpans(less) + pivot + quickSortStyleSpans(greater)
    }



    private fun fixOverlappingSpans(sortedStyleSpans: Array<StyleSpan>, spanType: TextStyle) {

        for (spanIndex in sortedStyleSpans.indices) {

            val currentSpanStart = etvSpannableContent.getSpanStart(sortedStyleSpans[spanIndex])
            val currentSpanEnd = etvSpannableContent.getSpanEnd(sortedStyleSpans[spanIndex])

            val previousSpanIndex =
                if (spanIndex > 0) spanIndex - 1 else null
            val nextSpanIndex =
                if (spanIndex < sortedStyleSpans.size - 1) spanIndex + 1 else null


            if (previousSpanIndex != null) {

                var prevSpanStart =
                    etvSpannableContent.getSpanStart(sortedStyleSpans[previousSpanIndex])
                var prevSpanEnd =
                    etvSpannableContent.getSpanEnd(sortedStyleSpans[previousSpanIndex])

                if (prevSpanEnd >= currentSpanStart && (prevSpanStart != -1 ||
                            prevSpanEnd != -1)) {

                    Log.d("basicTextFormatter", "Previous spanIndex overlaps.")

                    applyFormatting(prevSpanStart, currentSpanEnd, spanType)

                    etvSpannableContent.removeSpan(sortedStyleSpans[previousSpanIndex])
                    etvSpannableContent.removeSpan(sortedStyleSpans[spanIndex])
                }
            }

            if (nextSpanIndex != null) {

                var nextSpanStart =
                    etvSpannableContent.getSpanStart(sortedStyleSpans[nextSpanIndex])
                var nextSpanEnd =
                    etvSpannableContent.getSpanEnd(sortedStyleSpans[nextSpanIndex])

                if (nextSpanStart <= currentSpanEnd && (nextSpanStart != -1 ||
                            nextSpanEnd != -1)) {

                    Log.d("basicTextFormatter", "Next spanIndex overlaps.")

                    applyFormatting(currentSpanStart, nextSpanEnd, spanType)

                    etvSpannableContent.removeSpan(sortedStyleSpans[spanIndex])
                    etvSpannableContent.removeSpan(sortedStyleSpans[nextSpanIndex])
                }
            }

            // remove empty spans
            removeIfEmptySpan(currentSpanEnd, currentSpanStart, sortedStyleSpans[spanIndex])
        }
    }


    private fun removeIfEmptySpan(currentSpanEnd: Int, currentSpanStart: Int,
                                  currentSpan: StyleSpan) {

        if (currentSpanEnd == currentSpanStart) {

            etvSpannableContent.removeSpan(currentSpan)
        }
    }


    // Check characters in a given range and return whether they all have the desired span
    private fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int, spanType: TextStyle): Boolean {

        etvSpannableContent = editTextView.text

        val spanFoundTracker = mutableListOf<Boolean>()

        val formatTypeToCheck =
            when (spanType) {

                TextStyle.BOLD -> Typeface.BOLD
                TextStyle.ITALICS -> Typeface.ITALIC
                else -> null
            }

        if (etvSpannableContent != null && selectStart != selectEnd && spanType != null) {

            val selectedText = SpannableString(etvSpannableContent.subSequence(selectStart, selectEnd))

            val spans =
                etvSpannableContent.getSpans(selectStart, selectEnd, StyleSpan::class.java)
            Log.d("basicTextFormatter", "spans: ${spans.contentToString()}")

            val desiredSpans =
                spans.filter { it is StyleSpan && it.style == formatTypeToCheck}
            Log.d("basicTextFormatter", "desiredSpans: $desiredSpans")

            if (desiredSpans.isNotEmpty()) {

                // go through each character and check for the desired span

                for (charIndex in selectedText.indices) {

                    // check if char at charIndex falls in the range of a span
                    // and flag as having the span

                    for (span in desiredSpans) {

                        val spanRange =
                            selectedText.getSpanStart(span)..selectedText.getSpanEnd(span)

                        if (charIndex in spanRange) {

                            spanFoundTracker.add(true)
                            Log.d("basicTextFormatter",
                                "'${selectedText[charIndex]}' at $charIndex " +
                                        "has ${spanType.name}")
                        }
                        else {
                            spanFoundTracker.add(false)
                            Log.d("basicTextFormatter",
                                "'${selectedText[charIndex]}' at $charIndex " +
                                        "has ${spanType.name}")
                        }

                    }
                }

            }
        }

        Log.d("basicTextFormatter",
            "isFullySpanned: " + (spanFoundTracker.all { it } && spanFoundTracker.isNotEmpty()))
        return spanFoundTracker.all { it } && spanFoundTracker.isNotEmpty()
    }


    fun checkIfFullyBold(selectStart: Int, selectEnd: Int) : Boolean {

        return isSelectionFullySpanned(selectStart, selectEnd, TextStyle.BOLD)
    }

    fun checkIfFullyItalics(selectStart: Int, selectEnd: Int) : Boolean {

        return isSelectionFullySpanned(selectStart, selectEnd, TextStyle.ITALICS)
    }


    private fun fixLineHeight() {

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


}