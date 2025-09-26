package com.ckestudios.lumonote.utils.state

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.ckestudios.lumonote.utils.textformatting.TextFormatHelper


class StateTextWatcher(private val editTextView: EditText) : TextWatcher {

    // Internal flag to prevent recursive edits
    private var internalEdit = false

    // Backup of text before a change
    private var beforeText: CharSequence = ""

    private val textFormatHelper = TextFormatHelper()
    private val stateManager = StateManager(editTextView)


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

        //val (lineStart, lineEnd) = textFormatHelper.getCurrentLineIndices(editTextView)

        val imageSpans =


//        if (imageSpans.isNotEmpty()) {
//
//        }

        textFormatHelper.fixLineHeight(editTextView)

        // compare the previous and current text for changes
        // compare the previous and current spans for changes
        // if the same, no changes, if different identify the difference and add it to the state stack
        // log spans added using textformatters, including when cleaning up
    }

//    fun updateSpans(etvContentSpannable: Editable) {
//
//        currentBoldSpans =
//            etvContentSpannable.getSpans(0, etvContentSpannable.length, StyleSpan::class.java)
//                .filter { it.style == Typeface.BOLD }
//                .toTypedArray()
//
//        currentItalicsSpans =
//            etvContentSpannable.getSpans(0, etvContentSpannable.length, StyleSpan::class.java)
//                .filter { it.style == Typeface.ITALIC }
//                .toTypedArray()
//
//        currentUnderlineSpans =
//            etvContentSpannable.getSpans(0, etvContentSpannable.length,
//                UnderlineTextFormatter.CustomUnderlineSpan::class.java)
//
//        currentBulletSpans =
//            etvContentSpannable.getSpans(0, etvContentSpannable.length,
//                CustomBulletSpan::class.java)
//
//        currentImageSpans =
//            etvContentSpannable.getSpans(0, etvContentSpannable.length,
//                CustomImageSpan::class.java)
//
//        currStrikeThruSpans =
//            etvContentSpannable.getSpans(0, etvContentSpannable.length,
//                StrikethroughSpan::class.java)
//
//        currCheckedColorSpans =
//            etvContentSpannable.getSpans(0, etvContentSpannable.length,
//                ForegroundColorSpan::class.java)
//
//        currCheckedItalicsSpans =
//            etvContentSpannable.getSpans(0, etvContentSpannable.length,
//                CustomItalicsSpan::class.java)
//
//    }
//
//    fun compareSpans() {
//
//        if (previousBoldSpans != null) {
//
//            //compare old and new
//        } else {
//
//            // make current the new previous
//            previousBoldSpans =  currentBoldSpans
//        }
//
//        if (previousItalicsSpans != null) {
//
//            //compare old and new
//        }
//
//        if (previousUnderlineSpans != null) {
//
//            //compare old and new
//        }
//
//        if (previousBulletSpans != null) {
//
//            //compare old and new
//        }
//
//        if (previousImageSpans!= null) {
//
//            //compare old and new
//        }
//
//        if (prevStrikethruSpans != null) {
//
//            //compare old and new
//        }
//
//        if (prevCheckedColorSpans != null) {
//
//            //compare old and new
//        }
//
//        if (prevCheckedItalicsSpans != null) {
//
//            //compare old and new
//        }
//    }

    fun compareSpanArray(firstArray: Array<Any>, secondArray: Array<Any>) {

        // use contentEquals and sortedArray if needed
    }

}
