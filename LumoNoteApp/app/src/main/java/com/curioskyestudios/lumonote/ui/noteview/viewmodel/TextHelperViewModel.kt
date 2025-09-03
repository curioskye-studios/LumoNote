package com.curioskyestudios.lumonote.ui.noteview.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.curioskyestudios.lumonote.utils.texthelper.TextBulletHelper
import com.curioskyestudios.lumonote.utils.texthelper.TextSizeHelper
import com.curioskyestudios.lumonote.utils.texthelper.TextStyleHelper

class TextHelperViewModel : ViewModel() {

    private val _textStyleHelper = MutableLiveData<TextStyleHelper>()
    val textStyleHelper: LiveData<TextStyleHelper> get() = _textStyleHelper

    private val _textSizeHelper = MutableLiveData<TextSizeHelper>()
    val textSizeHelper: LiveData<TextSizeHelper> get() = _textSizeHelper

    private val _textBulletHelper = MutableLiveData<TextBulletHelper>()
    val textBulletHelper: LiveData<TextBulletHelper> get() = _textBulletHelper


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