package com.curioskyestudios.lumonote.ui.home.notepreview.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curioskyestudios.lumonote.data.database.DatabaseHelper
import com.curioskyestudios.lumonote.data.models.Note
import kotlinx.coroutines.launch

class NotePreviewViewModel(private val dbConnection: DatabaseHelper) : ViewModel() {

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    private val _isCurrentNotePinned = MutableLiveData<Boolean>()
    val isCurrentNotePinned: LiveData<Boolean> = _isCurrentNotePinned

    init {

        loadAllNotes()
    }

    fun loadAllNotes() {

        viewModelScope.launch {
            _notes.value = dbConnection.getAllNotes()
        }
    }

    fun updateIsCurrentNotePinned(isPinned: Boolean) {
        _isCurrentNotePinned.value = isPinned
    }
}