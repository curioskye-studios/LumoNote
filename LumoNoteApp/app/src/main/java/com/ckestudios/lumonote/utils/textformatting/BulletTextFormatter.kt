package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.Spanned
import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.data.models.BulletType
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletSpan
import com.ckestudios.lumonote.utils.state.SpanStateWatcher

class BulletTextFormatter(override val editTextView: EditText)
    : RichTextFormatter<CustomBulletSpan> {

    override lateinit var etvSpannableContent: Editable

    private var bulletType: BulletType? = null
    private var customBullet: String? = null

    private val textFormatHelper = TextFormatHelper()
    private val spanStateManager = SpanStateWatcher(editTextView)

    override fun updateSpannableContent() {

        etvSpannableContent = editTextView.text
    }

    fun processAsDefaultBullet(selectStart: Int, selectEnd: Int) {

        bulletType = BulletType.DEFAULT

        customBullet = null

        processFormatting(selectStart, selectEnd)
    }

    fun processAsCustomBullet(selectStart: Int, selectEnd: Int, bullet: String) {

        bulletType = BulletType.CUSTOM

        customBullet = bullet

        processFormatting(selectStart, selectEnd)
    }


    override fun processFormatting(selectStart: Int, selectEnd: Int) {

        updateSpannableContent()

        val bulletSpans =
            getSelectionSpans(selectStart, selectEnd)

        Log.d("bullettextformatter", "bulletSpans.isEmpty():" +
                "${bulletSpans.isEmpty()}")

        if (bulletSpans.isEmpty()) {

            assessProcessMethod(selectStart, selectEnd)
        } else {

            assessProcessMethod(selectStart, selectEnd)
        }

        textFormatHelper.fixLineHeight(editTextView)

        normalizeFormatting()
    }

    override fun getSelectionSpans(selectStart: Int, selectEnd: Int)
            : Array<CustomBulletSpan> {

        val bulletSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                CustomBulletSpan::class.java)

        return bulletSpans.filter {
            val start = etvSpannableContent.getSpanStart(it)
            val end = etvSpannableContent.getSpanEnd(it)

            // include spans intersecting selection OR if selection is zero-length
            (selectStart <= end && selectEnd >= start) ||
                    (selectStart == selectEnd && start == selectStart)
        }.toTypedArray()
    }

    private fun assessProcessMethod(selectStart: Int, selectEnd: Int) {

        val paragraphIndices =
            textFormatHelper.getSelectionParagraphIndices(editTextView)

        for (index in 0 until paragraphIndices.size - 1) {

            val paraStart = paragraphIndices[index]
            val paraEnd = paragraphIndices[index + 1]

            // Get all bullet spans in this paragraph
            val paraSpans = etvSpannableContent.getSpans(
                paraStart, paraEnd, CustomBulletSpan::class.java)

            if (paraSpans.isEmpty()) {

                applyFormatting(paraStart, paraEnd)
            }

            else {

                var shouldApplyNew = true

                for (span in paraSpans) {

                    val sameBulletType =
                        span.getBulletType() == bulletType &&
                            (bulletType != BulletType.CUSTOM ||
                            span.getCustomBullet() == customBullet)

                    shouldApplyNew = if (sameBulletType) {

                        removeFormatting(selectStart, selectEnd, arrayOf(span))
                        false
                    } else {

                        removeFormatting(selectStart, selectEnd, arrayOf(span))
                        true
                    }
                }

                if (shouldApplyNew) {

                    applyFormatting(paraStart, paraEnd)
                }
            }

        }
    }




    override fun applyFormatting(start: Int, end: Int) {

        // Ensure end does not exceed text length
        val safeEnd = if (start >= etvSpannableContent.length) etvSpannableContent.length
        else if (start == end) end + 1
        else end

        val bulletSpan = when (bulletType) {

            BulletType.DEFAULT ->
                CustomBulletSpan(30, 6f, BulletType.DEFAULT, null)

            BulletType.CUSTOM ->
                CustomBulletSpan(30, 6f, BulletType.CUSTOM, customBullet)

            else -> null
        }

        if (bulletSpan != null) {

            etvSpannableContent.setSpan(
                bulletSpan,
                start,
                safeEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spanStateManager.addSpan(bulletSpan, SpanType.BULLET_SPAN)
        }

    }


    override fun removeFormatting(selectStart: Int, selectEnd: Int,
                                  spansList: Array<CustomBulletSpan>) {

        for (span in spansList) {

            spanStateManager.removeSpan(span, SpanType.BULLET_SPAN)

            etvSpannableContent.removeSpan(span)
        }
    }


    override fun normalizeFormatting() {

        val bulletSpans = getSelectionSpans(0,
            etvSpannableContent.length)

        for (span in bulletSpans) {

            val spanStart = etvSpannableContent.getSpanStart(span)
            val spanEnd = etvSpannableContent.getSpanEnd(span)

            if (spanStart == spanEnd) {

                etvSpannableContent.removeSpan(span)
            }
        }
    }


    override fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int): Boolean? {

        updateSpannableContent()

        val newLinePosBeforeSelection = etvSpannableContent.lastIndexOf("\n",
            selectStart - 1)

        val skipNewLineSpace = 1

        // Exclude newline char itself to indicate current line
        val safeStart =
            if (newLinePosBeforeSelection != -1) newLinePosBeforeSelection + skipNewLineSpace
            else 0

        val bulletedSpans =
            editTextView.text?.getSpans(safeStart, selectEnd,
                CustomBulletSpan::class.java)

        Log.d("bulletedSpans", bulletedSpans?.contentToString() ?: "null")


        if (!bulletedSpans.isNullOrEmpty()) {

            return true
        }

        return false
    }

}