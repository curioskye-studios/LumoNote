package com.ckestudios.lumonote.ui.home.notepreview.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.data.models.Tag

class NotePrevViewModel : ViewModel() {

    private val _currentSelectedTag = MutableLiveData<Tag>(null)
    val currentSelectedTag: LiveData<Tag> get() = _currentSelectedTag

    private val _noNotes = MutableLiveData(false)
    val noNotes: LiveData<Boolean> = _noNotes

    private val _noPinnedNotes = MutableLiveData(false)
    val noPinnedNotes: LiveData<Boolean> = _noPinnedNotes

    private val _noUnpinnedNotes = MutableLiveData(false)
    val noUnpinnedNotes: LiveData<Boolean> = _noUnpinnedNotes


    fun setCurrentSelectedTag(tag: Tag?) {

        _currentSelectedTag.value = tag
    }

    fun updateNoNotesFlag(notesToAssess: List<Note>) {

        _noNotes.value = notesToAssess.isEmpty()
    }

    fun updateNoPinnedNotesFlag(notesToAssess: List<Note>) {

        _noPinnedNotes.value = notesToAssess.isEmpty()
    }

    fun updateNoUnpinnedNotesFlag(notesToAssess: List<Note>) {

        _noUnpinnedNotes.value = notesToAssess.isEmpty()
    }

}