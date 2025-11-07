package com.ckestudios.lumonote.utils.state

class Archive {

    //ACTION INTERPRETER

//    fun processTextAction(action: Action, editTextView: EditText, shouldUndoAction: Boolean){
//
//        // Details e.g.: text - ""
//
//        if (shouldUndoAction) {
//
//            when (action.actionPerformed) {
//
//                ActionPerformed.ADD -> performTextAction(ActionPerformed.REMOVE, action,
//                    editTextView)
//
//                ActionPerformed.REMOVE -> performTextAction(ActionPerformed.ADD, action,
//                    editTextView)
//            }
//        } else {
//
//            performTextAction(action.actionPerformed, action, editTextView)
//        }
//    }
//
//    private fun performTextAction(actionPerformed: ActionPerformed, action: Action,
//                                  editTextView: EditText) {
//
//        textStateWatcher.setMakingInternalEdits(true)
//
//        when (actionPerformed) {
//
//            ActionPerformed.ADD -> {
//
//                editTextView.text.insert(action.actionStart, action.actionInfo.toString())
//            }
//
//            ActionPerformed.REMOVE -> editTextView.text.delete(action.actionStart,
//                action.actionEnd)
//        }
//
//        editTextView.setSelection(action.actionEnd.coerceAtMost(editTextView.text.length))
//
//        textStateWatcher.setMakingInternalEdits(false)
//    }
//
//
//
//    fun processStyleSpanAction(action: Action, editTextView: EditText, shouldUndoAction: Boolean){
//
//        // Details e.g.: spantype: spanType.SPAN
//
//        if (shouldUndoAction) {
//
//            when (action.actionPerformed) {
//
//                ActionPerformed.ADD -> performStyleSpanAction(ActionPerformed.REMOVE, action,
//                    editTextView)
//
//                ActionPerformed.REMOVE -> performStyleSpanAction(ActionPerformed.ADD, action,
//                    editTextView)
//            }
//        } else {
//
//            performStyleSpanAction(action.actionPerformed, action, editTextView)
//        }
//    }
//
//    private fun performStyleSpanAction(actionPerformed: ActionPerformed, action: Action,
//                                       editTextView: EditText) {
//
//        val spanType = action.actionInfo as SpanType
//
//
//        textStateWatcher.setMakingInternalEdits(true)
//
//        when (actionPerformed) {
//
//            ActionPerformed.ADD ->
//                ActionPerformer.addStyleSpan(spanType, action.actionStart, action.actionEnd,
//                    editTextView)
//
//            ActionPerformed.REMOVE ->
//                ActionPerformer.removeStyleSpan(spanType, action.actionStart, action.actionEnd,
//                    editTextView)
//        }
//
//
//        editTextView.setSelection(action.actionEnd)
//
//        textStateWatcher.setMakingInternalEdits(false)
//    }
//
//
//
//
//    fun processImageSpanAction(action: Action, editTextView: EditText, shouldUndoAction: Boolean,
//                               imageBitmap: Bitmap) {
//
//        if (shouldUndoAction) {
//
//            when (action.actionPerformed) {
//
//                ActionPerformed.ADD -> performImageSpanAction(ActionPerformed.REMOVE, action,
//                    editTextView, imageBitmap)
//
//                ActionPerformed.REMOVE -> performImageSpanAction(ActionPerformed.ADD, action,
//                    editTextView, imageBitmap)
//            }
//        } else {
//
//            performImageSpanAction(action.actionPerformed, action, editTextView, imageBitmap)
//        }
//    }
//
//    private fun performImageSpanAction(actionPerformed: ActionPerformed, action: Action,
//                                       editTextView: EditText, imageBitmap: Bitmap) {
//
//        val spanType = action.actionInfo as SpanType
//
//
//        textStateWatcher.setMakingInternalEdits(true)
//
//        when (actionPerformed) {
//
//            ActionPerformed.ADD ->
//                ActionPerformer.addImageSpan(action.actionStart, action.actionEnd, editTextView,
//                    imageBitmap)
//
//            ActionPerformed.REMOVE ->
//                ActionPerformer.removeStyleSpan(spanType, action.actionStart, action.actionEnd,
//                    editTextView)
//        }
//
//        editTextView.setSelection(action.actionEnd)
//
//        textStateWatcher.setMakingInternalEdits(false)
//    }
//
//
//
//
//    fun processCustomBulletAction(action: Action, editTextView: EditText, shouldUndoAction: Boolean,
//                                  customBullet: String) {
//
//        if (shouldUndoAction) {
//
//            when (action.actionPerformed) {
//
//                ActionPerformed.ADD -> performCustomBulletAction(ActionPerformed.REMOVE, action,
//                        editTextView, customBullet)
//
//                ActionPerformed.REMOVE -> performCustomBulletAction(ActionPerformed.ADD, action,
//                        editTextView, customBullet)
//            }
//        } else {
//
//            performCustomBulletAction(action.actionPerformed, action, editTextView, customBullet)
//        }
//    }
//
//    private fun performCustomBulletAction(actionPerformed: ActionPerformed, action: Action,
//                                       editTextView: EditText, customBullet: String) {
//
//        val spanType = action.actionInfo as SpanType
//
//
//        textStateWatcher.setMakingInternalEdits(true)
//
//        when (actionPerformed) {
//
//            ActionPerformed.ADD -> {
//                ActionPerformer.addCustomBullet(action.actionStart, action.actionEnd, editTextView,
//                    customBullet)
//            }
//
//            ActionPerformed.REMOVE -> {
//                ActionPerformer.removeStyleSpan(spanType, action.actionStart, action.actionEnd,
//                    editTextView)
//            }
//        }
//
//        editTextView.setSelection(action.actionEnd)
//
//        textStateWatcher.setMakingInternalEdits(false)
//    }
//
//
//
//
//
//
//
//
//
//
//
//    //ACTION PERFORMER
//
//    fun addStyleSpan(spanType: SpanType, spanStart: Int, spanEnd: Int,
//                             editTextView: EditText){
//
//        val setSpan: Any? = when (spanType) {
//
//            SpanType.BOLD_SPAN -> StyleSpan(Typeface.BOLD)
//
//            SpanType.ITALICS_SPAN -> StyleSpan(Typeface.ITALIC)
//
//            SpanType.UNDERLINE_SPAN -> UnderlineTextFormatter.CustomUnderlineSpan()
//
//            SpanType.BULLET_SPAN -> {
//                removeStyleSpan(spanType, spanStart, spanEnd, editTextView)
//                CustomBulletSpan(30, 6f, BulletType.DEFAULT, null)
//            }
//
//            SpanType.IMAGE_SPAN -> null
//
//            SpanType.CHECKLIST_SPAN -> ChecklistSpan(editTextView.context)
//        }
//
//
//        if (setSpan != null) {
//
//            editTextView.text.setSpan(
//                setSpan,
//                spanStart,
//                spanEnd,
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//        }
//    }
//
//
//
//
//    fun addImageSpan(spanStart: Int, spanEnd: Int, editTextView: EditText,
//                             imageBitmap: Bitmap){
//
//        val imageSpan = CustomImageSpan(imageBitmap)
//        val objectCharacter = '\uFFFC'
//
//        val imageText = SpannableStringBuilder("$objectCharacter ")
//
//        // Apply the CustomImageSpan to the object character
//        imageText.setSpan(
//            imageSpan,
//            0,
//            1,
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//
//        editTextView.text.replace(spanStart, spanEnd, imageText)
//
//        (editTextView as CustomSelectionET).triggerSelectionChanged()
//    }
//
//
//    fun addCustomBullet(spanStart: Int, spanEnd: Int, editTextView: EditText,
//                                customBullet: String){
//
//        editTextView.text.setSpan(
//            CustomBulletSpan(30, 6f, BulletType.CUSTOM, customBullet),
//            spanStart,
//            spanEnd,
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//    }
//
//
//
//    private fun getDesiredStyleSpans(spanType: SpanType, editTextView: EditText): Array<out Any>? {
//
//        val spanClass = when(spanType) {
//
//            SpanType.BOLD_SPAN, SpanType.ITALICS_SPAN  -> StyleSpan::class.java
//
//            SpanType.UNDERLINE_SPAN -> UnderlineTextFormatter.CustomUnderlineSpan::class.java
//
//            SpanType.BULLET_SPAN -> CustomBulletSpan::class.java
//
//            SpanType.IMAGE_SPAN -> CustomImageSpan::class.java
//
//            SpanType.CHECKLIST_SPAN -> ChecklistSpan::class.java
//        }
//
//
//        val allSpans =
//            editTextView.text.getSpans(0, editTextView.text.length, spanClass)
//
//        return when (spanType) {
//
//            SpanType.BOLD_SPAN -> {
//                val styleSpans = allSpans as Array<StyleSpan>
//                styleSpans.filter { it.style == Typeface.BOLD }.toTypedArray()
//            }
//
//            SpanType.ITALICS_SPAN -> {
//                val styleSpans = allSpans as Array<StyleSpan>
//                styleSpans.filter { it.style == Typeface.ITALIC }.toTypedArray()
//            }
//
//            else -> allSpans
//        }
//    }



