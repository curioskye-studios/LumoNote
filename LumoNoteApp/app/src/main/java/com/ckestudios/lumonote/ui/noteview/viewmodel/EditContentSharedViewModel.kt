package com.ckestudios.lumonote.ui.noteview.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import com.ckestudios.lumonote.utils.state.StateManager
import com.ckestudios.lumonote.utils.state.TextStateWatcher

class EditContentSharedViewModel : ViewModel() {

    private val _noteContentEditTextView = MutableLiveData<CustomSelectionET>()
    val noteContentEditTextView : LiveData<CustomSelectionET>
        get() = _noteContentEditTextView

    private val _noteContentStateManager = MutableLiveData<StateManager>()
    val noteContentStateManager : LiveData<StateManager>
        get() = _noteContentStateManager

    private val _noteContentTextStateWatcher = MutableLiveData<TextStateWatcher>()
    val noteContentTextStateWatcher : LiveData<TextStateWatcher>
        get() = _noteContentTextStateWatcher


    private val _isNormalSized = MutableLiveData(true)
    val isNormalSized: LiveData<Boolean> get() = _isNormalSized
    private val _isHeader1Sized = MutableLiveData(false)
    val isHeader1Sized: LiveData<Boolean> get() = _isHeader1Sized
    private val _isHeader2Sized = MutableLiveData(false)
    val isHeader2Sized: LiveData<Boolean> get() = _isHeader2Sized


    private val _isBold = MutableLiveData(false)
    val isBold: LiveData<Boolean> get() = _isBold
    private val _isItalics = MutableLiveData(false)
    val isItalics: LiveData<Boolean> get() = _isItalics
    private val _isUnderlined = MutableLiveData(false)
    val isUnderlined: LiveData<Boolean> get() = _isUnderlined

    private val _isBulleted = MutableLiveData(false)
    val isBulleted : LiveData<Boolean> get() = _isBulleted


    fun setNoteContentEditTextView(noteContentET: CustomSelectionET) {

        _noteContentEditTextView.value = noteContentET
    }

    fun setNoteContentStateManager(stateManager: StateManager) {

        _noteContentStateManager.value = stateManager
    }

    fun setNoteContentTextWatcher(textStateWatcher: TextStateWatcher) {

        _noteContentTextStateWatcher.value = textStateWatcher
    }


    fun setIsNormalSized(isNormal: Boolean) {

        _isNormalSized.value = isNormal
        _isHeader1Sized.value = !isNormal
        _isHeader2Sized.value = !isNormal
    }

    fun setIsHeader1Sized(isHeader1: Boolean) {

        _isHeader1Sized.value = isHeader1
        _isHeader2Sized.value = !isHeader1
        _isNormalSized.value = !isHeader1
    }

    fun setIsHeader2Sized(isHeader2: Boolean) {

        _isHeader2Sized.value = isHeader2
        _isHeader1Sized.value = !isHeader2
        _isNormalSized.value = !isHeader2
    }


    fun setIsBold(isFullyBold: Boolean) {

        _isBold.value = isFullyBold
    }

    fun setIsItalics(isFullyItalics: Boolean) {

        _isItalics.value = isFullyItalics
    }

    fun setIsUnderlined(isFullyUnderlined: Boolean) {

        _isUnderlined.value = isFullyUnderlined
    }


    fun setIsBulleted(isBulleted: Boolean) {

        _isBulleted.value = isBulleted
    }

}