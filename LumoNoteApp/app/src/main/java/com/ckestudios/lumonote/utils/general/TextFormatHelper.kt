package com.ckestudios.lumonote.utils.general

import android.util.Log

class TextFormatHelper {

    fun getSelectionParagraphIndices(selectStart: Int, selectEnd: Int, editTextViewEnd: Int,
                                     etvContentString: String) : MutableList<Int>{

        val paragraphIndices = mutableListOf<Int>()

        val firstAndLastIndices = getFirstAndLastIndices(selectStart, selectEnd,
            editTextViewEnd, etvContentString)

        val firstNewLineIndex = firstAndLastIndices.first
        val lastNewLineIndex = firstAndLastIndices.second

        // Exclude first newline
        val selectionAsString =
            etvContentString.subSequence(firstNewLineIndex + 1, lastNewLineIndex).toString()
//        Log.d("sizetextformatter", selectionAsString.replace("\n", "\\n"))


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

            paragraphIndices.add(index)
        }
        paragraphIndices.add(lastNewLineIndex)


        // Print the extracted occurrences
        paragraphIndices.forEach { Log.d("sizetextformatter", "occurrence: $it") }

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

        val firstIndex =
            if (newLinePosBeforeSelection != -1)  newLinePosBeforeSelection
            else 0
        val lastIndex =
            if (newLinePosAfterSelection != -1) newLinePosAfterSelection
            else editTextViewEnd

        return Pair(firstIndex, lastIndex)
    }
}