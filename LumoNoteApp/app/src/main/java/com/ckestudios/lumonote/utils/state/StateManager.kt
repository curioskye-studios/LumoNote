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
    private var customBulletCache = mutableMapOf<String, String>()


    fun addImageToCache(image: Bitmap, start: Int, end: Int, identifier: String) {

        imageCache[identifier] = Triple(image, start, end)

        Log.d("SpanWatcher", "imageCache: ${imageCache.keys}")
    }

    fun removeImageFromCache(imageIdentifier: String) {

        if (imageIdentifier in imageCache.keys){

            imageCache.remove(imageIdentifier)
        }
    }

    fun addBulletToCache(customBullet: String, identifier: String) {

        customBulletCache[identifier] = customBullet

        Log.d("SpanWatcher", "customBulletCache: ${customBulletCache.keys}")
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

        Log.d("SpanWatcher", "undoAdd: $undoAction")
        Log.d("SpanWatcher", "undoStack: ${undoStack.getStackContents()}")
    }


    private fun addToRedo(redoAction: Action) {

        cacheCleanUp(imageCache)
        cacheCleanUp(customBulletCache)

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

            currentMultipartIdentifier = topAction.actionMultipartIdentifier ?: return

//            Log.d("SpanWatcher", "firstMultipartIdentifier: $firstMultipartIdentifier")
//            Log.d("SpanWatcher", "currentMultipartIdentifier: $currentMultipartIdentifier")

            if (currentMultipartIdentifier != firstMultipartIdentifier) return

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

                when {
                    actionToUndo!!.actionInfo == SpanType.IMAGE_SPAN -> {

                        val imageData =
                            imageCache[actionToUndo!!.actionMultipartIdentifier!!]

                        if (imageData != null) {
                            actionInterpreter.processImageSpanAction(actionToUndo!!, editTextView,
                                true, imageData.first)
                        }
                    }

                    actionToUndo!!.actionInfo == SpanType.BULLET_SPAN &&
                            actionToUndo!!.actionIsMultipart -> {

                        val customBullet =
                            customBulletCache[actionToUndo!!.actionMultipartIdentifier!!]

                        if (customBullet != null) {
                            actionInterpreter.processCustomBulletAction(actionToUndo!!, editTextView,
                                true, customBullet)
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
                 when {
                     actionToRedo!!.actionInfo == SpanType.IMAGE_SPAN -> {

                         val imageData =
                             imageCache[actionToRedo!!.actionMultipartIdentifier!!]

                         if (imageData != null) {
//                             Log.d("SpanWatcher",
//                                 "Redo Image ID: ${actionToRedo!!.actionMultipartIdentifier}, " +
//                                         "Cache keys: ${imageCache.keys}")

                             actionInterpreter.processImageSpanAction(actionToRedo!!,
                                 editTextView, false, imageData.first)
                         }
                     }

                     actionToRedo!!.actionInfo == SpanType.BULLET_SPAN &&
                             actionToRedo!!.actionIsMultipart -> {

                         val customBullet =
                             customBulletCache[actionToRedo!!.actionMultipartIdentifier!!]

                         if (customBullet != null) {
                             actionInterpreter.processCustomBulletAction(actionToRedo!!,
                                 editTextView, false, customBullet)
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