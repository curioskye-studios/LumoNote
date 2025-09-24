package com.ckestudios.lumonote.ui.noteview.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EditInputViewModel : ViewModel() {

    private val _textFormatBtnIsActive = MutableLiveData(false)
    val textFormatBtnIsActive: LiveData<Boolean> get() = _textFormatBtnIsActive
    private val _imageBtnIsActive = MutableLiveData(false)
    val imageBtnIsActive: LiveData<Boolean> get() = _imageBtnIsActive
    private val _checklistBtnIsActive = MutableLiveData(false)
    val checklistBtnIsActive: LiveData<Boolean> get() = _checklistBtnIsActive


    fun setTextFormatBtnActive(isActive: Boolean) {


        _textFormatBtnIsActive.value = isActive
    }

    fun setImageBtnIsActive(isActive: Boolean) {

        _imageBtnIsActive.value = isActive
    }

    fun setChecklistBtnActive(isActive: Boolean) {

        _checklistBtnIsActive.value = isActive
    }
}