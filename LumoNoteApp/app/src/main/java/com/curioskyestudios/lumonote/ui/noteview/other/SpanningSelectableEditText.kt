package com.curioskyestudios.lumonote.ui.noteview.other

import android.content.Context
import android.util.AttributeSet
import com.curioskyestudios.lumonote.utils.edittexthelper.TextBulletHelper
import com.curioskyestudios.lumonote.utils.edittexthelper.TextSizeHelper
import com.curioskyestudios.lumonote.utils.edittexthelper.TextSpanChecker
import com.curioskyestudios.lumonote.utils.edittexthelper.TextStyleHelper

class SpanningSelectableEditText(context: Context, attrs: AttributeSet?) : SelectableEditText(context, attrs) {

    private val textStyleHelper = TextStyleHelper(this)
    private val textSizeHelper = TextSizeHelper(this)
    private val textBulletHelper = TextBulletHelper(this)
    private val textSpanChecker = TextSpanChecker(this)

    fun getStyleHelper() : TextStyleHelper {

        return textStyleHelper
    }

    fun getSizeHelper() : TextSizeHelper {

        return textSizeHelper
    }

    fun getBulletHelper() : TextBulletHelper {

        return textBulletHelper
    }

    fun getSpanChecker() : TextSpanChecker {

        return textSpanChecker
    }
}