package com.ckestudios.lumonote.ui.noteview.other

import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.MetricAffectingSpan

class CustomItalicsSpan : MetricAffectingSpan() {

    override fun updateDrawState(tp: TextPaint) {
        tp.typeface = Typeface.create(tp.typeface, Typeface.ITALIC)
    }

    override fun updateMeasureState(tp: TextPaint) {
        tp.typeface = Typeface.create(tp.typeface, Typeface.ITALIC)
    }
}
