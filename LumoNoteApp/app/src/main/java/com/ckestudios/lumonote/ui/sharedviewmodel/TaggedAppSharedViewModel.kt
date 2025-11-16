package com.ckestudios.lumonote.ui.sharedviewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ckestudios.lumonote.data.models.Tag
import com.ckestudios.lumonote.data.repository.TaggedRepository
import kotlinx.coroutines.launch

class TaggedAppSharedViewModel(application: Application, private val taggedRepository: TaggedRepository)
    : AndroidViewModel(application) {

    private val _notifyRefresh = MutableLiveData<Boolean>()
    val notifyRefresh: LiveData<Boolean> = _notifyRefresh


    init {

        loadAllTags()
    }

    fun loadAllTags() {

        viewModelScope.launch {
        }
    }


    fun insertTagged(tagID: Int, noteID: Int) {

        taggedRepository.insertTagged(tagID, noteID)

        updateNotifyRefresh()
    }

    fun getTagsByNoteID(noteID: Int): List<Tag> {

        return taggedRepository.getTagsByNoteID(noteID)
    }

    fun deleteTagged(tagID: Int, noteID: Int) {

        return taggedRepository.deleteTagged(tagID, noteID)

        updateNotifyRefresh()
    }


    private fun updateNotifyRefresh() {

        setNotifyRefresh(true)
        setNotifyRefresh(false)
    }
    private fun setNotifyRefresh(shouldRefresh: Boolean) {

        _notifyRefresh.value = shouldRefresh
    }

}