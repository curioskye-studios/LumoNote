package com.ckestudios.lumonote.utils.state

import com.ckestudios.lumonote.data.models.Action
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.utils.basichelpers.BasicUtilityHelper

object ActionHelper {

    private val generatedIdentifiers = mutableListOf<String>()

    fun getMultipartIdentifier(): String {

        var identifier = BasicUtilityHelper.generateRandomString(8)

        while (identifier in generatedIdentifiers) {

            identifier = BasicUtilityHelper.generateRandomString(8)
        }

        return identifier
    }

    fun imageWasJustAdded(spanUndoAction: Action?, textUndoAction: Action) : Boolean {

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
}