package com.curioskyestudios.lumonote.ui.noteview.viewmodel

import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.curioskyestudios.lumonote.utils.texthelper.TextBulletHelper
import com.curioskyestudios.lumonote.utils.texthelper.TextSizeHelper
import com.curioskyestudios.lumonote.utils.texthelper.TextStyleHelper

class InputViewModel : ViewModel() {

    private val _textStyleHelper
    = MutableLiveData<TextStyleHelper>()
    val textStyleHelper: LiveData<TextStyleHelper> get() = _textStyleHelper

    private val _textSizeHelper = MutableLiveData<TextSizeHelper>()
    val textSizeHelper: LiveData<TextSizeHelper> get() = _textSizeHelper

    private val _textBulletHelper = MutableLiveData<TextBulletHelper>()
    val textBulletHelper: LiveData<TextBulletHelper> get() = _textBulletHelper

    // LiveData to track if EditText is focused
    private val _isEditing = MutableLiveData(false)
    val isEditing: LiveData<Boolean> get() = _isEditing

    // LiveData to track if TextFormatter should be visible
    private val _openFormatter = MutableLiveData(false)
    val openFormatter: LiveData<Boolean> get() = _openFormatter


    private val _styleSpans = MutableLiveData<Array<out StyleSpan>?>()
    val styleSpans: LiveData<Array<out StyleSpan>?> get() = _styleSpans
    private val _underlineSpans = MutableLiveData<Array<out TextStyleHelper.CustomUnderlineSpan>?>()
    val underlineSpans: LiveData<Array<out TextStyleHelper.CustomUnderlineSpan>?> get() = _underlineSpans
    private val _relativeSizeSpans = MutableLiveData<Array<out RelativeSizeSpan>?>()
    val relativeSizeSpans: LiveData<Array<out RelativeSizeSpan>?> get() = _relativeSizeSpans

    private val _selectionStart = MutableLiveData<Int>()
    val selectionStart: LiveData<Int> get() = _selectionStart
    private val _selectionEnd = MutableLiveData<Int>()
    val selectionEnd: LiveData<Int> get() = _selectionEnd



    fun setTextStyleHelper(textStyleHelper: TextStyleHelper) {

        _textStyleHelper.value = textStyleHelper
    }

    fun setTextSizeHelper(textSizeHelper: TextSizeHelper) {

        _textSizeHelper.value = textSizeHelper
    }

    fun setTextBulletHelper(textBulletHelper: TextBulletHelper) {

        _textBulletHelper.value = textBulletHelper
    }

    // Call this when EditText gains/loses focus
    fun setEditing(focused: Boolean) {

        _isEditing.value = focused
    }
    fun setOpenFormatter(open: Boolean) {

        _openFormatter.value = open
    }

    fun setStyleSpans(styleSpans: Array<out StyleSpan>?) {

        _styleSpans.value = styleSpans
    }
    fun setUnderlineSpans(underlineSpans: Array<out TextStyleHelper.CustomUnderlineSpan>?) {

        _underlineSpans.value = underlineSpans
    }
    fun setRelativeSizeSpans(relativeSizeSpans: Array<out RelativeSizeSpan>?) {

        _relativeSizeSpans.value = relativeSizeSpans
    }

    fun setSelectionStart(selectionStart: Int) {

        _selectionStart.value = selectionStart
    }
    fun setSelectionEnd(selectionEnd: Int) {

        _selectionEnd.value = selectionEnd
    }
}