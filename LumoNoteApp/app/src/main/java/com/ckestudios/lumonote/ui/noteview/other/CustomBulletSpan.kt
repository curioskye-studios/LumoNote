package com.ckestudios.lumonote.ui.noteview.other

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.text.Layout
import android.text.style.LeadingMarginSpan
import com.ckestudios.lumonote.data.models.BulletType

class CustomBulletSpan(private val gapWidth: Int, private val bulletRadius: Float,
                       private val bulletType: BulletType, bullet: String?)
    : LeadingMarginSpan {

    private var customBullet: String? = null


    init {

        if (bullet != null) {

            customBullet = bullet
        }
    }

    fun getBulletType() : BulletType { return bulletType }
    fun getCustomBullet() : String? { return customBullet }


    // Determines the space taken by the bullet
    override fun getLeadingMargin(first: Boolean): Int {

        return if (customBullet == null) {

            (2 * bulletRadius + gapWidth).toInt()
        } else {

            // Get the text width in pixels
            val paint = Paint().apply {
                textSize = 50f
                typeface = Typeface.DEFAULT_BOLD
            }
            val textWidth = paint.measureText(customBullet)

            (2 * bulletRadius + textWidth).toInt()
        }
    }


    // Draw the bullet on the canvas
    override fun drawLeadingMargin(c: Canvas, p: Paint, x: Int, dir: Int,
                                   top: Int, baseline: Int, bottom: Int,
                                   text: CharSequence, start: Int, end: Int,
                                   first: Boolean, layout: Layout) {

        if (first) {

            // Get font metrics to calculate vertical centering
            val fm = p.fontMetrics
            val centerY = baseline + (fm.ascent + fm.descent) / 2f

            when (bulletType) {

                BulletType.DEFAULT -> {

                    drawCircleBullet(c, p, x + dir * bulletRadius, centerY, bulletRadius)
                }

                BulletType.CUSTOM -> {

                    if (customBullet != null) {

                        // Calculate baseline to vertically center custom text
                        val textHeight = fm.descent - fm.ascent
                        val textBaseline = centerY + textHeight / 2f - fm.descent

                        c.drawText(customBullet!!, x.toFloat(), textBaseline, p)
                    } else {

                        drawCircleBullet(c, p, x + dir * bulletRadius, centerY, bulletRadius)
                    }
                }

            }

        }
    }

    private fun drawCircleBullet(c: Canvas, p: Paint, cx: Float, cy: Float,
                                 radiusFloat: Float) {

        // Save current paint style
        val style = p.style
        p.style = Paint.Style.FILL

        // Draw circle centered vertically
        c.drawCircle(cx, cy, radiusFloat, p)
        p.style = style
    }

}

