package com.ckestudios.lumonote.utils.state

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.ckestudios.lumonote.data.models.Action
import com.ckestudios.lumonote.data.models.ActionPerformed
import com.ckestudios.lumonote.data.models.ActionType
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import java.util.Timer
import kotlin.concurrent.timer

class TextStateWatcher(private val editTextView: CustomSelectionET,
                       private val stateManager: StateManager) : TextWatcher {

    private var loggingChanges = false
    private var makingInternalEdits = false
    private var startOfChanges = false

    private var beforeText: String = ""
    private var beforeTextState: String = ""
    private var afterText: String = ""

    private var saveChangesOnPauseTimer: Timer? = null
    private var uiRefreshTimer: Timer? = null

    init {

        if (editTextView.text.toString().isNotEmpty()) {

            beforeText = editTextView.text.toString()
        }

        startSaveChangesTimer()

        // run every 0.05 secs
        uiRefreshTimer = timer(initialDelay = 50, period = 50) {

            // refresh selection and UI indicators on main thread
            Handler(Looper.getMainLooper()).post {

                // triggers re-evaluation of redo and undo display
                editTextView.triggerSelectionChanged()
            }
        }

    }


    override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {

        if (loggingChanges || makingInternalEdits) return

        beforeTextState = text?.toString() ?: ""

        if (startOfChanges) {

            beforeText = beforeTextState
            startOfChanges = false
        }

        Log.d("TextWatcher", "Before: $text (start=$start, count=$count, after=$after)")
    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {

        if (makingInternalEdits) return

        // reset batch timer on keystroke
        startSaveChangesTimer()

        Log.d("TextWatcher", "On: (start=$start, before=$before, count=$count)")
    }

    override fun afterTextChanged(newText: Editable?) {

        if (loggingChanges || newText == null || makingInternalEdits) return

        // update after text snapshot
        afterText = buildString {
            for (element in newText) {
                append(element) // preserves invisible chars like \uFFFC
            }
        }

        Log.d("TextWatcher", "After: $afterText")
    }


    fun setMakingInternalEdits(isTrue: Boolean) {

        makingInternalEdits = isTrue
    }

    private fun beforeTextIsOriginalText(): Boolean {

        return beforeText == editTextView.text.toString()
    }

    // Starts 1s timer for batching keystrokes
    private fun startSaveChangesTimer() {

        stopSaveChangesTimer()

        saveChangesOnPauseTimer = timer(initialDelay = 700, period = 700) {

            // commit when user pauses typing
            stopSaveChangesTimer()
            commitChange()

            // reset snapshot
            startOfChanges = true
        }
    }

    private fun stopSaveChangesTimer() {

        saveChangesOnPauseTimer?.cancel()
        saveChangesOnPauseTimer = null
    }


    private fun commitChange() {

        loggingChanges = true

        val oldText = beforeText
        val newText = afterText

        Log.d("TextWatcher", "oldText: $oldText")
        Log.d("TextWatcher", "newText: $newText")

        // if nothing changed
        if (oldText == newText || beforeTextIsOriginalText()) {

            loggingChanges = false
            return
        }

        // find start and end differences between old and new text
        val changeStart = findChangeStart(oldText, newText)
        val oldTextSegment = getOldTextSegment(changeStart, oldText, newText)
        val newTextSegment = getNewTextSegment(changeStart, oldText, newText)

        Log.d("TextWatcher", "Diff → start=$changeStart, oldSeg='$oldTextSegment', " +
                "newSeg='$newTextSegment'")

        processTextChange(changeStart, oldText, newText, oldTextSegment, newTextSegment)

        // update baseline text for next difference comparison
        beforeText = newText

        loggingChanges = false
    }

    private fun processTextChange(changeStart: Int, oldText: String, newText: String,
                                  oldTextSegment: String, newTextSegment: String) {

        val objectChar = '\uFFFC'
        val oldObjCharCount = oldText.filter { it == objectChar}.length
        val newObjCharCount = newText.filter { it == objectChar}.length
        Log.d("TextWatcher", "oldObjCharCount: $oldObjCharCount")
        Log.d("TextWatcher", "newObjCharCount: $newObjCharCount")

        var actionPerformed: ActionPerformed? = null
        var actionType: ActionType? = null
        var isMultipart: Boolean? = null
        var multipartIdentifier: String? = null
        var start: Int? = null
        var end: Int? = null
        var actionInfo: Any? = null

        when {
            // ADD → user inserted image char after first one logged
            oldTextSegment.isEmpty() && newTextSegment.isEmpty() &&
                    newObjCharCount > oldObjCharCount -> {

                val newImageCharPos = newText.lastIndexOf(objectChar)
                actionPerformed = ActionPerformed.ADD
                actionType = ActionType.TEXT
                isMultipart = false
                multipartIdentifier = null
                start = newImageCharPos
                end = newImageCharPos + 1
                actionInfo = objectChar.toString()

                Log.d("TextWatcher", "ADD IMAGE: '$objectChar' at $newImageCharPos")
            }

            // NEWLINE → user only changed a newline
            oldText.replace("\n", "") == newText.replace("\n", "") -> {

                val diffStart = findChangeStart(oldText, newText)
                val isAdd = newText.length > oldText.length
                actionPerformed = if (isAdd) ActionPerformed.ADD else ActionPerformed.REMOVE
                actionType = ActionType.TEXT
                isMultipart = false
                multipartIdentifier = null
                start = diffStart
                end = diffStart + 1
                actionInfo = "\n"

                Log.d("TextWatcher", "NEWLINE ${if (isAdd) "ADD" else "REMOVE"} at $diffStart")
            }

            // ADD → user inserted text
            oldTextSegment.isEmpty() && newTextSegment.isNotEmpty() -> {

                actionPerformed = ActionPerformed.ADD
                actionType = ActionType.TEXT
                isMultipart = false
                multipartIdentifier = null
                start = changeStart
                end = changeStart + newTextSegment.length
                actionInfo = newTextSegment

                Log.d("TextWatcher", "ADD: '$newTextSegment' at $changeStart")
            }

            // REMOVE → user deleted text
            newTextSegment.isEmpty() && oldTextSegment.isNotEmpty() -> {

                actionPerformed = ActionPerformed.REMOVE
                actionType = ActionType.TEXT
                isMultipart = false
                multipartIdentifier = null
                start = changeStart
                end = changeStart + oldTextSegment.length
                actionInfo = oldTextSegment

                Log.d("TextWatcher", "REMOVE: '$oldTextSegment' at $changeStart")
            }

            // REPLACE → user replaced text segment
            oldTextSegment.isNotEmpty() && newTextSegment.isNotEmpty() -> {

                val replaceMultipartIdentifier = ActionHelper.getMultipartIdentifier()

                // two part action, log both removal and insertion for replace
                saveTextStateToUndo(ActionPerformed.REMOVE, ActionType.TEXT, true,
                    replaceMultipartIdentifier, changeStart, changeStart + oldTextSegment.length,
                    oldTextSegment)

                saveTextStateToUndo(ActionPerformed.ADD, ActionType.TEXT, true,
                    replaceMultipartIdentifier, changeStart, changeStart + newTextSegment.length,
                    newTextSegment)

                Log.d("TextWatcher", "REPLACE: '$oldTextSegment' → '$newTextSegment' at " +
                        "$changeStart")
            }

        }

        if (actionPerformed == null || actionType == null || isMultipart == null || start == null ||
            end == null) return

        saveTextStateToUndo(actionPerformed, actionType, isMultipart, multipartIdentifier, start,
            end, actionInfo)
    }

    private fun saveTextStateToUndo(actionPerformed: ActionPerformed, actionType: ActionType,
                                    isMultipart: Boolean, multipartIdentifier: String?, start: Int,
                                    end: Int, actionInfo: Any?) {
        val action = Action(
            actionPerformed,
            actionType,
            isMultipart,
            multipartIdentifier,
            start,
            end,
            actionInfo
        )
        stateManager.addToUndo(action)
    }


    private fun getOldTextSegment(changeStart: Int, oldText: String, newText: String) : String {

        val oldSuffixMatchLength = findChangeEnd(oldText, newText)
        val oldEndExcludingMatch = oldText.length - oldSuffixMatchLength

        return if (changeStart < oldEndExcludingMatch)
            oldText.substring(changeStart, oldEndExcludingMatch.coerceAtMost(oldText.length))
        else ""
    }

    private fun getNewTextSegment(changeStart: Int, oldText: String, newText: String) : String {

        val newSuffixMatchLength = findChangeEnd(newText, oldText)
        val newEndExcludingMatch = newText.length - newSuffixMatchLength

        return if (changeStart < newEndExcludingMatch)
            newText.substring(changeStart, newEndExcludingMatch.coerceAtMost(newText.length))
        else ""
    }


    private fun findChangeStart(oldText: String, newText: String): Int {

        // Length of the shorter text, prevent going out of bounds
        val maxToCompare = minOf(oldText.length, newText.length)

        for (i in 0 until maxToCompare) {

            if (oldText[i] != newText[i]) {

                return i
            }
        }

        Log.d("TextWatcher", "changeStart smallerTextSize: '$maxToCompare'")

        return maxToCompare
    }

    private fun findChangeEnd(firstText: String, secondText: String): Int {

        // Length of the shorter text, prevent going out of bounds
        val maxToCompare = minOf(firstText.length, secondText.length)
        var matchingCharsCount = 0

        val firstSpan = firstText.length - 1
        val secondSpan = secondText.length - 1

        while (
            matchingCharsCount < maxToCompare &&
            firstText[firstSpan - matchingCharsCount] == secondText[secondSpan - matchingCharsCount]
        ) {
            matchingCharsCount++
        }

        // handle case where extra chars exist but all matched so far
        if (matchingCharsCount == maxToCompare && firstText.length != secondText.length) {
            return matchingCharsCount - 1
        }

        return matchingCharsCount
    }

}
