package com.ckestudios.lumonote.data.models

// Stores all the data associated with a tag element object
data class Tag(

    val tagID: Int,

    val tagName: String
) : Item() {
    override val ID: Int
        get() = tagID
}