    //private fun performUndo(undoAction: Action) {
    //
    //
    //    }
    //
    //    private fun performRedo(redoAction: Action) {
    //
    //        setActionToRedo(redoAction)
    //
    //        addToUndo(redoAction)
    //
    //        redoStack.popActionFromStack()
    //
    //        if (actionToRedo == null) return
    //
    //
    //        when (actionToRedo!!.actionType) {
    //
    //            ActionType.TEXT -> actionInterpreter.interpretBasicAction(actionToRedo!!,
    //                false, true)
    //
    //            ActionType.SPAN -> {
    //                 when {
    //                     actionToRedo!!.actionInfo == SpanType.IMAGE_SPAN -> {
    //
    //                         val imageData =
    //                             imageCache[actionToRedo!!.actionMultipartIdentifier!!]
    //
    //                         if (imageData != null) {
    ////                             Log.d("SpanWatcher",
    ////                                 "Redo Image ID: ${actionToRedo!!.actionMultipartIdentifier}, " +
    ////                                         "Cache keys: ${imageCache.keys}")
    //
    //                             actionInterpreter.interpretImageAction(actionToRedo!!,
    //                                 false, imageData.first)
    //                         }
    //                     }
    //
    //                     actionToRedo!!.actionInfo == SpanType.BULLET_SPAN &&
    //                             actionToRedo!!.actionIsMultipart -> {
    //
    //                         val customBullet =
    //                             customBulletCache[actionToRedo!!.actionMultipartIdentifier!!]
    //
    //                         if (customBullet != null) {
    ////                             actionInterpreter.processCustomBulletAction(actionToRedo!!,
    ////                                 editTextView, false, customBullet)
    //                         }
    //                     }
    //
    //                     else -> actionInterpreter.interpretBasicAction(actionToRedo!!,
    //                        false, false)
    //                 }
    //            }
    //
    //        }
    //    }

}