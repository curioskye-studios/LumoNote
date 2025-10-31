package com.ckestudios.lumonote.utils.state

import android.graphics.Bitmap
import android.widget.EditText
import com.ckestudios.lumonote.data.models.Action
import com.ckestudios.lumonote.data.models.ActionPerformed
import com.ckestudios.lumonote.data.models.SpanType

class ActionInterpreter(private val textStateWatcher: TextStateWatcher) {

    private val actionPerformer = ActionPerformer()

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

        editTextView.setSelection(action.actionEnd.coerceAtMost(editTextView.text.length))

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

            ActionPerformed.ADD ->
                actionPerformer.addStyleSpan(spanType, action.actionStart, action.actionEnd,
                    editTextView)

            ActionPerformed.REMOVE ->
                actionPerformer.removeStyleSpan(spanType, action.actionStart, action.actionEnd,
                    editTextView)
        }


        editTextView.setSelection(action.actionEnd)

        textStateWatcher.setMakingInternalEdits(false)
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
                actionPerformer.addImageSpan(action.actionStart, action.actionEnd, editTextView,
                    imageBitmap)

            ActionPerformed.REMOVE ->
                actionPerformer.removeStyleSpan(spanType, action.actionStart, action.actionEnd,
                    editTextView)
        }

        editTextView.setSelection(action.actionEnd)

        textStateWatcher.setMakingInternalEdits(false)
    }




    fun processCustomBulletAction(action: Action, editTextView: EditText, shouldUndoAction: Boolean,
                                  customBullet: String) {

        if (shouldUndoAction) {

            when (action.actionPerformed) {

                ActionPerformed.ADD -> performCustomBulletAction(ActionPerformed.REMOVE, action,
                        editTextView, customBullet)

                ActionPerformed.REMOVE -> performCustomBulletAction(ActionPerformed.ADD, action,
                        editTextView, customBullet)
            }
        } else {

            performCustomBulletAction(action.actionPerformed, action, editTextView, customBullet)
        }
    }

    private fun performCustomBulletAction(actionPerformed: ActionPerformed, action: Action,
                                       editTextView: EditText, customBullet: String) {

        val spanType = action.actionInfo as SpanType


        textStateWatcher.setMakingInternalEdits(true)

        when (actionPerformed) {

            ActionPerformed.ADD -> {
                actionPerformer.addCustomBullet(action.actionStart, action.actionEnd, editTextView,
                    customBullet)
            }

            ActionPerformed.REMOVE -> {
                actionPerformer.removeStyleSpan(spanType, action.actionStart, action.actionEnd,
                    editTextView)
            }
        }

        editTextView.setSelection(action.actionEnd)

        textStateWatcher.setMakingInternalEdits(false)
    }



}