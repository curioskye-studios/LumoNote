package com.ckestudios.lumonote.utils.state

import android.graphics.Bitmap
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.EditText
import com.ckestudios.lumonote.data.models.Action
import com.ckestudios.lumonote.data.models.ActionPerformed
import com.ckestudios.lumonote.data.models.BulletType
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.ui.noteview.other.ChecklistSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomImageSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
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

        editTextView.setSelection(action.actionEnd)

        textStateWatcher.setMakingInternalEdits(false)
    }



    fun processStyleSpanAction(action: Action, editTextView: EditText, shouldUndoAction: Boolean){

        // Details e.g.: spantype: spanType.SPAN

        if (shouldUndoAction) {

            when (action.actionPerformed) {

                ActionPerformed.ADD -> performStyleSpanAction(ActionPerformed.REMOVE, action,
                    editTextView)

                ActionPerformed.REMOVE -> performStyleSpanAction(ActionPerformed.ADD, action,
                    editTextView)
            }
        } else {

            performStyleSpanAction(action.actionPerformed, action, editTextView)
        }
    }

    private fun performStyleSpanAction(actionPerformed: ActionPerformed, action: Action,
                                       editTextView: EditText) {

        val spanType = action.actionInfo as SpanType


        textStateWatcher.setMakingInternalEdits(true)

        when (actionPerformed) {

            ActionPerformed.ADD -> {

                addStyleSpan(spanType, action.actionStart, action.actionEnd, editTextView)
            }

            ActionPerformed.REMOVE ->
                removeStyleSpan(spanType, action.actionStart, action.actionEnd, editTextView)
        }


        editTextView.setSelection(action.actionEnd)

        textStateWatcher.setMakingInternalEdits(false)
    }

    private fun addStyleSpan(spanType: SpanType, spanStart: Int, spanEnd: Int,
                             editTextView: EditText){

        val setSpan: Any? = when (spanType) {

            SpanType.BOLD_SPAN -> StyleSpan(Typeface.BOLD)

            SpanType.ITALICS_SPAN -> StyleSpan(Typeface.ITALIC)

            SpanType.UNDERLINE_SPAN -> UnderlineTextFormatter.CustomUnderlineSpan()

            SpanType.BULLET_SPAN ->
                CustomBulletSpan(30, 6f, BulletType.DEFAULT, null)

            SpanType.IMAGE_SPAN -> null

            SpanType.CHECKLIST_SPAN -> null
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

    private fun removeStyleSpan(spanType: SpanType, targetSpanStart: Int, targetSpanEnd: Int,
                                editTextView: EditText){

        val spanList = getDesiredStyleSpans(spanType, editTextView)


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



    fun processImageSpanAction(action: Action, editTextView: EditText, shouldUndoAction: Boolean,
                               imageBitmap: Bitmap) {

        if (shouldUndoAction) {

            when (action.actionPerformed) {

                ActionPerformed.ADD -> performImageSpanAction(ActionPerformed.REMOVE, action,
                    editTextView, imageBitmap)

                ActionPerformed.REMOVE -> performImageSpanAction(ActionPerformed.ADD, action,
                    editTextView, imageBitmap)
            }
        } else {

            performImageSpanAction(action.actionPerformed, action, editTextView, imageBitmap)
        }
    }

    private fun performImageSpanAction(actionPerformed: ActionPerformed, action: Action,
                                       editTextView: EditText, imageBitmap: Bitmap) {

        val spanType = action.actionInfo as SpanType


        textStateWatcher.setMakingInternalEdits(true)

        when (actionPerformed) {

            ActionPerformed.ADD ->
                addImageSpan(action.actionStart, action.actionEnd, editTextView, imageBitmap)

            ActionPerformed.REMOVE ->
                removeStyleSpan(spanType, action.actionStart, action.actionEnd, editTextView)
        }

        editTextView.setSelection(action.actionEnd)

        textStateWatcher.setMakingInternalEdits(false)
    }

    private fun addImageSpan(spanStart: Int, spanEnd: Int, editTextView: EditText,
                             imageBitmap: Bitmap){

        val imageSpan = CustomImageSpan(imageBitmap)
        val objectCharacter = '\uFFFC'

        val imageText = SpannableStringBuilder("$objectCharacter ")

        // Apply the CustomImageSpan to the object character
        imageText.setSpan(
            imageSpan,
            0,
            1,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        editTextView.text.replace(spanStart, spanEnd, imageText)

        (editTextView as CustomSelectionET).triggerSelectionChanged()
    }



    private fun getDesiredStyleSpans(spanType: SpanType, editTextView: EditText): Array<out Any>? {

        val spanClass = when(spanType) {

            SpanType.BOLD_SPAN, SpanType.ITALICS_SPAN  -> StyleSpan::class.java

            SpanType.UNDERLINE_SPAN -> UnderlineTextFormatter.CustomUnderlineSpan::class.java

            SpanType.BULLET_SPAN -> CustomBulletSpan::class.java

            SpanType.IMAGE_SPAN -> CustomImageSpan::class.java

            SpanType.CHECKLIST_SPAN -> ChecklistSpan::class.java
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

            else -> allSpans
        }
    }

}