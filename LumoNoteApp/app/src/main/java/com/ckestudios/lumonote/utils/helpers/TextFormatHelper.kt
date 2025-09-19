package com.ckestudios.lumonote.utils.helpers

import android.text.Editable
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.utils.textformatting.UnderlineTextFormatter

class TextFormatHelper {

    fun getSelectionParagraphIndices(selectStart: Int, selectEnd: Int, editTextViewEnd: Int,
                 etvContentString: String) : MutableList<Int>{

        val paragraphIndices = mutableListOf<Int>()

        val firstAndLastIndices = getFirstAndLastIndices(selectStart, selectEnd,
            editTextViewEnd, etvContentString)

        val firstNewLineIndex = firstAndLastIndices.first
        val lastNewLineIndex = firstAndLastIndices.second

        val selectionAsString =
            etvContentString.subSequence(firstNewLineIndex, lastNewLineIndex).toString()
        Log.d("bullettextformatter", "selection: " +
                selectionAsString.replace("\n", "\\n"))


        // Create a Regex object and index offset
        val regex = Regex("\n")
        val offsetToMatchOrgIndex: Int = firstNewLineIndex

        // Add offset to all occurrence indices between first and last
        val matchesIndices = regex.findAll(selectionAsString).map {
            it.range.first + offsetToMatchOrgIndex
        }.toList()


        // Populate indices
        paragraphIndices.add(firstNewLineIndex)
        for (index in matchesIndices) {

            if (index != firstNewLineIndex && index != lastNewLineIndex) {

                paragraphIndices.add(index)
            }
        }
        paragraphIndices.add(lastNewLineIndex)


        // Print the extracted occurrences
        paragraphIndices.forEach { Log.d("bullettextformatter", "occurrence: $it") }

        return paragraphIndices
    }


    private fun getFirstAndLastIndices(selectStart: Int, selectEnd: Int, editTextViewEnd: Int,
               etvContentString: String): Pair<Int, Int> {

        // Subtract 1 so the search starts before the inserted "\n", not after the
        // cursor jump on pressing Enter
        val newLinePosBeforeSelection = etvContentString.lastIndexOf("\n",
            selectStart - 1)
        val newLinePosAfterSelection = etvContentString.indexOf("\n",
            selectEnd)

        val skipNewLineSpace = 1

        // Exclude newline char itself to indicate current line
        val firstIndex =
            if (newLinePosBeforeSelection != -1)  newLinePosBeforeSelection + skipNewLineSpace
            else 0
        val lastIndex =
            if (newLinePosAfterSelection != -1) newLinePosAfterSelection
            else editTextViewEnd

        return Pair(firstIndex, lastIndex)
    }


    fun fixLineHeight(editTextView: EditText) {

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



    fun sortSpans(spans: Array<out Any>, etvSpannableContent: Editable): Array<Any> {

        return spans.sortedBy { etvSpannableContent.getSpanStart(it) }.toTypedArray()
    }


    fun fixOverlappingSpans(sortedSpans: Array<Any>, etvSpannableContent: Editable,
                            applyFormattingFunction: (Int, Int) -> Unit) {

        for (spanIndex in sortedSpans.indices) {

            val currentSpanStart =
                etvSpannableContent.getSpanStart(sortedSpans[spanIndex])
            val currentSpanEnd =
                etvSpannableContent.getSpanEnd(sortedSpans[spanIndex])

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

                    applyFormattingFunction(prevSpanStart, currentSpanEnd)

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

                    applyFormattingFunction(currentSpanStart, nextSpanEnd)

                    etvSpannableContent.removeSpan(sortedSpans[spanIndex])
                    etvSpannableContent.removeSpan(sortedSpans[nextSpanIndex])
                }
            }

            // remove empty spans
            removeIfEmptySpan(currentSpanEnd, currentSpanStart, sortedSpans[spanIndex],
                etvSpannableContent)
        }
    }

    private fun removeIfEmptySpan(currentSpanEnd: Int, currentSpanStart: Int,
                                  currentSpan: Any, etvSpannableContent: Editable) {

        if (currentSpanEnd == currentSpanStart) {

            etvSpannableContent.removeSpan(currentSpan)
        }
    }

    fun clearBasicFormatting(selectStart: Int, selectEnd: Int, etvSpannableContent: Editable) {

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


    fun checkIfCurrentLineHasText(editTextView: EditText) : Boolean{

        val cursorPos = editTextView.selectionStart

        val text = editTextView.text.toString()

        // Find start of the line (after previous '\n' or 0)
        val lineStart = text.lastIndexOf('\n', cursorPos - 1).let {
            if (it == -1) 0 else it + 1 }

        // Find end of the line (next '\n' or end of text)
        val lineEnd = text.indexOf('\n', cursorPos).let {
            if (it == -1) text.length else it }

        val lineContent = text.substring(lineStart, lineEnd)

        return lineContent.trim().isEmpty()
    }

}