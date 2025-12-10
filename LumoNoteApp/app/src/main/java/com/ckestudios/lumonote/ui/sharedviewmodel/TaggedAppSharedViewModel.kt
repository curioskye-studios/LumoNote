package com.ckestudios.lumonote.ui.sharedviewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.data.models.Tag
import com.ckestudios.lumonote.data.repository.TaggedRepository

class TaggedAppSharedViewModel(application: Application, private val taggedRepository: TaggedRepository)
    : AndroidViewModel(application) {

    private val _notifyRefresh = MutableLiveData<Boolean>()
    val notifyRefresh: LiveData<Boolean> = _notifyRefresh


    fun insertTagged(tagID: Int, noteID: Int) {

        taggedRepository.insertTagged(tagID, noteID)

        updateNotifyRefresh()
    }

    fun deleteTagged(tagID: Int, noteID: Int) {

        taggedRepository.deleteTagged(tagID, noteID)

        updateNotifyRefresh()
    }

    fun getTagsByNoteID(noteID: Int): List<Tag> {

        return taggedRepository.getTagsByNoteID(noteID)
    }

    fun getNotesByTagID(tagID: Int): List<Note> {

        return taggedRepository.getNotesByTagID(tagID)
    }



    private fun updateNotifyRefresh() {

        setNotifyRefresh(true)
        setNotifyRefresh(false)
    }
    private fun setNotifyRefresh(shouldRefresh: Boolean) {

        _notifyRefresh.value = shouldRefresh
    }

}