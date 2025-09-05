package com.curioskyestudios.lumonote.ui.noteview.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.curioskyestudios.lumonote.utils.edittexthelper.TextBulletHelper
import com.curioskyestudios.lumonote.utils.edittexthelper.TextSizeHelper
import com.curioskyestudios.lumonote.utils.edittexthelper.TextStyleHelper

class TextHelperSharedViewModel : ViewModel() {

    // LiveData to track if TextFormatter should be visible
    private val _openFormatter = MutableLiveData(false)
    val openFormatter: LiveData<Boolean> get() = _openFormatter


    private val _textStyleHelper = MutableLiveData<TextStyleHelper>()
    val textStyleHelper: LiveData<TextStyleHelper> get() = _textStyleHelper

    private val _textSizeHelper = MutableLiveData<TextSizeHelper>()
    val textSizeHelper: LiveData<TextSizeHelper> get() = _textSizeHelper

    private val _textBulletHelper = MutableLiveData<TextBulletHelper>()
    val textBulletHelper: LiveData<TextBulletHelper> get() = _textBulletHelper


    fun setOpenFormatter(open: Boolean) {

        _openFormatter.value = open
    }


    fun setTextStyleHelper(textStyleHelper: TextStyleHelper) {

        _textStyleHelper.value = textStyleHelper
    }

    fun setTextSizeHelper(textSizeHelper: TextSizeHelper) {

        _textSizeHelper.value = textSizeHelper
    }

    fun setTextBulletHelper(textBulletHelper: TextBulletHelper) {

        _textBulletHelper.value = textBulletHelper
    }

}