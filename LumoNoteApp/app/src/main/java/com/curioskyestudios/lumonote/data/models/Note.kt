package com.curioskyestudios.lumonote.data.models

// Stores all the data associated with a note element object
data class Note(
    val noteID: Int,

    val noteTitle: String,
    val noteContent: String,

    val noteCreatedDate: String,
    val noteModifiedDate: String,
    val notePinned: Boolean
)
