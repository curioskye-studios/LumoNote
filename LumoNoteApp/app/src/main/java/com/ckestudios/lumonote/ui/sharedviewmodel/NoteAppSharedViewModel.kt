package com.ckestudios.lumonote.ui.sharedviewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.data.repository.NoteRepository
import com.ckestudios.lumonote.utils.basichelpers.GeneralDateHelper
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

    private val _deleteNoteConfirmed = MutableLiveData(false)
    val deleteNoteConfirmed: LiveData<Boolean> = _deleteNoteConfirmed

    private val _notifyRefresh = MutableLiveData<Boolean>()
    val notifyRefresh: LiveData<Boolean> = _notifyRefresh

    private val _isNewNote = MutableLiveData<Boolean>()
    val isNewNote: LiveData<Boolean> = _isNewNote

    private val _noteWasCreated = MutableLiveData<Boolean>()
    val noteWasCreated: LiveData<Boolean> = _noteWasCreated
    private val _noteWasUpdated = MutableLiveData<Boolean>()
    val noteWasUpdated: LiveData<Boolean> = _noteWasUpdated
    private val _noteWasDeleted = MutableLiveData<Boolean>()
    val noteWasDeleted: LiveData<Boolean> = _noteWasDeleted

    private val _currentNotePinned = MutableLiveData<Boolean>()
    val currentNotePinned: LiveData<Boolean> = _currentNotePinned

    private val _previewNotePinned = MutableLiveData<Boolean>()
    val previewNotePinned: LiveData<Boolean> = _previewNotePinned
    private val _currentPreviewNoteID = MutableLiveData(-1)
    val currentPreviewNoteID: LiveData<Int> = _currentPreviewNoteID

    private val _currentOpenNoteID = MutableLiveData(-1)
    val currentOpenNoteID: LiveData<Int> = _currentOpenNoteID


    init {

        loadAllNotes()

        val currentDateAsLocalDate = GeneralDateHelper.convertDateToLocalDate(Date())
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

    fun setDeleteNoteConfirmed(shouldDelete: Boolean) {
        _deleteNoteConfirmed.value = shouldDelete
    }

    fun getNote(noteID: Int) : Note? {

        return noteRepository.getItemByID(noteID)
    }

    fun deleteNote(noteID: Int) {

        noteRepository.deleteItem(noteID)

        // notify activity & reset
        setNoteWasDeleted(true)
        setNoteWasDeleted(false)
    }

    fun saveNote(note: Note) {

        if (isNewNote.value == true) {

            Log.d("noteDataDate", note.noteModifiedDate)

            noteRepository.insertItem(note)

            setNoteWasCreated(true)

        } else {

            noteRepository.updateItem(note)

            setNoteWasUpdated(true)
        }
    }

    fun getNotesByPinnedStatus(getUnpinned: Boolean): List<Note> {

        return noteRepository.getNotesByPinnedStatus(getUnpinned)
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

    fun setCurrentOpenNoteID(noteID: Int) {
        _currentOpenNoteID.value = noteID
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