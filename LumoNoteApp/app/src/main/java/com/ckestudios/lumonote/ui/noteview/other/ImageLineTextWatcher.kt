package com.ckestudios.lumonote.ui.noteview.other

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.ckestudios.lumonote.utils.state.StateManager
import com.ckestudios.lumonote.utils.textformatting.TextFormatterHelper


/**
 * TextWatcher that enforces rules for lines containing images:
 * - No typing directly on an image line (except ENTER to create a new line below).
 * - Keeps only one image per line.
 */
class ImageLineTextWatcher(private val editTextView: EditText,
                           private val stateManager: StateManager) : TextWatcher {

    // Internal flag to prevent recursive edits
    private var internalEdit = false

    // Backup of text before a change
    private var beforeText: CharSequence = ""

    override fun beforeTextChanged(text: CharSequence?, start: Int, count: Int, after: Int) {

        if (internalEdit) return

        beforeText = when (text) { null -> "" else -> text }
    }

    override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {

        // Not used here; logic handled in afterTextChanged
    }

    override fun afterTextChanged(etvContentSpannable: Editable?) {

        if (internalEdit) return

        if (etvContentSpannable == null) return

        val cursor = editTextView.selectionStart
        if (cursor < 0) return

        val (lineStart, lineEnd) = TextFormatterHelper.getCurrentLineIndices(editTextView)

        var imageSpans =
            checkLineForImages(etvContentSpannable, lineStart, lineEnd)

        if (imageSpans.isNotEmpty()) {

//            val justTyped =
//                if (cursor > 0 && cursor <= etvContentSpannable.length)
//                    etvContentSpannable.subSequence(cursor - 1, cursor)
//                else ""

            // only one image allowed per line
            val imageBitmap = imageSpans[0].getBitmap()

            internalEdit = true

            if (cursor > 0) {

                etvContentSpannable.delete(cursor - 1, cursor)
                editTextView.setSelection(lineEnd.coerceAtMost(etvContentSpannable.length))
            }

            imageSpans = checkLineForImages(etvContentSpannable, lineStart, lineEnd)

            // if image spans is empty, means the user just deleted the image.
            // possibility for this be committed as an REMOVE action with bitmap
            // for accurate tracking here

            internalEdit = false

            // Jump to next line
            editTextView.setSelection((lineEnd).coerceAtMost(etvContentSpannable.length))

            TextFormatterHelper.fixLineHeight(editTextView)
        }
    }

    private fun checkLineForImages(etvContentSpannable: Editable, lineStart: Int,
                                   lineEnd: Int) : Array<CustomImageSpan> {

        return etvContentSpannable.getSpans(lineStart, lineEnd, CustomImageSpan::class.java)
    }

}
