package com.ckestudios.lumonote.ui.noteview.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InputSharedViewModel : ViewModel() {

    // LiveData to track if TextFormatter should be visible
    private val _shouldOpenFormatter = MutableLiveData(false)
    val shouldOpenFormatter: LiveData<Boolean> get() = _shouldOpenFormatter

    private val _noteContentIsEditing = MutableLiveData(false)
    val noteContentIsEditing: LiveData<Boolean> get() = _noteContentIsEditing

    private val _isContentSelectionEmpty = MutableLiveData(false)
    val isContentSelectionEmpty: LiveData<Boolean> get() = _isContentSelectionEmpty

    private val _currentLineHasText = MutableLiveData(false)
    val currentLineHasText: LiveData<Boolean> get() = _currentLineHasText

    private val _currentLineHasImage = MutableLiveData(false)
    val currentLineHasImage: LiveData<Boolean> get() = _currentLineHasImage


    fun setShouldOpenFormatter(open: Boolean) {

        _shouldOpenFormatter.value = open
    }

    fun setNoteContentIsEditing(focused: Boolean) {

        _noteContentIsEditing.value = focused
    }

    fun setContentSelectionIsEmpty(isEmpty: Boolean) {

        _isContentSelectionEmpty.value = isEmpty
    }

    fun setCurrentLineHasText(hasText: Boolean) {

        _currentLineHasText.value = hasText
    }

    fun setCurrentLineHasImage(hasImage: Boolean) {

        _currentLineHasImage.value = hasImage
    }

}