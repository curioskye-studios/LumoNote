package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.style.RelativeSizeSpan
import android.widget.EditText
import com.ckestudios.lumonote.utils.helpers.TextFormatHelper

class BulletTextFormatter(override val editTextView: EditText)
    : RichTextFormatter<RelativeSizeSpan> {

    override lateinit var etvSpannableContent: Editable

    private val textFormatHelper = TextFormatHelper()


    override fun processFormatting(selectStart: Int, selectEnd: Int) {

    }

    override fun getSelectionSpans(selectStart: Int, selectEnd: Int): Array<RelativeSizeSpan> {
        return arrayOf()
    }

    override fun applyFormatting(start: Int, end: Int) {

    }

    override fun normalizeFormatting() {

    }

    override fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int): Boolean? {
        return null
    }

}