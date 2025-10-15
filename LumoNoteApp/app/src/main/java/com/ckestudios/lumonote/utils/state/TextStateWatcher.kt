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
        afterText = newText.toString()

        Log.d("TextWatcher", "After: $newText")
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


        // nothing changed
        if (oldText == newText || beforeTextIsOriginalText()) {

            loggingChanges = false
            return
        }


        // find start and end differences between old and new text
        val changeStart = findChangeStart(oldText, newText)
        val oldSuffixMatchLength = findChangeEnd(oldText, newText)
        val newSuffixMatchLength = findChangeEnd(newText, oldText)

        // calculate actual text boundaries after trimming matches
        val oldEndExcludingMatch = oldText.length - oldSuffixMatchLength
        val newEndExcludingMatch = newText.length - newSuffixMatchLength

        // extract the changed (removed if replaced) portion from old text
        val oldSegment =
            if (changeStart < oldEndExcludingMatch)
                oldText.substring(changeStart, oldEndExcludingMatch.coerceAtMost(oldText.length))
            else ""

        // extract the changed (added if replaced) portion from new text
        val newSegment =
            if (changeStart < newEndExcludingMatch)
                newText.substring(changeStart, newEndExcludingMatch.coerceAtMost(newText.length))
            else ""

        Log.d("TextWatcher", "Diff → start=$changeStart, oldSeg='$oldSegment', newSeg='$newSegment'")


        when {

            // ADD → user inserted text
            oldSegment.isEmpty() && newSegment.isNotEmpty() -> {
                val action = Action(
                    ActionPerformed.ADD,
                    ActionType.TEXT,
                    false,
                    changeStart,
                    changeStart + newSegment.length,
                    newSegment
                )

                stateManager.addToUndo(action)
                Log.d("TextWatcher", "ADD: '$newSegment' at $changeStart")
            }

            // REMOVE → user deleted text
            newSegment.isEmpty() && oldSegment.isNotEmpty() -> {

                val action = Action(
                    ActionPerformed.REMOVE,
                    ActionType.TEXT,
                    false,
                    changeStart,
                    changeStart + oldSegment.length,
                    oldSegment
                )

                stateManager.addToUndo(action)
                Log.d("TextWatcher", "REMOVE: '$oldSegment' at $changeStart")
            }

            // REPLACE → user replaced text segment
            oldSegment.isNotEmpty() && newSegment.isNotEmpty() -> {

                val removeAction = Action(
                    ActionPerformed.REMOVE,
                    ActionType.TEXT,
                    true,
                    changeStart,
                    changeStart + oldSegment.length,
                    oldSegment
                )

                val addAction = Action(
                    ActionPerformed.ADD,
                    ActionType.TEXT,
                    true,
                    changeStart,
                    changeStart + newSegment.length,
                    newSegment
                )

                // log both removal and insertion for replace
                stateManager.addToUndo(removeAction)
                stateManager.addToUndo(addAction)

                Log.d("TextWatcher", "REPLACE: '$oldSegment' → '$newSegment' at $changeStart")
            }
        }

        // update baseline text for next diff comparison
        beforeText = newText

        loggingChanges = false
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

        val firstSpan = firstText.length - 1
        val secondSpan = secondText.length - 1

        var matchingCharsCount = 0

        while (
            matchingCharsCount < maxToCompare &&
            firstText[firstSpan - matchingCharsCount] == secondText[secondSpan - matchingCharsCount]
        ) {
            matchingCharsCount++
        }

        return matchingCharsCount
    }

}
