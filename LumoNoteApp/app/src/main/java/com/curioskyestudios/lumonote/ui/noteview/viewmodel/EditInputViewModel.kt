package com.curioskyestudios.lumonote.ui.noteview.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditInputViewModel : ViewModel() {

    private val _textFormatBtnActive = MutableLiveData(false)
    val textFormatBtnActive: LiveData<Boolean> get() = _textFormatBtnActive
    private val _colorBtnActive = MutableLiveData(false)
    val colorBtnActive: LiveData<Boolean> get() = _colorBtnActive
    private val _checklistBtnActive = MutableLiveData(false)
    val checklistBtnActive: LiveData<Boolean> get() = _checklistBtnActive


    fun setTextFormatBtnActive(isActive: Boolean) {

        _textFormatBtnActive.value = isActive
    }

    fun setColorBtnActive(isActive: Boolean) {

        _colorBtnActive.value = isActive
    }

    fun setChecklistBtnActive(isActive: Boolean) {

        _checklistBtnActive.value = isActive
    }
}