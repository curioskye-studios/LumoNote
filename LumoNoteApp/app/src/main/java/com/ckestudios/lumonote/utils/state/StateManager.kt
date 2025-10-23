package com.ckestudios.lumonote.utils.state

import android.graphics.Bitmap
import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.data.models.Action
import com.ckestudios.lumonote.data.models.ActionType
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.textformatting.TextFormatterHelper

class StateManager(private val editTextView: EditText) {

    private val undoStack = ActionStateStack()
    private val redoStack = ActionStateStack()
    private var isNewAction = false

    private val textFormatterHelper = TextFormatterHelper()
    private val generalUIHelper = GeneralUIHelper()
    private lateinit var actionInterpreter: ActionInterpreter
    private val actionHelper = ActionHelper()

    private var actionToUndo: Action? = null
    private var actionToRedo: Action? = null

    private var imageCache = mutableMapOf<String, Triple<Bitmap, Int, Int>>()


    fun addImageToCache(image: Bitmap, start: Int, end: Int, identifier: String) {

        imageCache[identifier] = Triple(image, start, end)

        Log.d("SpanWatcher", imageCache.keys.toString())
    }

    fun removeImageFromCache(imageIdentifier: String) {

        if (imageIdentifier in imageCache.keys){

            imageCache.remove(imageIdentifier)
        }
    }


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

        val undoTopAction = undoStack.getTopActionOfStack()

        val inRedoStack = redoStack.getStackContents().contains(undoAction)

        if (!inRedoStack) { clearRedoStack() }


        if (imageWasJustAdded(undoTopAction, undoAction)) {

            undoAction.actionIsMultipart = true
            undoAction.actionMultipartIdentifier = undoTopAction!!.actionMultipartIdentifier

            // reverse the order of the actions since image span registered first
            undoStack.popActionFromStack()
        }

        undoStack.pushActionToStack(undoAction)

        if (undoTopAction != null && imageWasJustAdded(undoTopAction, undoAction)) {

            undoStack.pushActionToStack(undoTopAction)
        }

        Log.d("SpanWatcher", "undoAdd: $undoAction")
        Log.d("SpanWatcher", "undoStack: ${undoStack.getStackContents()}")
    }

    private fun imageWasJustAdded(spanUndoAction: Action?, textUndoAction: Action) : Boolean {

        return if (spanUndoAction == null) { false }

        else if (spanUndoAction.actionInfo is SpanType && textUndoAction.actionInfo is String) {

            val imagePlaceHolder = "￼"

            spanUndoAction.actionInfo == SpanType.IMAGE_SPAN &&
                    textUndoAction.actionInfo == imagePlaceHolder
        }

        else { false }
    }

    private fun addToRedo(redoAction: Action) {

        redoStack.pushActionToStack(redoAction)
    }


    fun undoAction(actionInterpreter: ActionInterpreter) {

        this.actionInterpreter = actionInterpreter

        var topAction = undoStack.getTopActionOfStack()

        Log.d("SpanWatcher", "undo: $topAction")

        if (topAction == null) return

        performUndo(topAction)


        val multipartIdentifier = topAction.actionMultipartIdentifier ?: return

        while (topAction!!.actionIsMultipart &&
                topAction!!.actionMultipartIdentifier == multipartIdentifier) {

            topAction = undoStack.getTopActionOfStack()

            Log.d("SpanWatcher", "undoNextPart: $topAction")

            if (topAction == null) return

            performUndo(topAction)
        }
    }

    fun redoAction() {

        var topAction = redoStack.getTopActionOfStack()

        Log.d("SpanWatcher", "redo: $topAction")

        if (topAction == null) return

        performRedo(topAction)


        val multipartIdentifier = topAction.actionMultipartIdentifier ?: return

        while (topAction!!.actionIsMultipart &&
            topAction!!.actionMultipartIdentifier == multipartIdentifier) {

            topAction = redoStack.getTopActionOfStack()

            Log.d("SpanWatcher", "redoNextPart: $topAction")

            if (topAction == null) return

            performRedo(topAction)
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

            ActionType.SPAN -> {

                when (actionToUndo!!.actionInfo) {

                    SpanType.IMAGE_SPAN -> {

                        val imageData =
                            imageCache[actionToUndo!!.actionMultipartIdentifier!!]

                        if (imageData != null) {
                            actionInterpreter.processImageSpanAction(actionToUndo!!, editTextView,
                                true, imageData.first)
                        }
                    }

                    else -> actionInterpreter.processStyleSpanAction(actionToUndo!!, editTextView,
                        true)
                }
            }
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

            ActionType.SPAN -> {
                 when (actionToRedo!!.actionInfo) {

                     SpanType.IMAGE_SPAN -> {

                         val imageData =
                             imageCache[actionToRedo!!.actionMultipartIdentifier!!]

                         if (imageData != null) {
                             actionInterpreter.processImageSpanAction(actionToRedo!!,
                                 editTextView, false, imageData.first)
                         }
                     }

                     else -> actionInterpreter.processStyleSpanAction(actionToRedo!!, editTextView,
                         false)
                 }
            }

        }
    }


    private fun clearRedoStack() {

        redoStack.clearStack()
    }

}