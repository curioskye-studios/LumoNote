package com.ckestudios.lumonote.utils.state

import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.data.models.Action
import com.ckestudios.lumonote.data.models.ActionPerformed
import com.ckestudios.lumonote.data.models.ActionType
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.ui.noteview.other.ChecklistSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomImageSpan
import com.ckestudios.lumonote.utils.textformatting.UnderlineTextFormatter

class SpanStateWatcher(private val editTextView: EditText,
                       private val stateManager: StateManager) {

    private var currentBoldSpans = ArrayList<StyleSpan>()
    private var currentItalicsSpans = ArrayList<StyleSpan>()
    private var currentUnderlineSpans = ArrayList<UnderlineTextFormatter.CustomUnderlineSpan>()

    private var currentBulletSpans = ArrayList<CustomBulletSpan>()

    private var currentImageSpans = ArrayList<CustomImageSpan>()

    private var currChecklistSpans = ArrayList<ChecklistSpan>()

    private var currH1SizeSpans = ArrayList<RelativeSizeSpan>()
    private var currH2SizeSpans = ArrayList<RelativeSizeSpan>()


    fun addStyleSpan(span: Any, spanType: SpanType, isNormalization: Boolean,
                     multipartIdentifier: String?) {

        when (spanType) {

            SpanType.BOLD_SPAN -> currentBoldSpans.add(span as StyleSpan)
            SpanType.ITALICS_SPAN -> currentItalicsSpans.add(span as StyleSpan)
            SpanType.UNDERLINE_SPAN -> {
                currentUnderlineSpans.add((span as UnderlineTextFormatter.CustomUnderlineSpan))
            }

            SpanType.BULLET_SPAN -> currentBulletSpans.add(span as CustomBulletSpan)

            SpanType.IMAGE_SPAN -> currentImageSpans.add(span as CustomImageSpan)

            SpanType.CHECKLIST_SPAN -> currChecklistSpans.add(span as ChecklistSpan)
        }

        val spanStart = editTextView.text.getSpanStart(span)
        val spanEnd = editTextView.text.getSpanEnd(span)

        val action = Action(
            ActionPerformed.ADD,
            ActionType.SPAN,
            isNormalization,
            multipartIdentifier,
            spanStart,
            spanEnd,
            spanType
        )

        stateManager.addToUndo(action)

        Log.d("SpanWatcher", "${spanType.spanName} added to $spanStart-$spanEnd")
    }

    fun <T> removeStyleSpan(targetSpan: T, spanType: SpanType, isNormalization: Boolean,
                            multipartIdentifier: String?) {

        val spanList =
            when (spanType) {

                SpanType.BOLD_SPAN -> currentBoldSpans
                SpanType.ITALICS_SPAN -> currentItalicsSpans
                SpanType.UNDERLINE_SPAN -> currentUnderlineSpans

                SpanType.BULLET_SPAN -> currentBulletSpans

                SpanType.IMAGE_SPAN -> currentImageSpans

                SpanType.CHECKLIST_SPAN -> currChecklistSpans
            }

        if (spanList.isEmpty()) return


        val spansToRemove = mutableListOf<Any>() // Avoid ConcurrentModificationException

        for (span in spanList) {

            if (span == targetSpan) {

                val spanStart = editTextView.text.getSpanStart(span)
                val spanEnd = editTextView.text.getSpanEnd(span)

                val action = Action(
                    ActionPerformed.REMOVE,
                    ActionType.SPAN,
                    isNormalization,
                    multipartIdentifier,
                    spanStart,
                    spanEnd,
                    spanType
                )

                stateManager.addToUndo(action)

                spansToRemove.add(span)

                Log.d("SpanWatcher", "${spanType.spanName} removed from $spanStart-$spanEnd")
            }
        }

        spanList.removeAll(spansToRemove.toSet())
    }




}