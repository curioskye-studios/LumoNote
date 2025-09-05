package com.curioskyestudios.lumonote.ui.noteview.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InputSharedViewModel : ViewModel() {

    // LiveData to track if TextFormatter should be visible
    private val _openFormatter = MutableLiveData(false)
    val openFormatter: LiveData<Boolean> get() = _openFormatter


    fun setOpenFormatter(open: Boolean) {

        _openFormatter.value = open
    }
}