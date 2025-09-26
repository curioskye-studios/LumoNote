package com.ckestudios.lumonote.utils.state

import android.text.style.StyleSpan
import android.widget.EditText
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.ui.noteview.other.ChecklistSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomImageSpan
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.textformatting.TextFormatHelper
import com.ckestudios.lumonote.utils.textformatting.UnderlineTextFormatter

class StateManager(private val editTextView: EditText) {

    private val undoStack = UndoStateStack()
    private val redoStack = RedoStateStack()

    private var currentBoldSpans = ArrayList<StyleSpan>()
    private var currentItalicsSpans = ArrayList<StyleSpan>()
    private var currentUnderlineSpans = ArrayList<UnderlineTextFormatter.CustomUnderlineSpan>()

    private var currentBulletSpans = ArrayList<CustomBulletSpan>()

    private var currentImageSpans = ArrayList<CustomImageSpan>()

    private var currChecklistSpans = ArrayList<ChecklistSpan>()

    private val textFormatHelper = TextFormatHelper()
    private val generalUIHelper = GeneralUIHelper()

    fun addSpan(span: Any, spanType: SpanType) {

        when (spanType) {

            SpanType.BOLD_SPAN -> currentBoldSpans.add(span as StyleSpan)
            SpanType.ITALICS_SPAN -> currentItalicsSpans.add(span as StyleSpan)
            SpanType.UNDERLINE_SPAN -> {
                currentUnderlineSpans.add((span as UnderlineTextFormatter.CustomUnderlineSpan))
            }

            SpanType.BULLET_SPAN -> currentBulletSpans.add(span as CustomBulletSpan)

            SpanType.IMAGE_SPAN -> currentImageSpans.add(span as CustomImageSpan)

            SpanType.CHECKLIST_SPAN -> currChecklistSpans.add(span as ChecklistSpan)

            else -> {}
        }

        val spanStart = editTextView.text.getSpanStart(span)
        val spanEnd = editTextView.text.getSpanEnd(span)

        generalUIHelper.displayFeedbackToast(editTextView.context,
            "${spanType.spanName} added at $spanStart-$spanEnd", true)
    }

    fun removeSpan(targetSpan: Any, spanType: SpanType) {

        val spanList =
            when (spanType) {

                SpanType.BOLD_SPAN -> currentBoldSpans
                SpanType.ITALICS_SPAN -> currentItalicsSpans
                SpanType.UNDERLINE_SPAN -> currentUnderlineSpans

                SpanType.BULLET_SPAN -> currentBulletSpans

                SpanType.IMAGE_SPAN -> currentImageSpans

                SpanType.CHECKLIST_SPAN -> currChecklistSpans

                else -> null
            }

        if (spanList.isNullOrEmpty()) return

        for (span in spanList) {

            if (span == targetSpan) {

                val spanStart = editTextView.text.getSpanStart(span)
                val spanEnd = editTextView.text.getSpanEnd(span)

                generalUIHelper.displayFeedbackToast(editTextView.context,
                    "${spanType.spanName} removed from $spanStart-$spanEnd",
                    true)

                spanList.remove(span)
            }
        }
    }

    fun addToUndo() {

    }


}