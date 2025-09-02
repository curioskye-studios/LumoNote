package com.curioskyestudios.lumonote.ui.noteview.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curioskyestudios.lumonote.data.database.DatabaseHelper
import com.curioskyestudios.lumonote.data.models.Note
import kotlinx.coroutines.launch

class NoteViewModel(private val dbConnection: DatabaseHelper) : ViewModel() {

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    private val _isNewNote = MutableLiveData<Boolean>()
    val isNewNote: LiveData<Boolean> = _isNewNote

    private val _noteWasCreated = MutableLiveData<Boolean>()
    val noteWasCreated: LiveData<Boolean> = _noteWasCreated
    private val _noteWasUpdated =  MutableLiveData<Boolean>()
    val noteWasUpdated: LiveData<Boolean> = _noteWasUpdated
    private val _noteWasDeleted= MutableLiveData<Boolean>()
    val noteWasDeleted: LiveData<Boolean> = _noteWasDeleted

    private val _currentNotePinned = MutableLiveData<Boolean>()
    val currentNotePinned: LiveData<Boolean> = _currentNotePinned

    init {

        loadAllNotes()
    }

    fun loadAllNotes() {

        viewModelScope.launch {
            _notes.value = dbConnection.getAllNotes()
        }
    }

    fun getNote(noteID: Int) : Note {
        // Call database helper object and invoke get note method to pull note from database
        return dbConnection.getNoteByID(noteID)
    }

    fun deleteNote(noteID: Int) {
        // Call database helper object and invoke delete note method w/ note id
        dbConnection.deleteNote(noteID)

        // notify activity
        setNoteWasDeleted(true)

        // reset value
        setNoteWasDeleted(false)
    }
    fun saveNote(note: Note) {

        if (isNewNote.value == true) {

            Log.d("noteDataDate", note.noteModifiedDate)

            // Call database helper object and invoke note insertion method w/ new note
            dbConnection.insertNote(note)

            setNoteWasCreated(true)
        } else {


            // Call database helper object and invoke note update method w/ updated note
            dbConnection.updateNote(note)

            setNoteWasUpdated(true)
        }
    }

    fun updateCurrentPinStatus(isPinned: Boolean) {

        _currentNotePinned.value = isPinned
    }



    fun setIsNewNote(isNew: Boolean) {
        _isNewNote.value = isNew
    }

    private fun setNoteWasCreated(flag: Boolean) {
        _noteWasCreated.value = flag
        _noteWasUpdated.value = !flag
    }
    private fun setNoteWasUpdated(flag: Boolean) {
        _noteWasUpdated.value =  flag
        _noteWasCreated.value = !flag
    }

    private fun setNoteWasDeleted(flag: Boolean){
        _noteWasDeleted.value = flag
    }
}