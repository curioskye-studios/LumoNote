package com.ckestudios.lumonote.ui.sharedviewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ckestudios.lumonote.data.repository.TagRepository
import com.ckestudios.lumonote.data.models.Tag
import kotlinx.coroutines.launch

class TagAppSharedViewModel(application: Application, private val tagRepository: TagRepository)
    : AndroidViewModel(application) {

    private val _tags = MutableLiveData<List<Tag>>()
    val tags: LiveData<List<Tag>> = _tags

    // Track the currently highlighted/selected item, by default first item is highlighted
    private val _selectedTagPosition = MutableLiveData<Int>().apply {
        value = 0
    }
    val selectedTagPosition: LiveData<Int> = _selectedTagPosition


    init {

        loadAllTags()
    }

    private fun loadAllTags() {

        viewModelScope.launch {
            _tags.value = tagRepository.getItems() as List<Tag>
        }
    }

    fun setCurrentTagPosition(newPosition: Int) {

        _selectedTagPosition.value = newPosition
    }

}