package com.ckestudios.lumonote.utils.edittexthelper

import android.graphics.Typeface
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.EditText
import androidx.core.text.getSpans
import com.ckestudios.lumonote.data.models.TextSize
import com.ckestudios.lumonote.data.models.TextStyle

class TextSpanChecker (private val editTextView: EditText) {

    private var selectionStart: Int = 0
    private var selectionEnd: Int = 0

    private lateinit var selected: String


    init {

        updateSelectedText()
    }


    fun setSelection(newSelectionStart: Int, newSelectionEnd: Int) {

        selectionStart = newSelectionStart
        selectionEnd = newSelectionEnd

        updateSelectedText()
    }


    private fun updateSelectedText() {

        selected = editTextView.text?.substring(selectionStart, selectionEnd).toString()
    }


    fun getTextSizingType() : TextSize {

        val relativeSizeSpans =
            editTextView.text?.getSpans(selectionStart,
                selectionEnd, RelativeSizeSpan::class.java)

        Log.d("relativeSizeSpan", relativeSizeSpans?.contentToString() ?: "null")


        if (relativeSizeSpans?.isNotEmpty() == true) {

            if (relativeSizeSpans[0].sizeChange == TextSize.H1.scaleFactor){

                return TextSize.H1
            }
            else if (relativeSizeSpans[0].sizeChange == TextSize.H2.scaleFactor){

                return TextSize.H2
            }
        }

        return TextSize.NORMAL
    }


    fun getTextStylePresentValues(textStyleHelper: TextStyleHelper) : MutableMap<TextStyle, Boolean> {

        val styleIsPresentValues =
            mutableMapOf(TextStyle.BOLD to false, TextStyle.ITALICS to false,
                TextStyle.UNDERLINE to false)

        val styleSpans =
            editTextView.text?.getSpans<StyleSpan>(selectionStart, selectionEnd)

        val underlineSpans = editTextView.text?.
        getSpans<TextStyleHelper.CustomUnderlineSpan>(selectionStart, selectionEnd)

        Log.d("styleSpans", styleSpans?.contentToString() ?: "null")
        Log.d("underlineSpans", underlineSpans?.contentToString() ?: "null")

        if (styleSpans?.isNotEmpty() == true) {

            if (textStyleHelper.isAllSpanned(TextStyle.BOLD) ||
                (styleSpans.any { it.style == Typeface.BOLD } &&
                        selectionStart == selectionEnd)) {

                styleIsPresentValues[TextStyle.BOLD] = true
            }

            if (textStyleHelper.isAllSpanned(TextStyle.ITALICS) ||
                (styleSpans.any { it.style == Typeface.ITALIC } &&
                        selectionStart == selectionEnd)) {

                styleIsPresentValues[TextStyle.ITALICS] = true
            }
        }

        if (underlineSpans?.isNotEmpty() == true) {

            if (textStyleHelper.isAllSpanned(TextStyle.UNDERLINE) ||
                (underlineSpans.isNotEmpty() &&
                        selectionStart == selectionEnd)) {

                styleIsPresentValues[TextStyle.UNDERLINE] = true
            }
        }


        return styleIsPresentValues
    }

}