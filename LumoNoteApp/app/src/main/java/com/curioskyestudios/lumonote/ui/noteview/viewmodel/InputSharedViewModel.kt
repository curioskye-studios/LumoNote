package com.curioskyestudios.lumonote.ui.noteview.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InputSharedViewModel : ViewModel() {

    // LiveData to track if TextFormatter should be visible
    private val _shouldOpenFormatter = MutableLiveData(false)
    val shouldOpenFormatter: LiveData<Boolean> get() = _shouldOpenFormatter

    private val _noteContentIsEditing = MutableLiveData(false)
    val noteContentIsEditing: LiveData<Boolean> get() = _noteContentIsEditing


    fun setShouldOpenFormatter(open: Boolean) {

        _shouldOpenFormatter.value = open
    }

    fun setNoteContentIsEditing(focused: Boolean) {

        _noteContentIsEditing.value = focused
    }
}