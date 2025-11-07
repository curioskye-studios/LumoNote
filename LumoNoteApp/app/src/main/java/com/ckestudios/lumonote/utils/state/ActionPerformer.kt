package com.ckestudios.lumonote.utils.state

import android.graphics.Bitmap
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.data.models.Action
import com.ckestudios.lumonote.data.models.ActionPerformed
import com.ckestudios.lumonote.data.models.BulletType
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.ui.noteview.other.ChecklistSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomImageSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import com.ckestudios.lumonote.utils.textformatting.BasicTextFormatter
import com.ckestudios.lumonote.utils.textformatting.UnderlineTextFormatter

class ActionPerformer() {

    private var currentCustomBullet: String? = null
    private var currentImageBitmap: Bitmap? = null

    fun updateCustomBullet(bullet: String?) {
        currentCustomBullet = bullet
    }
    fun updateImageBitmap(bitmap: Bitmap?) {
        currentImageBitmap = bitmap
    }

    fun performTextAction(actionToPerform: ActionPerformed, action: Action, editTextView: EditText,
                          textStateWatcher: TextStateWatcher) {

        textStateWatcher.setMakingInternalEdits(true)

        when (actionToPerform) {

            ActionPerformed.ADD -> editTextView.text.insert(action.actionStart,
                action.actionInfo.toString())

            ActionPerformed.REMOVE -> editTextView.text.delete(action.actionStart,
                action.actionEnd)
        }

        editTextView.setSelection(action.actionEnd.coerceAtMost(editTextView.text.length))

        textStateWatcher.setMakingInternalEdits(false)
    }


    fun performBasicSpanAction(actionToPerform: ActionPerformed, action: Action,
                               editTextView: EditText, spanType: SpanType,
                                       textStateWatcher: TextStateWatcher) {

        textStateWatcher.setMakingInternalEdits(true)

        when (actionToPerform) {

            ActionPerformed.ADD -> addBasicSpan(spanType, action.actionStart, action.actionEnd,
                editTextView)

            ActionPerformed.REMOVE -> removeDesiredSpan(spanType, action.actionStart, action.actionEnd,
                editTextView)
        }


        editTextView.setSelection(action.actionEnd)

        textStateWatcher.setMakingInternalEdits(false)
    }


    fun performComplexSpanAction(actionToPerform: ActionPerformed, action: Action,
                                 editTextView: EditText, textStateWatcher: TextStateWatcher,
                                 isCustomBullet: Boolean, isImage: Boolean) {

        val spanType = action.actionInfo as SpanType
        val isAddAction = actionToPerform == ActionPerformed.ADD

        textStateWatcher.setMakingInternalEdits(true)

        when {
            isAddAction && isCustomBullet && currentCustomBullet != null ->
                addCustomBullet(action.actionStart, action.actionEnd, editTextView,
                        currentCustomBullet!!)

            isAddAction && isImage && currentImageBitmap != null ->
                addImageSpan(action.actionStart, action.actionEnd, editTextView,
                    currentImageBitmap!!)

            actionToPerform == ActionPerformed.REMOVE ->
                removeDesiredSpan(spanType, action.actionStart, action.actionEnd, editTextView)
        }

        editTextView.setSelection(action.actionEnd)

        textStateWatcher.setMakingInternalEdits(false)
    }


    fun addBasicSpan(spanType: SpanType, spanStart: Int, spanEnd: Int,
                             editTextView: EditText){

        val basicTextFormatter = BasicTextFormatter(editTextView, false, null)
        val underlineTextFormatter =
            UnderlineTextFormatter(editTextView, false, null)

        when (spanType) {

            SpanType.BOLD_SPAN, SpanType.ITALICS_SPAN ->
                basicTextFormatter.setBasicSpanType(spanType, spanStart, spanEnd)

            SpanType.UNDERLINE_SPAN -> underlineTextFormatter.processFormatting(spanStart, spanEnd)

            SpanType.BULLET_SPAN -> {
                removeDesiredSpan(SpanType.BULLET_SPAN, spanStart, spanEnd, editTextView)
                editTextView.text.setSpan(
                    CustomBulletSpan(30, 6f, BulletType.DEFAULT, null),
                    spanStart, spanEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            SpanType.CHECKLIST_SPAN -> {
                editTextView.text.setSpan(ChecklistSpan(editTextView.context), spanStart, spanEnd,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            else -> {}
        }

    }

    fun addCustomBullet(spanStart: Int, spanEnd: Int, editTextView: EditText,
                                customBullet: String){


        Log.d("SaveSpans", "point 2")

        removeDesiredSpan(SpanType.BULLET_SPAN, spanStart, spanEnd, editTextView)
        editTextView.text.setSpan(
            CustomBulletSpan(30, 6f, BulletType.CUSTOM, customBullet),
            spanStart, spanEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    fun addImageSpan(spanStart: Int, spanEnd: Int, editTextView: EditText, imageBitmap: Bitmap){

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



    private fun removeDesiredSpan(spanType: SpanType, targetSpanStart: Int, targetSpanEnd: Int,
                                  editTextView: EditText){

        val spanList = getDesiredSpans(spanType, editTextView)

        if (spanList != null) {

            for (span in spanList) {

                val spanStart = editTextView.text.getSpanStart(span)

                val spanEnd = editTextView.text.getSpanEnd(span)


                if ((spanStart == targetSpanStart && spanEnd == targetSpanEnd) ||
                    (spanType == SpanType.CHECKLIST_SPAN && spanEnd == targetSpanEnd)) {

                    editTextView.text.removeSpan(span)
                }
            }
        }

    }

    private fun getDesiredSpans(spanType: SpanType, editTextView: EditText): Array<out Any>? {

        val spanClass = when (spanType) {

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