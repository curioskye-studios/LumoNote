package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.Spannable
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.ui.noteview.other.CustomItalicsSpan
import com.ckestudios.lumonote.utils.helpers.TextFormatHelper

class SimpleChecklistFormatter(private val editTextView: EditText) {  // no span type needed

    private lateinit var etvSpannableContent: Editable
    private var shouldRemoveChecklist = false

    private val textFormatHelper = TextFormatHelper()

    private fun updateSpannableContent() {

        etvSpannableContent = editTextView.text
    }

    fun updateRemoveChecklist(shouldRemove: Boolean) {

        shouldRemoveChecklist = shouldRemove
    }

    fun processFormatting(selectStart: Int, selectEnd: Int) {

        updateSpannableContent()

        val paragraphIndices =
            textFormatHelper.getSelectionParagraphIndices(selectStart, selectEnd,
                etvSpannableContent.length, etvSpannableContent.toString())

        for (i in 0 until paragraphIndices.size - 1) {

            val paraStart = paragraphIndices[i]
            val paraEnd = paragraphIndices[i + 1]

            if (shouldRemoveChecklist) {

                removeFormatting(paraStart, paraEnd)
            } else {

                applyFormatting(paraStart, paraEnd)
            }
        }

        if (shouldRemoveChecklist) shouldRemoveChecklist = false
    }

    private fun removeFormatting(start: Int, end: Int) {

        val line = etvSpannableContent.substring(start, end).trimStart()

        val newLine = when {

            line.startsWith("☐") -> line.replaceFirst("☐  ", "")

            line.startsWith("☑") -> line.replaceFirst("☑  ", "")

            else -> line
        }

        val spans = etvSpannableContent.getSpans(
            start, start + newLine.length,
            CharacterStyle::class.java
        ).filter {
            it is StrikethroughSpan || it is CustomItalicsSpan ||
                    it is ForegroundColorSpan
        }

        for (span in spans) {

            etvSpannableContent.removeSpan(span)
        }

        etvSpannableContent.replace(start, end, newLine)
    }

    private fun applyFormatting(start: Int, end: Int) {

        updateSpannableContent()

        val line = etvSpannableContent.substring(start, end).trimStart()
        var newLine: String

        when {

            line.startsWith("☐") -> {

                newLine = line.replaceFirst("☐", "☑")
                etvSpannableContent.replace(start, end, newLine)

                // apply strikethrough AFTER replacement
                val textStart = start + 3 // skip "☑ "
                val textEnd = start + newLine.length

                setCheckedSpans(textStart, textEnd)
            }

            line.startsWith("☑") -> {

                newLine = line.replaceFirst("☑", "☐")
                etvSpannableContent.replace(start, end, newLine)

                // remove strikethrough AFTER replacement
                val spans = etvSpannableContent.getSpans(
                    start, start + newLine.length,
                    CharacterStyle::class.java
                ).filter {
                    it is StrikethroughSpan || it is CustomItalicsSpan ||
                            it is ForegroundColorSpan
                }

                for (span in spans) {

                    etvSpannableContent.removeSpan(span)
                }
            }

            else -> {

                newLine = "☐  $line"
                etvSpannableContent.replace(start, end, newLine)
            }
        }
    }

    private fun setCheckedSpans(start: Int, end: Int) {

        etvSpannableContent.setSpan(
            StrikethroughSpan(),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        etvSpannableContent.setSpan(
            CustomItalicsSpan(),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val checkedColor = ContextCompat.getColor(editTextView.context,
            R.color.light_grey_2)

        etvSpannableContent.setSpan(
            ForegroundColorSpan(checkedColor),
            start - 3,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }


    fun checkCurrentLineHasChecklist(selectStart: Int): Boolean {

        updateSpannableContent()


        val newLineBeforeCursor =
            etvSpannableContent.lastIndexOf('\n', selectStart - 1)
        val newLineAfterCursor =
            etvSpannableContent.indexOf('\n', selectStart)

        val lineStart =
            if (newLineBeforeCursor == -1) 0
            else newLineBeforeCursor + 1

        val lineEnd =
            if (newLineAfterCursor == -1) etvSpannableContent.length
            else newLineAfterCursor

        val line = etvSpannableContent.substring(lineStart, lineEnd).trimStart()

        return line.startsWith("☐") || line.startsWith("☑")
    }
}
