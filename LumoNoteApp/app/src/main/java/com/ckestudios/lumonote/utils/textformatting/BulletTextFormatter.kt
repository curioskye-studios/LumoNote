package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.Spanned
import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.data.models.BulletType
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletSpan
import com.ckestudios.lumonote.utils.helpers.TextFormatHelper

class BulletTextFormatter(override val editTextView: EditText)
    : RichTextFormatter<CustomBulletSpan> {

    override lateinit var etvSpannableContent: Editable

    private val textFormatHelper = TextFormatHelper()
    private var bulletType: BulletType? = null
    private var customBullet: String = ""
    private var numberBullet: Int = 0

    override fun updateSpannableContent() {

        etvSpannableContent = editTextView.text
    }

    fun setBulletType(newBulletType: BulletType, bullet: String?, number: Int?) {

        bulletType = newBulletType

        if (bullet != null) {

            customBullet = bullet
        }

        if (number != null) {

            numberBullet = number
        }
    }


    override fun processFormatting(selectStart: Int, selectEnd: Int) {

        updateSpannableContent()

        val bulletSpans =
            getSelectionSpans(selectStart, selectEnd)

        if (bulletSpans.isEmpty()) {

            assessProcessMethod(selectStart, selectEnd, bulletSpans,
                true)
        } else {

            assessProcessMethod(selectStart, selectEnd, bulletSpans,
                false)
        }

        fixLineHeight()
    }

    override fun getSelectionSpans(selectStart: Int, selectEnd: Int)
            : Array<CustomBulletSpan> {

        val bulletSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                CustomBulletSpan::class.java)

        // Ensure only current paragraph
        return bulletSpans.filter {

            val start = etvSpannableContent.getSpanStart(it)
            val end = etvSpannableContent.getSpanEnd(it)
            start < selectEnd && end > selectStart
        }.toTypedArray()
    }

    private fun assessProcessMethod(selectStart: Int, selectEnd: Int,
                                    spansList: Array<CustomBulletSpan>, shouldApply: Boolean) {

        //val items = listOf("First point", "Second point", "Third point")
        //val spannable = SpannableStringBuilder()
        //
        //items.forEachIndexed { index, item ->
        //    val start = spannable.length
        //    spannable.append(item).append("\n")
        //    spannable.setSpan(
        //        NumberedBulletSpan(index + 1),
        //        start,
        //        spannable.length,
        //        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        //    )
        //}
        //
        //textView.text = spannable

        val paragraphIndices =
            textFormatHelper.getSelectionParagraphIndices(selectStart, selectEnd,
                etvSpannableContent.length, etvSpannableContent.toString())

        if (paragraphIndices.size == 2) {

            if (shouldApply) {

                applyFormatting(paragraphIndices[0], paragraphIndices[1])
            } else {

                removeFormatting(paragraphIndices[0], paragraphIndices[1], spansList)
            }
        }

        else {

            for (index in paragraphIndices.indices){

                Log.d("sizetextformatter", "index: $index")

                if (index + 1 <= paragraphIndices.size - 1) {

                    val applyStart = paragraphIndices[index]
                    val applyEnd = paragraphIndices[index + 1]

                    Log.d("sizetextformatter", "applyStart: $applyStart")
                    Log.d("sizetextformatter", "applyEnd: $applyEnd")

                    if (shouldApply) {

                        applyFormatting(applyStart, applyEnd)
                    } else {

                        removeFormatting(applyStart, applyEnd, spansList)
                    }
                }
            }

        }

    }

    override fun applyFormatting(start: Int, end: Int) {

        var bulletSpan= when (bulletType) {
            BulletType.DEFAULT -> CustomBulletSpan(30, 6f)
            BulletType.CUSTOM -> CustomBulletSpan(30, 6f)
            else -> null
        }

        if (bulletSpan != null) {

            etvSpannableContent.setSpan(
                bulletSpan,
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    override fun removeFormatting(selectStart: Int, selectEnd: Int,
                                  spansList: Array<CustomBulletSpan>) {

        for (span in spansList) {

            etvSpannableContent.removeSpan(span)
        }

        if (bulletType == BulletType.DEFAULT || bulletType == BulletType.CUSTOM) {

            applyFormatting(selectStart, selectEnd)
        }
    }


    override fun normalizeFormatting() {

    }

    override fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int): Boolean? {
        return null
    }

}