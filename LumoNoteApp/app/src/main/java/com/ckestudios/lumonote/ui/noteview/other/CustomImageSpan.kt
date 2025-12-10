package com.ckestudios.lumonote.ui.noteview.other

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

/**
 * CustomImageSpan: embeds a Bitmap in EditText
 * - Removes extra padding above and below
 * - Line height exactly equals bitmap height
 */
class CustomImageSpan(
    private val bitmap: Bitmap
) : ReplacementSpan() {

    // Return the width of the bitmap
    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        // Override font metrics to tightly match bitmap
        fm?.let {
            // ascent = -bitmap.height → top of line matches bitmap top
            // descent = 0 → bottom of line matches bitmap bottom
            it.ascent = -bitmap.height
            it.top = it.ascent
            it.descent = 0
            it.bottom = 0
        }
        return bitmap.width
    }

    // Draw bitmap starting at the top of the line
    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        // Translate canvas to the top of the line
        canvas.save()
        canvas.translate(x, top.toFloat())
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        canvas.restore()
    }

    fun getBitmap(): Bitmap {

        return bitmap
    }
}
