package com.ckestudios.lumonote.data.models

// Stores all the data associated with a note element object
data class Note (

    val noteID: Int,

    val noteTitle: String,
    val noteContent: String,
    val noteSpans: String,

    val noteCreatedDate: String,
    val noteModifiedDate: String,
    var notePinned: Boolean
) : Item() {
    override val ID: Int
        get() = noteID
}
