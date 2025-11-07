package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.widget.EditText

interface RichTextFormatter<T> {

    val editTextView: EditText
    var etvSpannableContent: Editable
    val isActiveEditing: Boolean

    fun updateSpannableContent()

    fun processFormatting(selectStart: Int, selectEnd: Int)

    fun getSelectionSpans(selectStart: Int, selectEnd: Int): Array<T>

    fun applyFormatting(start: Int, end: Int)

    fun removeFormatting(selectStart: Int, selectEnd: Int, spansList: Array<T>)

    fun normalizeFormatting()

    // Check characters in a given range and return whether they all have the desired span
    fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int): Boolean?


}