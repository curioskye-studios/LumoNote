package com.ckestudios.lumonote.utils.state

import android.widget.EditText
import com.ckestudios.lumonote.data.models.Action
import com.ckestudios.lumonote.data.models.ActionPerformed

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

                if (editTextView.selectionStart != editTextView.selectionEnd) {
                    editTextView.setSelection(action.actionStart, action.actionEnd)
                }
            }

            ActionPerformed.REMOVE -> editTextView.text.delete(action.actionStart,
                action.actionEnd)
        }

        textStateWatcher.setMakingInternalEdits(false)
    }


    fun processSpanAction(action: Action, editTextView: EditText, shouldUndoAction: Boolean){

        // Details e.g.: spantype: spanType.SPAN

    }

    private fun performSpanAction() {


    }

}