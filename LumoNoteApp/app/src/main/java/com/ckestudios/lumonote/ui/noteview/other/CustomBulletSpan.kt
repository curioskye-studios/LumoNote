package com.ckestudios.lumonote.ui.noteview.other

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.style.LeadingMarginSpan
import com.ckestudios.lumonote.data.models.BulletType

class CustomBulletSpan(
    private val gapWidth: Int,
    private val bulletRadius: Float
) : LeadingMarginSpan {

    private var bulletType: BulletType = BulletType.DEFAULT
    private var customBullet: String = ""
    private var numberBullet: Int = 0

    fun setBullet(newBulletType: BulletType, bullet: String?, number: Int?) {

        bulletType = newBulletType

        if (bullet != null) {

            customBullet = bullet
        }

        if (number != null) {

            numberBullet = number
        }
    }

    override fun getLeadingMargin(first: Boolean): Int {
        return (2 * bulletRadius + gapWidth).toInt()
    }

    override fun drawLeadingMargin(
        c: Canvas, p: Paint, x: Int, dir: Int,
        top: Int, baseline: Int, bottom: Int,
        text: CharSequence, start: Int, end: Int,
        first: Boolean, layout: Layout
    ) {


        if (first) {

            when (bulletType) {

                BulletType.DEFAULT -> {

                    val style = p.style
                    p.style = Paint.Style.FILL
                    c.drawCircle(x + dir * bulletRadius, (top + bottom) / 2f, bulletRadius, p)
                    p.style = style
                }

                BulletType.CUSTOM -> {

                    c.drawText(customBullet, x.toFloat(), baseline.toFloat(), p)
                }

                BulletType.NUMBERED -> {

                    c.drawText("$numberBullet", x.toFloat(), baseline.toFloat(), p)
                }
            }

        }
        //if (first) {
        //            c.drawText(emoji, x.toFloat(), baseline.toFloat(), p)
        //        }
    }
}
