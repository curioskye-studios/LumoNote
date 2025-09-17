package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.data.models.TextSize
import com.ckestudios.lumonote.utils.general.TextFormatHelper

class SizeTextFormatter(override val editTextView: EditText)
    : RichTextFormatter<RelativeSizeSpan> {

    override lateinit var etvSpannableContent: Editable
    private var sizeType: TextSize? = null
    private val textFormatHelper = TextFormatHelper()


    fun setSizeSpanType(sizeSpanType: TextSize) {

        sizeType = sizeSpanType
    }


    override fun processFormatting(selectStart: Int, selectEnd: Int) {

        updateSpannableContent()

        val relativeSizeSpans =
            getSelectionSpans(selectStart, selectEnd)

        if (relativeSizeSpans.isEmpty()) {

            assessProcessMethod(selectStart, selectEnd, relativeSizeSpans,
                true)
        } else {

           assessProcessMethod(selectStart, selectEnd, relativeSizeSpans,
               false)
        }
    }

    override fun getSelectionSpans(selectStart: Int, selectEnd: Int)
        : Array<RelativeSizeSpan> {

        val relativeSizeSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                RelativeSizeSpan::class.java)

        // Ensure only current paragraph
        return relativeSizeSpans.filter {

            val start = etvSpannableContent.getSpanStart(it)
            val end = etvSpannableContent.getSpanEnd(it)
            start < selectEnd && end > selectStart
        }.toTypedArray()
    }


    private fun assessProcessMethod(selectStart: Int, selectEnd: Int,
            spansList: Array<RelativeSizeSpan>, shouldApply: Boolean) {

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

        var relativeSpan= when (sizeType) {
            TextSize.H1 -> RelativeSizeSpan(TextSize.H1.scaleFactor)
            TextSize.H2 -> RelativeSizeSpan(TextSize.H2.scaleFactor)
            else -> null
        }

        if (relativeSpan != null) {

            etvSpannableContent.setSpan(
                relativeSpan,
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    override fun removeFormatting(selectStart: Int, selectEnd: Int,
                                  spansList: Array<RelativeSizeSpan>) {

        for (span in spansList) {

            etvSpannableContent.removeSpan(span)
        }

        if (sizeType == TextSize.H1 || sizeType == TextSize.H2) {

            applyFormatting(selectStart, selectEnd)
        }
    }

    override fun normalizeFormatting() {}

    override fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int): Boolean {

        updateSpannableContent()

        return getSelectionSpans(selectStart, selectEnd).isNotEmpty()
    }

}