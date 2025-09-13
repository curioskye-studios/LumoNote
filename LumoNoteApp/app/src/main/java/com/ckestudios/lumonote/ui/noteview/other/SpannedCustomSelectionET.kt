package com.ckestudios.lumonote.ui.noteview.other

import android.content.Context
import android.util.AttributeSet
import com.ckestudios.lumonote.utils.edittexthelper.TextBulletHelper
import com.ckestudios.lumonote.utils.edittexthelper.TextSizeHelper
import com.ckestudios.lumonote.utils.edittexthelper.TextSpanChecker
import com.ckestudios.lumonote.utils.edittexthelper.TextStyleHelper
import com.ckestudios.lumonote.utils.textformatting.BasicTextFormatter

class SpannedCustomSelectionET(context: Context, attrs: AttributeSet?)
    : CustomSelectionET(context, attrs) {

    private val textStyleHelper = TextStyleHelper(this)
    private val textSizeHelper = TextSizeHelper(this)
    private val textBulletHelper = TextBulletHelper(this)
    private val textSpanChecker = TextSpanChecker(this)
    private lateinit var basicTextFormatter: BasicTextFormatter

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

    fun setBasicTextFormatter(textFormatter: BasicTextFormatter) {

        basicTextFormatter = textFormatter
    }

    fun getBasicTextFormatter() : BasicTextFormatter {

        return basicTextFormatter
    }

}
