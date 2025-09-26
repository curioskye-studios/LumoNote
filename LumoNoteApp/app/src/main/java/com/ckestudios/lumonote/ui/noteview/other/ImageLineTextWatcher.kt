package com.ckestudios.lumonote.ui.noteview.other

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.ckestudios.lumonote.utils.textformatting.TextFormatHelper


/**
 * TextWatcher that enforces rules for lines containing images:
 * - No typing directly on an image line (except ENTER to create a new line below).
 * - Keeps only one image per line.
 */
class ImageLineTextWatcher(private val editTextView: EditText) : TextWatcher {

    private val textFormatHelper = TextFormatHelper()

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

        val (lineStart, lineEnd) = textFormatHelper.getCurrentLineIndices(editTextView)

        val imageSpans =
            etvContentSpannable.getSpans(lineStart, lineEnd, CustomImageSpan::class.java)

        if (imageSpans.isNotEmpty()) {

//            val justTyped =
//                if (cursor > 0 && cursor <= etvContentSpannable.length)
//                    etvContentSpannable.subSequence(cursor - 1, cursor)
//                else ""

            internalEdit = true

            if (cursor > 0) {

                etvContentSpannable.delete(cursor - 1, cursor)
                editTextView.setSelection(lineEnd.coerceAtMost(etvContentSpannable.length))
            }

            internalEdit = false

            // Jump to next line
            editTextView.setSelection((lineEnd).coerceAtMost(etvContentSpannable.length))

            textFormatHelper.fixLineHeight(editTextView)
        }
    }

}
