package com.example.lumonote.ui.noteview.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lumonote.data.database.DatabaseHelper
import com.example.lumonote.data.models.Note
import kotlinx.coroutines.launch

class NoteViewModel(private val dbConnection: DatabaseHelper) : ViewModel() {

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    init {

        loadAllNotes()
    }

    fun loadAllNotes() {

        viewModelScope.launch {
            _notes.value = dbConnection.getAllNotes()
        }
    }
}