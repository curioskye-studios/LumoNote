package com.ckestudios.lumonote.utils.state

import android.graphics.Bitmap
import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.data.models.Action
import com.ckestudios.lumonote.data.models.ActionType
import com.ckestudios.lumonote.data.models.SpanType

class StateManager(private val editTextView: EditText) {

    private val undoStack = ActionStateStack()
    private val redoStack = ActionStateStack()
    private var isNewAction = false

    private lateinit var actionInterpreter: ActionInterpreter

    private var actionToUndo: Action? = null
    private var actionToRedo: Action? = null

    private var imageCache = mutableMapOf<String, Triple<Bitmap, Int, Int>>()
    private var customBulletCache = mutableListOf<Triple<String, Int, Int>>()


    fun addImageToCache(image: Bitmap, start: Int, end: Int, identifier: String) {

        imageCache[identifier] = Triple(image, start, end)

        Log.d("SpanWatcher", imageCache.keys.toString())
    }

    fun removeImageFromCache(imageIdentifier: String) {

        if (imageIdentifier in imageCache.keys){

            imageCache.remove(imageIdentifier)
        }
    }

    fun addBulletToCache(customBullet: String, start: Int, end: Int) {

        customBulletCache.add(Triple(customBullet, start, end))

        Log.d("SpanWatcher", customBulletCache.toString())
    }

    fun removeBulletFromCache(start: Int, end: Int) {

        for (bullet in customBulletCache) {

            if (bullet.second == start && bullet.third == end){

                customBulletCache.remove(bullet)
            }
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

        val spanUndoAction = undoStack.getTopActionOfStack()

        val inRedoStack = redoStack.getStackContents().contains(undoAction)

        if (!inRedoStack) { clearRedoStack() }


        if (imageWasJustAdded(spanUndoAction, undoAction)) {

            undoAction.actionIsMultipart = true
            undoAction.actionMultipartIdentifier = spanUndoAction!!.actionMultipartIdentifier

            // reverse the order of the actions since image span registered first
            undoStack.popActionFromStack()
        }

        undoStack.pushActionToStack(undoAction)

        if (spanUndoAction != null && imageWasJustAdded(spanUndoAction, undoAction)) {

            undoStack.pushActionToStack(spanUndoAction)
        }

        Log.d("SpanWatcher", "undoAdd: $undoAction")
        Log.d("SpanWatcher", "undoStack: ${undoStack.getStackContents()}")
    }

    private fun imageWasJustAdded(spanUndoAction: Action?, textUndoAction: Action) : Boolean {

        return if (spanUndoAction == null) { false }

        else if (spanUndoAction.actionInfo is SpanType && textUndoAction.actionInfo is String) {

            val imagePlaceHolder = "￼"

            spanUndoAction.actionInfo == SpanType.IMAGE_SPAN &&
                    textUndoAction.actionInfo == imagePlaceHolder &&
                    spanUndoAction.actionStart == textUndoAction.actionStart &&
                    spanUndoAction.actionEnd == textUndoAction.actionEnd
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


        val firstMultipartIdentifier = topAction.actionMultipartIdentifier ?: return
        var currentMultipartIdentifier: String

        while (topAction!!.actionIsMultipart) {

            topAction = undoStack.getTopActionOfStack()

            Log.d("SpanWatcher", "undoNextPart: $topAction")

            if (topAction == null) return

            currentMultipartIdentifier = topAction.actionMultipartIdentifier ?: return

//            Log.d("SpanWatcher", "firstMultipartIdentifier: $firstMultipartIdentifier")
//            Log.d("SpanWatcher", "currentMultipartIdentifier: $currentMultipartIdentifier")

            if (currentMultipartIdentifier != firstMultipartIdentifier) return

            performUndo(topAction)
        }
    }

    fun redoAction() {

        var topAction = redoStack.getTopActionOfStack()

        Log.d("SpanWatcher", "redo: $topAction")

        if (topAction == null) return

        performRedo(topAction)


        val firstMultipartIdentifier = topAction.actionMultipartIdentifier ?: return
        var currentMultipartIdentifier: String

        while (topAction!!.actionIsMultipart) {

            topAction = redoStack.getTopActionOfStack()

            Log.d("SpanWatcher", "redoNextPart: $topAction")

            if (topAction == null) return

            Log.d("SpanWatcher", "Point 1")
            Log.d("SpanWatcher", "currenttopAction: $topAction")

            currentMultipartIdentifier = topAction.actionMultipartIdentifier ?: return

            Log.d("SpanWatcher", "firstMultipartIdentifier: $firstMultipartIdentifier")
            Log.d("SpanWatcher", "currentMultipartIdentifier: $currentMultipartIdentifier")

            if (currentMultipartIdentifier != firstMultipartIdentifier) return

            performRedo(topAction)

            Log.d("SpanWatcher", "Point 2")
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
                             Log.d("SpanWatcher",
                                 "Redo Image ID: ${actionToRedo!!.actionMultipartIdentifier}, " +
                                         "Cache keys: ${imageCache.keys}")

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