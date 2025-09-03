package com.curioskyestudios.lumonote.utils.texthelper

import android.graphics.Typeface
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.EditText
import androidx.core.text.getSpans
import com.curioskyestudios.lumonote.data.models.TextStyle

class TextStyleHelper (private val editTextView: EditText) {

    private val spanDataMap =  HashMap<Int, Triple<String,Int, Int>>()
    private var spanCounter = 0

    class CustomUnderlineSpan : UnderlineSpan()

    fun isAllSpanned(spanType: TextStyle): Boolean {

        Log.d("Text Span", "Point 1")

        val stringBuilder: Editable? = editTextView.text
        val selectionStart: Int = editTextView.selectionStart
        val selectionEnd: Int = editTextView.selectionEnd

        val spanCheck = mutableListOf<Boolean>()

        if (selectionStart != selectionEnd) { // Only if text is actually selected

            val selectedText = SpannableString(stringBuilder?.subSequence(selectionStart, selectionEnd))

            for (i in selectedText.indices) {

                val spans = selectedText.getSpans(i, i + 1, CharacterStyle::class.java)

                for (span in spans) {

                    Log.d("Spans", "Span class: ${span::class.java.name}")
                }


                for (span in spans) {

                    if (span is StyleSpan) {

                        val styleName = when (span.style) {
                            Typeface.NORMAL -> "NORMAL"
                            Typeface.BOLD -> TextStyle.BOLD.styleName
                            Typeface.ITALIC -> "ITALIC"
                            Typeface.BOLD_ITALIC -> "BOLD_ITALIC"
                            else -> "UNKNOWN"
                        }

                        Log.d("CharStyle StyleSpan", styleName)
                    }
                    else {

                        Log.d("CharStyle Span", "${span.javaClass.simpleName}")
                    }
                }

                when (spanType) {

                    TextStyle.BOLD, TextStyle.ITALICS -> {

                        var checkType  = if (spanType == TextStyle.BOLD) {

                            Typeface.BOLD
                        } else {

                            Typeface.ITALIC
                        }

                        if (spans.isNotEmpty() && spans.any { it is StyleSpan && it.style == checkType }) {

                            spanCheck.add(true)
                            Log.d("CharStyle", "Char '${selectedText[i]}' at $i has styles: ${spans.joinToString()}")
                        } else {

                            spanCheck.add(false)
                            Log.d("CharStyle","Char '${selectedText[i]}' at $i has no $spanType style")
                        }
                    }

                    TextStyle.UNDERLINE -> {

                        if (spans.isNotEmpty() && spans.any { it is CustomUnderlineSpan } ) {

                            spanCheck.add(true)
                            Log.d("CharStyle","Char '${selectedText[i]}' at $i has underline")
                        } else {

                            spanCheck.add(false)
                            Log.d("CharStyle","Char '${selectedText[i]}' at $i has no underline")
                        }

                    }

                }

            }
        }

        //Log.d("isAllSpanned", (spanCheck.all { it } && spanCheck.isNotEmpty()).toString())
        //Log.d("isAllSpanned", spanCheck.toTypedArray().contentToString())

        // check if the selected range has spans
        return spanCheck.all { it } && spanCheck.isNotEmpty()
    }


    fun clearTextStyles() {

        val stringBuilder: Editable? = editTextView.text
        val selectionStart: Int = editTextView.selectionStart
        val selectionEnd: Int = editTextView.selectionEnd

        val relevantSpans =
            stringBuilder?.getSpans<CharacterStyle>(selectionStart, selectionEnd)

        Log.d("Relevant Spans", relevantSpans.contentToString())

        if (relevantSpans != null) {

            for (span in relevantSpans) {

                if (span !is RelativeSizeSpan){

                    stringBuilder?.removeSpan(span)
                }
            }
            spanDataMap.clear()
        }

    }



    fun formatText(type: TextStyle) {

        val stringBuilder: Editable? = editTextView.text
        val selectionStart: Int = editTextView.selectionStart
        val selectionEnd: Int = editTextView.selectionEnd

        var setSpan: CharacterStyle? = null

        when (type) {

            TextStyle.BOLD -> setSpan = toggleBasicFormatting(type, Typeface.BOLD)
            TextStyle.ITALICS -> setSpan = toggleBasicFormatting(type, Typeface.ITALIC)

            TextStyle.UNDERLINE -> {

                val underlineSpans =
                    stringBuilder?.getSpans<CustomUnderlineSpan>(selectionStart, selectionEnd)

                // If selected text already has underline, remove it
                if (isAllSpanned(type)) {

                    if (underlineSpans != null) {

                        for (span in underlineSpans) {

                            stringBuilder?.removeSpan(span)
                        }
                    }
                } else {

                    // Otherwise add underline
                    setSpan = CustomUnderlineSpan()
                }
            }
        }


        // store in your map
        spanDataMap[spanCounter] = Triple(type.styleName, selectionStart, selectionEnd)
        spanCounter++

        if (setSpan == null) {

            editTextView.setSelection(selectionStart)
        }
        if (selectionStart != selectionEnd) {

            // User selected a range of text
            stringBuilder?.setSpan(
                setSpan,
                selectionStart,
                selectionEnd,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )

            removeUnintendedUnderlines(stringBuilder)

            editTextView.text = stringBuilder
            editTextView.setSelection(selectionStart, selectionEnd)

        }
    }



    fun formatBullet() {

    }




    private fun toggleBasicFormatting(type: TextStyle, typeface: Int)
        : CharacterStyle? {
        val stringBuilder: Editable? = editTextView.text
        val selectionStart: Int = editTextView.selectionStart
        val selectionEnd: Int = editTextView.selectionEnd

        val styleSpans = stringBuilder?.getSpans<StyleSpan>(selectionStart, selectionEnd)

        Log.d("togBasicFormat", styleSpans.contentToString())


        // check if all the selected range has the desired style spans
        if (isAllSpanned(type)) {

            // if there are pre-existing words set to that style span, remove it (bold or italics)
            if (styleSpans != null) {

                for (span in styleSpans) {

                    if (span.style == typeface) {

                        stringBuilder?.removeSpan(span)
                    }
                }
            }

            return null
        }
        else {

            // if not, add the text style to everything
            return StyleSpan(typeface)
        }

    }



    fun removeUnintendedUnderlines(editableText: Editable?) {

        if (editableText != null) {

            val allUnderlines = editableText.getSpans(0, editableText.length, UnderlineSpan::class.java)

            for (span in allUnderlines) {

                if (span !is CustomUnderlineSpan) {

                    editableText.removeSpan(span)
                }
            }

        }

    }


}