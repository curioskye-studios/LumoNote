package com.ckestudios.lumonote.data.repository

import android.content.Context
import com.ckestudios.lumonote.data.database.DatabaseHelper
import com.ckestudios.lumonote.data.models.Tag

class TaggedRepository (context: Context){

    private val dbConnection = DatabaseHelper(context)

    fun insertTagged(tagID: Int, noteID: Int) {

        dbConnection.insertTagged(tagID, noteID)
    }

    fun getTagsByNoteID(noteID: Int): List<Tag> {

        return dbConnection.getTagsByNoteID(noteID)
    }

    fun deleteTagged(tagID: Int, noteID: Int) {

        return dbConnection.deleteTagged(tagID, noteID)
    }

}