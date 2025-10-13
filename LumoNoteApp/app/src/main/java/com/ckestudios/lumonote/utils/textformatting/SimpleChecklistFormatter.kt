package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.Spannable
import android.widget.EditText
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.ui.noteview.other.ChecklistSpan
import com.ckestudios.lumonote.utils.state.SpanStateWatcher

class SimpleChecklistFormatter(private val editTextView: EditText) {

    private lateinit var etvSpannableContent: Editable
    private var shouldRemoveChecklist = false
    private val textFormatHelper = TextFormatHelper()
    private val spanStateWatcher = SpanStateWatcher(editTextView)

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



    private fun applyFormatting(start: Int, end: Int) {

        updateSpannableContent()

        val line = etvSpannableContent.substring(start, end).trimStart()
        val newLine: String

        when {

            line.startsWith("☐") -> {

                newLine = line.replaceFirst("☐", "☑")
                etvSpannableContent.replace(start, end, newLine)

                val span = ChecklistSpan(editTextView.context)

                etvSpannableContent.setSpan(
                    span,
                    start,
                    start + newLine.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                spanStateWatcher.addSpan(span, SpanType.CHECKLIST_SPAN)
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

    private fun removeFormatting(start: Int, end: Int) {

        val line = etvSpannableContent.substring(start, end).trimStart()

        val newLine = when {

            line.startsWith("☐") -> line.replaceFirst("☐  ", "")

            line.startsWith("☑") -> line.replaceFirst("☑  ", "")

            else -> line
        }

        // Remove only checklist-related spans
        val spans =
            etvSpannableContent.getSpans(start, end, ChecklistSpan::class.java)

        spans.forEach {

            spanStateWatcher.removeSpan(it, SpanType.CHECKLIST_SPAN)

            etvSpannableContent.removeSpan(it)
        }

        etvSpannableContent.replace(start, end, newLine)
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
