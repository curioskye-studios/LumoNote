package com.ckestudios.lumonote.utils.state

import android.graphics.Bitmap
import android.widget.EditText
import com.ckestudios.lumonote.data.models.Action
import com.ckestudios.lumonote.data.models.ActionPerformed
import com.ckestudios.lumonote.data.models.SpanType

class ActionInterpreter(private val editTextView: EditText,
                        private val textStateWatcher: TextStateWatcher) {

    private val actionPerformer = ActionPerformer()

    fun interpretBasicAction(action: Action, shouldUndoAction: Boolean, isTextAction: Boolean) {

        val actionToPerform = getActionToPerform(action, shouldUndoAction)

        if (isTextAction) {
            actionPerformer.performTextAction(actionToPerform, action, editTextView,
                textStateWatcher)
        } else {

            val spanType = action.actionInfo as SpanType

            interpretBasicSpanAction(actionToPerform, action, spanType)
        }

    }

    private fun interpretBasicSpanAction(actionToPerform: ActionPerformed, action: Action,
                                         spanType: SpanType) {

        actionPerformer.performBasicSpanAction(actionToPerform, action, editTextView,
                spanType, textStateWatcher)
    }


    fun interpretCustomBulletAction(action: Action, shouldUndoAction: Boolean,
                                    customBullet: String) {

        val actionToPerform = getActionToPerform(action, shouldUndoAction)

        actionPerformer.updateCustomBullet(customBullet)

        actionPerformer.performComplexSpanAction(actionToPerform, action, editTextView,
            textStateWatcher, true, false)

        actionPerformer.updateCustomBullet(null)
    }

    fun interpretImageAction(action: Action, shouldUndoAction: Boolean, imageBitmap: Bitmap) {

        val actionToPerform = getActionToPerform(action, shouldUndoAction)

        actionPerformer.updateImageBitmap(imageBitmap)

        actionPerformer.performComplexSpanAction(actionToPerform, action, editTextView,
                textStateWatcher, false, true)

        actionPerformer.updateImageBitmap(null)
    }


    private fun getActionToPerform(action: Action, shouldUndoAction: Boolean) : ActionPerformed {

        return when {

            shouldUndoAction && action.actionPerformed == ActionPerformed.ADD -> {
                ActionPerformed.REMOVE
            }
            shouldUndoAction && action.actionPerformed == ActionPerformed.REMOVE -> {
                ActionPerformed.ADD
            }

            else -> action.actionPerformed
        }
    }

}