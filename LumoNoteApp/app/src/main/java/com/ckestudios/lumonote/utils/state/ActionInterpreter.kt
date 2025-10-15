package com.ckestudios.lumonote.utils.state

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.EditText
import com.ckestudios.lumonote.data.models.Action
import com.ckestudios.lumonote.data.models.ActionPerformed
import com.ckestudios.lumonote.data.models.BulletType
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletSpan
import com.ckestudios.lumonote.utils.textformatting.UnderlineTextFormatter

class ActionInterpreter(private val textStateWatcher: TextStateWatcher) {

    fun processTextAction(action: Action, editTextView: EditText, shouldUndoAction: Boolean){

        // Details e.g.: text - ""

        if (shouldUndoAction) {

            when (action.actionPerformed) {

                ActionPerformed.ADD -> performTextAction(ActionPerformed.REMOVE, action,
                    editTextView)

                ActionPerformed.REMOVE -> performTextAction(ActionPerformed.ADD, action,
                    editTextView)
            }
        } else {

            performTextAction(action.actionPerformed, action, editTextView)
        }
    }

    private fun performTextAction(actionPerformed: ActionPerformed, action: Action,
                                  editTextView: EditText) {

        textStateWatcher.setMakingInternalEdits(true)

        when (actionPerformed) {

            ActionPerformed.ADD -> {

                editTextView.text.insert(action.actionStart, action.actionInfo.toString())
            }

            ActionPerformed.REMOVE -> editTextView.text.delete(action.actionStart,
                action.actionEnd)
        }


        if (editTextView.selectionStart != editTextView.selectionEnd) {

            editTextView.setSelection(action.actionStart, action.actionEnd)
        } else {

            editTextView.setSelection(action.actionEnd)
        }

        textStateWatcher.setMakingInternalEdits(false)
    }


    fun processSpanAction(action: Action, editTextView: EditText, shouldUndoAction: Boolean){

        // Details e.g.: spantype: spanType.SPAN

        if (shouldUndoAction) {

            when (action.actionPerformed) {

                ActionPerformed.ADD -> performSpanAction(ActionPerformed.REMOVE, action,
                    editTextView)

                ActionPerformed.REMOVE -> performSpanAction(ActionPerformed.ADD, action,
                    editTextView)
            }
        } else {

            performSpanAction(action.actionPerformed, action, editTextView)
        }

    }

    private fun performSpanAction(actionPerformed: ActionPerformed, action: Action,
                                  editTextView: EditText) {

        val spanType = action.actionInfo as SpanType

        //undo normalization too

        textStateWatcher.setMakingInternalEdits(true)


        when (actionPerformed) {

            ActionPerformed.ADD -> {

                addSpanType(spanType, action.actionStart, action.actionEnd, editTextView)
            }

            ActionPerformed.REMOVE ->
                removeSpanType(spanType, action.actionStart, action.actionEnd, editTextView)
        }


        if (editTextView.selectionStart != editTextView.selectionEnd) {

            editTextView.setSelection(action.actionStart, action.actionEnd)
        } else {

            editTextView.setSelection(action.actionEnd)
        }

        textStateWatcher.setMakingInternalEdits(false)
    }

    private fun addSpanType(spanType: SpanType, spanStart: Int, spanEnd: Int,
                            editTextView: EditText){


        val setSpan = when (spanType) {

            SpanType.BOLD_SPAN -> StyleSpan(Typeface.BOLD)

            SpanType.ITALICS_SPAN -> StyleSpan(Typeface.ITALIC)

            SpanType.UNDERLINE_SPAN -> UnderlineTextFormatter.CustomUnderlineSpan()

            SpanType.BULLET_SPAN ->
                CustomBulletSpan(30, 6f, BulletType.DEFAULT, null)

            SpanType.IMAGE_SPAN -> null

            SpanType.CHECKLIST_SPAN -> null

            else -> null
        }

        if (setSpan != null) {

            editTextView.text.setSpan(
                setSpan,
                spanStart,
                spanEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }


    private fun removeSpanType(spanType: SpanType, targetSpanStart: Int, targetSpanEnd: Int,
                               editTextView: EditText){

        val spanList = getDesiredSpans(spanType, editTextView)

        if (spanList != null) {

            for (span in spanList) {

                val spanStart = editTextView.text.getSpanStart(span)
                val spanEnd = editTextView.text.getSpanEnd(span)

                if (spanStart == targetSpanStart && spanEnd == targetSpanEnd) {

                    editTextView.text.removeSpan(span)
                }
            }
        }

    }

    private fun getDesiredSpans(spanType: SpanType, editTextView: EditText): Array<out Any>? {

        val spanClass = when(spanType) {

            SpanType.BOLD_SPAN, SpanType.ITALICS_SPAN  -> StyleSpan::class.java

            SpanType.UNDERLINE_SPAN -> UnderlineTextFormatter.CustomUnderlineSpan::class.java

            SpanType.BULLET_SPAN -> CustomBulletSpan::class.java

            SpanType.IMAGE_SPAN -> null

            SpanType.CHECKLIST_SPAN -> null

            else -> null
        }


        val allSpans =
            editTextView.text.getSpans(0, editTextView.text.length, spanClass)

        return when (spanType) {

            SpanType.BOLD_SPAN -> {
                val styleSpans = allSpans as Array<StyleSpan>
                styleSpans.filter { it.style == Typeface.BOLD }.toTypedArray()
            }

            SpanType.ITALICS_SPAN -> {
                val styleSpans = allSpans as Array<StyleSpan>
                styleSpans.filter { it.style == Typeface.ITALIC }.toTypedArray()
            }

            SpanType.UNDERLINE_SPAN -> allSpans

            SpanType.BULLET_SPAN -> allSpans

            SpanType.IMAGE_SPAN -> null

            SpanType.CHECKLIST_SPAN -> null

            else -> null
        }
    }

}