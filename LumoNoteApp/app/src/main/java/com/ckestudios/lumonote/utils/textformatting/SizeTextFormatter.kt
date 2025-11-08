package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.data.models.TextSize

class SizeTextFormatter(override val editTextView: EditText,
                        override val isActiveEditing: Boolean)
    : RichTextFormatter<RelativeSizeSpan> {

    override lateinit var etvSpannableContent: Editable
    private var sizeType: TextSize? = null


    override fun updateSpannableContent() {

        etvSpannableContent = editTextView.text
    }


    fun setSizeSpanType(sizeSpanType: TextSize) {

        sizeType = sizeSpanType
    }


    override fun processFormatting(selectStart: Int, selectEnd: Int) {

        updateSpannableContent()

        val relativeSizeSpans =
            getSelectionSpans(selectStart, selectEnd)

        val shouldApplyCheck = relativeSizeSpans.isEmpty()

        assessProcessMethod(relativeSizeSpans, shouldApplyCheck)

        TextFormatterHelper.fixLineHeight(editTextView)
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

            // include spans intersecting selection OR if selection is zero-length
            (selectStart <= end && selectEnd >= start) ||
                (selectStart == selectEnd && start == selectStart)
        }.toTypedArray()
    }


    private fun assessProcessMethod(spansList: Array<RelativeSizeSpan>, shouldApply: Boolean) {

        val paragraphIndices =
            TextFormatterHelper.getSelectionParagraphIndices(editTextView)

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

        updateSpannableContent()

        val safeEnd = if (start == end) end + 1 else end

        val relativeSpan= when (sizeType) {
            TextSize.H1 -> RelativeSizeSpan(TextSize.H1.scaleFactor)
            TextSize.H2 -> RelativeSizeSpan(TextSize.H2.scaleFactor)
            else -> null
        }

        if (relativeSpan != null) {

            etvSpannableContent.setSpan(
                relativeSpan,
                start,
                safeEnd,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
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

    fun isSelectionFullySpanned(textSize: TextSize, selectStart: Int, selectEnd: Int): Boolean {

        updateSpannableContent()

        val spanList = getDesiredSizeSpans(textSize)

        if (spanList.isNullOrEmpty()) return false

        val sizeSpansAtSelection = spanList.filter {

            val start = editTextView.text.getSpanStart(it)
            val end = editTextView.text.getSpanEnd(it)

            // include spans intersecting action range
            (selectStart <= end && selectEnd >= start)
        }.toTypedArray()

//        Log.d("SpanWatcher", sizeSpansAtSelection.size.toString())

        return sizeSpansAtSelection.isNotEmpty()
    }


    private fun getDesiredSizeSpans(textSize: TextSize)
            : Array<RelativeSizeSpan>? {

        val allSpans =
            etvSpannableContent.getSpans(0, editTextView.text.length,
                RelativeSizeSpan::class.java)

        return when (textSize) {

            TextSize.H1 -> allSpans.filter { it.sizeChange == 1.4f }.toTypedArray()

            TextSize.H2 -> allSpans.filter { it.sizeChange == 1.2f }.toTypedArray()

            else -> null
        }
    }

}