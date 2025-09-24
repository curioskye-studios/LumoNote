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

class SimpleChecklistFormatter(private val editTextView: EditText) {

    private lateinit var etvSpannableContent: Editable
    private var shouldRemoveChecklist = false
    private val textFormatHelper = TextFormatHelper()

    private fun updateSpannableContent() {

        etvSpannableContent = editTextView.text
    }

    fun updateRemoveChecklist(shouldRemove: Boolean) {

        shouldRemoveChecklist = shouldRemove
    }

    fun processFormatting() {

        updateSpannableContent()

        val paragraphIndices = textFormatHelper.getSelectionParagraphIndices(editTextView)

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

        // Remove only checklist-related spans
        val spans =
            etvSpannableContent.getSpans(start, end, CharacterStyle::class.java)
            .filter { it is StrikethroughSpan || it is CustomItalicsSpan ||
                    it is ForegroundColorSpan }

        spans.forEach { etvSpannableContent.removeSpan(it) }

        etvSpannableContent.replace(start, end, newLine)
    }

    private fun applyFormatting(start: Int, end: Int) {

        updateSpannableContent()

        val line = etvSpannableContent.substring(start, end).trimStart()
        val newLine: String

        when {

            line.startsWith("☐") -> {

                newLine = line.replaceFirst("☐", "☑")
                etvSpannableContent.replace(start, end, newLine)

                setCheckedSpans(start, start + newLine.length)
            }
            line.startsWith("☑") -> {

                newLine = line.replaceFirst("☑", "☐")
                etvSpannableContent.replace(start, end, newLine)

                removeFormatting(start, end)
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

        val checkedColor =
            ContextCompat.getColor(editTextView.context, R.color.light_grey_2)

        etvSpannableContent.setSpan(
            ForegroundColorSpan(checkedColor),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    fun getCheckedState(selectStart: Int): String? {

        updateSpannableContent()

        val (lineStart, lineEnd) = textFormatHelper.getCurrentLineIndices(editTextView)

        val line =  etvSpannableContent.substring(lineStart, lineEnd).trimStart()

        val hasChecklist = checkCurrentLineHasChecklist(selectStart)

        return when {
            line.startsWith("☐") && hasChecklist -> "☐"
            line.startsWith("☑") && hasChecklist -> "☑"
            else -> null
        }
    }

    fun checkCurrentLineHasChecklist(selectStart: Int): Boolean {

        updateSpannableContent()

        val (lineStart, lineEnd) = textFormatHelper.getCurrentLineIndices(editTextView)

        val line =  etvSpannableContent.substring(lineStart, lineEnd).trimStart()

        return line.startsWith("☐") || line.startsWith("☑")
    }
}
