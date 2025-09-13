package com.ckestudios.lumonote.utils.textformatting

import android.widget.EditText

interface RichTextFormatter {

    val editTextView: EditText

    fun normalizeFormatting()
}