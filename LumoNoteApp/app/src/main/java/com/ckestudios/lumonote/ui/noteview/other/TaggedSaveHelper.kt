package com.ckestudios.lumonote.ui.noteview.other

import com.ckestudios.lumonote.data.models.Tag
import com.ckestudios.lumonote.ui.sharedviewmodel.TaggedAppSharedViewModel

class TaggedSaveHelper(private val taggedAppSharedViewModel: TaggedAppSharedViewModel) {

    fun commitNoteTags(newTags: List<Tag>, noteID: Int) {

        val oldTags = taggedAppSharedViewModel.getTagsByNoteID(noteID)

//        Log.d("TagDebug", "oldTags: ${oldTags}.")
//        Log.d("TagDebug", "newTags: ${newTags}.")

        if (newTags == oldTags) return

        updateAddedTags(newTags, oldTags, noteID)
        updateRemovedTags(newTags, oldTags, noteID)
    }
    
    private fun updateAddedTags(newTags: List<Tag>, oldTags: List<Tag>, noteID: Int) {

        for (tag in newTags) {

            val tagExists = tag in oldTags

            if (!tagExists || oldTags.isEmpty()) {

//                Log.d("TagDebug", "${tag.tagName} added.")
                taggedAppSharedViewModel.insertTagged(tag.tagID, noteID)
            }
        }
    }

    private fun updateRemovedTags(newTags: List<Tag>, oldTags: List<Tag>, noteID: Int) {

        for (tag in oldTags) {

            val tagWasRemoved = tag !in newTags

            if (tagWasRemoved || newTags.isEmpty()) {

//                Log.d("TagDebug", "${tag.tagName} removed.")
                taggedAppSharedViewModel.deleteTagged(tag.tagID, noteID)
            }
        }
    }


}