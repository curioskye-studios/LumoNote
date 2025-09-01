package com.example.lumonote.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _notePreviewActive = MutableLiveData<Boolean>()
    val notePreviewActive: LiveData<Boolean> get() = _notePreviewActive
    private val _calendarActive = MutableLiveData<Boolean>()
    val calendarActive: LiveData<Boolean> get() = _calendarActive
    private val _settingsActive = MutableLiveData<Boolean>()
    val settingsActive: LiveData<Boolean> get() = _settingsActive


    fun setNotePreviewActive(isActive: Boolean) {

        _notePreviewActive.value = isActive
    }

    fun setCalendarActive(isActive: Boolean) {

        _calendarActive.value = isActive
    }

    fun setSettingsActive(isActive: Boolean) {

        _settingsActive.value = isActive
    }


}