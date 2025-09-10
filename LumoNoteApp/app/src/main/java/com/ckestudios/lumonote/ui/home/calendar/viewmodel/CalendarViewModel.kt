package com.ckestudios.lumonote.ui.home.calendar.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class CalendarViewModel : ViewModel() {

    private val _selectedDate = MutableLiveData<LocalDate>()
    val selectedDate: LiveData<LocalDate> get() = _selectedDate

    private val _dateHasNotes = MutableLiveData(true)
    val dateHasNotes: LiveData<Boolean> get() = _dateHasNotes


    fun setSelectedDate(date: LocalDate) {

        _selectedDate.value = date
    }

    fun setDateHasNotes(hasNotes: Boolean) {

        _dateHasNotes.value = hasNotes
    }
}