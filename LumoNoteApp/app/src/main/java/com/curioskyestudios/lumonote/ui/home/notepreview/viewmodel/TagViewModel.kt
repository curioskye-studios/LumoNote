package com.curioskyestudios.lumonote.ui.home.notepreview.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.curioskyestudios.lumonote.data.database.DatabaseHelper
import com.curioskyestudios.lumonote.data.models.Tag
import kotlinx.coroutines.launch

class TagViewModel(private val dbConnection: DatabaseHelper) : ViewModel() {

    private val _tags = MutableLiveData<List<Tag>>()
    val tags: LiveData<List<Tag>> = _tags

    // Track the currently highlighted/selected item, by default first item is highlighted
    private val _selectedTagPosition = MutableLiveData<Int>().apply {
        value = 0
    }
    val selectedTagPosition: LiveData<Int> = _selectedTagPosition

    init {

//        dbConnection.insertTag(Tag(1, "All Notes"))
//        dbConnection.insertTag(Tag(2, "School"))
//        dbConnection.insertTag(Tag(3, "Work"))
//        dbConnection.insertTag(Tag(4, "Korean"))
//        dbConnection.insertTag(Tag(5, "Japanese"))
//        dbConnection.insertTag(Tag(6, "Italian"))
        loadAllTags()
    }

    private fun loadAllTags() {

        viewModelScope.launch {
            _tags.value = dbConnection.getAllTags()
        }
    }

    fun setCurrentTagPosition(newPosition: Int) {

        _selectedTagPosition.value = newPosition
    }

}