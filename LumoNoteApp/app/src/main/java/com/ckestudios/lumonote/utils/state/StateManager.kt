package com.ckestudios.lumonote.utils.state

import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.data.models.Action
import com.ckestudios.lumonote.data.models.ActionType
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.textformatting.TextFormatHelper

class StateManager(private val editTextView: EditText) {

    private val undoStack = ActionStateStack()
    private val redoStack = ActionStateStack()
    private var isNewAction = false

    private val textFormatHelper = TextFormatHelper()
    private val generalUIHelper = GeneralUIHelper()
    private lateinit var actionInterpreter: ActionInterpreter

    private var actionToUndo: Action? = null
    private var actionToRedo: Action? = null


    fun checkIfUndoEmpty(): Boolean {

        return undoStack.isStackEmpty()
    }

    fun checkIfRedoEmpty(): Boolean {

        return redoStack.isStackEmpty()
    }


    private fun setActionToUndo(action: Action) {

        actionToUndo = action
        actionToRedo = null
    }

    private fun setActionToRedo(action: Action) {

        actionToRedo = action
        actionToUndo = null
    }


    fun addToUndo(undoAction: Action) {

        val inRedoStack = redoStack.getStackContents().contains(undoAction)

        if (!inRedoStack) {

            clearRedoStack()
        }

        undoStack.pushActionToStack(undoAction)
    }

    private fun addToRedo(redoAction: Action) {

        redoStack.pushActionToStack(redoAction)
    }


    fun undoAction(actionInterpreter: ActionInterpreter) {

        this.actionInterpreter = actionInterpreter

        val topAction = undoStack.getTopActionOfStack()

        Log.d("TextWatcher", "undo: $topAction")

        if (topAction == null) return

        performUndo(topAction)


        if (topAction.actionIsReplacement) {

            val secondAction = undoStack.getTopActionOfStack()

            Log.d("TextWatcher", "undo2nd: $secondAction")

            if (secondAction == null) return

            performUndo(secondAction)
        }
    }

    fun redoAction() {

        val topAction = redoStack.getTopActionOfStack()

        Log.d("TextWatcher", "redo: $topAction")

        if (topAction == null) return

        performRedo(topAction)


        if (topAction.actionIsReplacement) {

            val secondAction = redoStack.getTopActionOfStack()

            Log.d("TextWatcher", "redo2nd: $secondAction")

            if (secondAction == null) return

            performRedo(secondAction)
        }
    }


    private fun performUndo(undoAction: Action) {

        setActionToUndo(undoAction)

        addToRedo(undoAction)

        undoStack.popActionFromStack()

        if (actionToUndo == null) return


        when (actionToUndo!!.actionType) {

            ActionType.TEXT -> actionInterpreter.processTextAction(actionToUndo!!,
                editTextView, true)

            ActionType.SPAN -> {} //actionInterpreter.performSpanAction()
        }
    }

    private fun performRedo(redoAction: Action) {

        setActionToRedo(redoAction)

        addToUndo(redoAction)

        redoStack.popActionFromStack()

        if (actionToRedo == null) return


        when (actionToRedo!!.actionType) {

            ActionType.TEXT -> actionInterpreter.processTextAction(actionToRedo!!,
                editTextView, false)

            ActionType.SPAN -> {} //actionInterpreter.performSpanAction()
        }
    }


    private fun clearRedoStack() {

        redoStack.clearStack()
    }

}