package com.ckestudios.lumonote.utils.state

import android.graphics.Bitmap
import android.widget.EditText
import com.ckestudios.lumonote.data.models.Action
import com.ckestudios.lumonote.data.models.ActionType
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.utils.helpers.ActionHelper

class StateManager(private val editTextView: EditText) {

    private val undoStack = ActionStateStack()
    private val redoStack = ActionStateStack()
    private var isNewAction = false

    private lateinit var actionInterpreter: ActionInterpreter

    private var actionToUndo: Action? = null
    private var actionToRedo: Action? = null

    private var imageCache = mutableMapOf<String, Triple<Bitmap, Int, Int>>()
    private var customBulletCache = mutableMapOf<String, String>()


    fun addImageToCache(image: Bitmap, start: Int, end: Int, identifier: String) {

        imageCache[identifier] = Triple(image, start, end)

//        Log.d("SpanWatcher", "imageCache: ${imageCache.keys}")
    }

    fun removeImageFromCache(imageIdentifier: String) {

        if (imageIdentifier in imageCache.keys){

            imageCache.remove(imageIdentifier)
        }
    }

    fun addBulletToCache(customBullet: String, identifier: String) {

        customBulletCache[identifier] = customBullet

//        Log.d("SpanWatcher", "customBulletCache: ${customBulletCache.keys}")
    }

    fun removeBulletFromCache(bulletIdentifier: String) {

        if (bulletIdentifier in customBulletCache.keys){

            customBulletCache.remove(bulletIdentifier)
        }
    }

    fun cacheCleanUp(cache: MutableMap<*, *>) {
         for (pair in cache) {

             //if pair.key not in redo or undo actions multipart, remove
             //cache.remove(key)
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

        cacheCleanUp(imageCache)
        cacheCleanUp(customBulletCache)

        val lastUndoAction = undoStack.getTopActionOfStack()
        var checklistUndoSpan: Action? = null

        val inRedoStack = redoStack.getStackContents().contains(undoAction)
        val imageAdded = ActionHelper.imageWasJustAdded(lastUndoAction, undoAction)
        val checklistAdded =
            ActionHelper.checklistWasJustAdded(undoAction, undoStack.getStackContents())

        if (!inRedoStack) { clearRedoStack() }


        if (imageAdded) {

            undoAction.actionIsMultipart = true
            undoAction.actionMultipartIdentifier = lastUndoAction!!.actionMultipartIdentifier

            // reverse the order of the actions since image span registered first
            undoStack.popActionFromStack()
        }

        if (checklistAdded) {

            val undoContents = undoStack.getStackContents()
            val checklistSpanPos =
                undoContents.indexOfFirst { it.actionInfo == SpanType.CHECKLIST_SPAN }

            if (checklistSpanPos != -1) {
                undoContents[checklistSpanPos].actionIsMultipart = true
                undoContents[checklistSpanPos].actionMultipartIdentifier =
                    undoAction.actionMultipartIdentifier
            }

            // reverse the order of the actions since checklist span registered first
            undoStack.popActionFromStack()
            checklistUndoSpan = undoStack.getTopActionOfStack()
            undoStack.popActionFromStack()

            if (lastUndoAction != null) {
                undoStack.pushActionToStack(lastUndoAction)
            }
        }

        undoStack.pushActionToStack(undoAction)

        if (lastUndoAction != null && imageAdded) {

            undoStack.pushActionToStack(lastUndoAction)
        }
        if (checklistUndoSpan != null) {

            undoStack.pushActionToStack(checklistUndoSpan)
        }

//        Log.d("SpanWatcher", "undoAdd: $undoAction")
//        Log.d("SpanWatcher", "undoStack: ${undoStack.getStackContents()}")
    }


    private fun addToRedo(redoAction: Action) {

        cacheCleanUp(imageCache)
        cacheCleanUp(customBulletCache)

        redoStack.pushActionToStack(redoAction)
    }

    fun processAction(actionInterpreter: ActionInterpreter, isUndo: Boolean) {

        this.actionInterpreter = actionInterpreter

        val stack = if (isUndo) undoStack else redoStack

        var topAction = stack.getTopActionOfStack()

        val logText = if (isUndo) "undo" else "redo"
//        Log.d("SpanWatcher", "$logText: $topAction")


        if (topAction == null) return

        performUndoOrRedo(topAction, isUndo)

        val firstMultipartIdentifier = topAction.actionMultipartIdentifier ?: return
        var currentMultipartIdentifier: String

        while (topAction!!.actionIsMultipart) {

            topAction = stack.getTopActionOfStack()

//            Log.d("SpanWatcher", "${logText}NextPart: $topAction")

            if (topAction == null) return

            currentMultipartIdentifier = topAction.actionMultipartIdentifier ?: return

//            Log.d("SpanWatcher", "firstMultipartIdentifier: $firstMultipartIdentifier")
//            Log.d("SpanWatcher", "currentMultipartIdentifier: $currentMultipartIdentifier")

            if (currentMultipartIdentifier != firstMultipartIdentifier) return

            performUndoOrRedo(topAction, isUndo)
        }
    }


    private fun performUndoOrRedo(actionToPerform: Action, isUndo: Boolean) {

        if (isUndo) setActionToUndo(actionToPerform) else setActionToRedo(actionToPerform)

        if (isUndo) addToRedo(actionToPerform) else addToUndo(actionToPerform)

        if (isUndo) undoStack.popActionFromStack() else redoStack.popActionFromStack()

        val actionToUndoOrRedo = if (isUndo) actionToUndo else actionToRedo
        if (actionToUndoOrRedo == null) return


        when (actionToUndoOrRedo.actionType) {

            ActionType.TEXT -> actionInterpreter.interpretBasicAction(actionToUndoOrRedo,
                isUndo, true)

            ActionType.SPAN -> {

                when {
                    actionToUndoOrRedo.actionInfo == SpanType.IMAGE_SPAN -> {

                        val imageData =
                            imageCache[actionToUndoOrRedo.actionMultipartIdentifier!!]

                        if (imageData != null) {
                            actionInterpreter.interpretImageAction(actionToUndoOrRedo,
                                isUndo, imageData.first)
                        }
                    }

                    actionToUndoOrRedo.actionInfo == SpanType.BULLET_SPAN &&
                            actionToUndoOrRedo.actionIsMultipart -> {

                        val customBullet =
                            customBulletCache[actionToUndoOrRedo.actionMultipartIdentifier!!]

                        if (customBullet != null) {
                            actionInterpreter.interpretCustomBulletAction(actionToUndoOrRedo,
                                isUndo, customBullet)
                        }
                    }

                    else -> actionInterpreter.interpretBasicAction(actionToUndoOrRedo,
                        isUndo, false)
                }
            }

        }
    }


    private fun clearRedoStack() {

        redoStack.clearStack()
    }

}