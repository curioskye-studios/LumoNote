package com.ckestudios.lumonote.ui.sharedviewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.data.repository.NoteRepository
import com.ckestudios.lumonote.utils.basichelpers.BasicUtilityHelper
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date


// This lives as long as the Application
class NoteAppSharedViewModel(application: Application, private val noteRepository: NoteRepository)
    : AndroidViewModel(application) {

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes
    private val _notesOnDate = MutableLiveData<List<Note>>()
    val notesOnDate: LiveData<List<Note>> = _notesOnDate
    private val _notifyRefresh = MutableLiveData<Boolean>()
    val notifyRefresh: LiveData<Boolean> = _notifyRefresh

    private val _isNewNote = MutableLiveData<Boolean>()
    val isNewNote: LiveData<Boolean> = _isNewNote

    private val _noteWasCreated = MutableLiveData<Boolean>()
    val noteWasCreated: LiveData<Boolean> = _noteWasCreated
    private val _noteWasUpdated = MutableLiveData<Boolean>()
    val noteWasUpdated: LiveData<Boolean> = _noteWasUpdated
    private val _noteWasDeleted= MutableLiveData<Boolean>()
    val noteWasDeleted: LiveData<Boolean> = _noteWasDeleted

    private val _currentNotePinned = MutableLiveData<Boolean>()
    val currentNotePinned: LiveData<Boolean> = _currentNotePinned

    private val _previewNotePinned = MutableLiveData<Boolean>()
    val previewNotePinned: LiveData<Boolean> = _previewNotePinned
    private val _currentPreviewNoteID = MutableLiveData(-1)
    val currentPreviewNoteID: LiveData<Int> = _currentPreviewNoteID


    init {

        loadAllNotes()

        val currentDateAsLocalDate = BasicUtilityHelper.convertDateToLocalDate(Date())
        loadAllNotesOnDate(currentDateAsLocalDate)
    }

    fun loadAllNotes() {

        viewModelScope.launch {
            _notes.value = noteRepository.getItems()
        }
    }

    fun loadAllNotesOnDate(date: LocalDate) {

        viewModelScope.launch {
            _notesOnDate.value = noteRepository.getNotesByDate(date.toString())
        }
    }

    fun setNotifyRefresh(shouldRefresh: Boolean) {
        _notifyRefresh.value = shouldRefresh
    }

    fun getNote(noteID: Int) : Note {
        // Call database helper object and invoke get note method to pull note from database
        return noteRepository.getItemByID(noteID)
    }

    fun deleteNote(noteID: Int) {
        // Call database helper object and invoke delete note method w/ note id
        noteRepository.deleteItem(noteID)

        // notify activity
        setNoteWasDeleted(true)

        // reset value
        setNoteWasDeleted(false)
    }
    fun saveNote(note: Note) {

        if (isNewNote.value == true) {

            Log.d("noteDataDate", note.noteModifiedDate)

            // Call database helper object and invoke note insertion method w/ new note
            noteRepository.insertItem(note)

            setNoteWasCreated(true)

        } else {

            // Call database helper object and invoke note update method w/ updated note
            noteRepository.updateItem(note)

            setNoteWasUpdated(true)
        }
    }

    fun updateCurrentPinStatus(isPinned: Boolean) {

        _currentNotePinned.value = isPinned
    }

    fun updatePreviewPinStatus(isPinned: Boolean) {

        _previewNotePinned.value = isPinned
    }

    fun setCurrentPreviewNoteID(noteID: Int) {
        _currentPreviewNoteID.value = noteID
    }



    fun setIsNewNote(isNew: Boolean) {
        _isNewNote.value = isNew
    }

    private fun setNoteWasCreated(flag: Boolean) {
        _noteWasCreated.value = flag
        _noteWasUpdated.value = !flag
    }

    private fun setNoteWasUpdated(flag: Boolean) {
        _noteWasUpdated.value = flag
        _noteWasCreated.value = !flag
    }

    private fun setNoteWasDeleted(flag: Boolean){
        _noteWasDeleted.value = flag
    }

}