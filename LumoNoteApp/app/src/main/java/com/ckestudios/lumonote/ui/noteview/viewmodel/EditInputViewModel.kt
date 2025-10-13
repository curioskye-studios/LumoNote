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

    private val _undoBtnIsActive = MutableLiveData(false)
    val undoBtnIsActive: LiveData<Boolean> get() = _undoBtnIsActive

    private val _redoBtnIsActive = MutableLiveData(false)
    val redoBtnIsActive: LiveData<Boolean> get() = _redoBtnIsActive


    fun setTextFormatBtnActive(isActive: Boolean) {


        _textFormatBtnIsActive.value = isActive
    }

    fun setImageBtnIsActive(isActive: Boolean) {

        _imageBtnIsActive.value = isActive
    }

    fun setChecklistBtnActive(isActive: Boolean) {

        _checklistBtnIsActive.value = isActive
    }

    fun setUndoBtnActive(isActive: Boolean) {

        _undoBtnIsActive.value = isActive
    }

    fun setRedoBtnActive(isActive: Boolean) {

        _redoBtnIsActive.value = isActive
    }
}