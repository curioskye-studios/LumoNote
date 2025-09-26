package com.ckestudios.lumonote.ui.noteview.other

import android.content.Context
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.UpdateAppearance
import androidx.core.content.ContextCompat
import com.ckestudios.lumonote.R

class ChecklistSpan(context: Context) : CharacterStyle(), UpdateAppearance {

    private val textColor: Int = ContextCompat.getColor(context, R.color.light_grey_2)

    override fun updateDrawState(tp: TextPaint) {
        // Apply strikethrough
        tp.isStrikeThruText = true

        // Apply italics
        tp.textSkewX = -0.25f  // negative = italic slant

        // Apply color
        tp.color = textColor
    }
}
