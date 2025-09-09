package com.ckestudios.lumonote.ui.noteview.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditInputViewModel : ViewModel() {

    private val _textFormatBtnIsActive = MutableLiveData(false)
    val textFormatBtnIsActive: LiveData<Boolean> get() = _textFormatBtnIsActive
    private val _colorBtnIsActive = MutableLiveData(false)
    val colorBtnIsActive: LiveData<Boolean> get() = _colorBtnIsActive
    private val _checklistBtnIsActive = MutableLiveData(false)
    val checklistBtnIsActive: LiveData<Boolean> get() = _checklistBtnIsActive


    fun setTextFormatBtnActive(isActive: Boolean) {


        _textFormatBtnIsActive.value = isActive
    }

    fun setColorBtnActive(isActive: Boolean) {

        _colorBtnIsActive.value = isActive
    }

    fun setChecklistBtnActive(isActive: Boolean) {

        _checklistBtnIsActive.value = isActive
    }
}