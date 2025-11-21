package com.ckestudios.lumonote.utils.helpers

import com.ckestudios.lumonote.data.models.Action
import com.ckestudios.lumonote.data.models.SpanType

object ActionHelper {

    private val generatedIdentifiers = mutableListOf<String>()

    fun getMultipartIdentifier(): String {

        var identifier = GeneralTextHelper.generateRandomString(8)

        while (identifier in generatedIdentifiers) {

            identifier = GeneralTextHelper.generateRandomString(8)
        }

        return identifier
    }

    fun imageWasJustAdded(spanUndoAction: Action?, textUndoAction: Action) : Boolean {

        return if (spanUndoAction == null) { false }

        else if (spanUndoAction.actionInfo == SpanType.IMAGE_SPAN && textUndoAction.actionInfo is String) {

            val imagePlaceHolder = "￼"

            // return this check
            textUndoAction.actionInfo == imagePlaceHolder &&
            spanUndoAction.actionStart == textUndoAction.actionStart &&
            spanUndoAction.actionEnd == textUndoAction.actionEnd
        }

        else { false }
    }

    fun checklistWasJustAdded(textUndoAction: Action, actionList: ArrayList<Action>) : Boolean {

        val allIdentifiers: List<String?> = actionList.map { it.actionMultipartIdentifier }

        return allIdentifiers.count { it == textUndoAction.actionMultipartIdentifier } == 1 &&
            actionList.any { it.actionInfo == SpanType.CHECKLIST_SPAN}
    }


}