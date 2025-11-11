package com.ckestudios.lumonote.ui.sharedviewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ckestudios.lumonote.data.models.Tag
import com.ckestudios.lumonote.data.repository.TagRepository
import kotlinx.coroutines.launch

class TagAppSharedViewModel(application: Application, private val tagRepository: TagRepository)
    : AndroidViewModel(application) {

    private val _tags = MutableLiveData<List<Tag>>()
    val tags: LiveData<List<Tag>> = _tags

    private val _notifyRefresh = MutableLiveData<Boolean>()
    val notifyRefresh: LiveData<Boolean> = _notifyRefresh

    private val _tagWasCreated = MutableLiveData<Boolean>()
    val tagWasCreated: LiveData<Boolean> = _tagWasCreated
    private val _tagWasUpdated = MutableLiveData<Boolean>()
    val tagWasUpdated: LiveData<Boolean> = _tagWasUpdated
    private val _tagWasDeleted = MutableLiveData<Boolean>()
    val tagWasDeleted: LiveData<Boolean> = _tagWasDeleted
    private val _emptyTagDeleted = MutableLiveData(false)
    val emptyTagDeleted: LiveData<Boolean> = _emptyTagDeleted

    private val _noTagsCreated = MutableLiveData<Boolean>()
    val noTagsCreated: LiveData<Boolean> = _noTagsCreated

    // Track the currently highlighted/selected item, by default first item is highlighted
    private val _selectedNotePreviewTagPos = MutableLiveData<Int>().apply {
        value = 0
    }
    val selectedNotePreviewTagPos: LiveData<Int> = _selectedNotePreviewTagPos


    init {

        loadAllTags()
        deleteEmptyTags()
    }

    fun loadAllTags() {

        viewModelScope.launch {
            _tags.value = tagRepository.getItems() as List<Tag>
            setNoTagsCreated(tags.value?.size == 1)
        }
    }

    fun getTag(tagID: Int) : Tag {

        return tagRepository.getItemByID(tagID) as Tag
    }

    fun deleteTag(tagID: Int) {

        tagRepository.deleteItem(tagID)

        // notify activity
        setTagWasDeleted(true)
        // reset value
        setTagWasDeleted(false)

        updateNotifyRefresh()
    }

    fun createTag(tag: Tag) {

        tagRepository.insertItem(tag)

        setTagWasCreated(true)

        updateNotifyRefresh()
    }

    fun updateTag(tag: Tag) {

        tagRepository.updateItem(tag)
        setTagWasUpdated(true)

        updateNotifyRefresh()
    }

    fun deleteEmptyTags() {

        val tagList = tags.value as List<Tag>

        for (tag in tagList) {

            if (tag.tagName == "") {
                setEmptyTagDeleted(true)
                deleteTag(tag.tagID)
            }
        }
    }


    private fun updateNotifyRefresh() {

        setNotifyRefresh(true)
        setNotifyRefresh(false)
        setNoTagsCreated(tags.value?.size == 1)
    }
    private fun setNotifyRefresh(shouldRefresh: Boolean) {

        _notifyRefresh.value = shouldRefresh
    }

    fun setCurrentNotePreviewTagPos(newPosition: Int) {

        _selectedNotePreviewTagPos.value = newPosition
    }

    private fun setTagWasCreated(flag: Boolean) {
        _tagWasCreated.value = flag
        _tagWasUpdated.value = !flag
    }

    private fun setTagWasUpdated(flag: Boolean) {
        _tagWasUpdated.value = flag
        _tagWasCreated.value = !flag
    }

    private fun setTagWasDeleted(flag: Boolean){
        _tagWasDeleted.value = flag
    }

    private fun setNoTagsCreated(isTrue: Boolean){
        _noTagsCreated.value = isTrue
    }

    private fun setEmptyTagDeleted(isTrue: Boolean){
        _emptyTagDeleted.value = isTrue
    }

}