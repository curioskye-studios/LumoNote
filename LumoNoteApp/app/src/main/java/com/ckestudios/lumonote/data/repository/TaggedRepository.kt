package com.ckestudios.lumonote.data.repository

import android.content.Context
import com.ckestudios.lumonote.data.database.DatabaseHelper
import com.ckestudios.lumonote.data.models.Item
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.data.models.Tag

class TaggedRepository (context: Context) : Repository{

    private val dbConnection = DatabaseHelper(context)

    fun insertTagged(tagID: Int, noteID: Int) {

        dbConnection.insertTagged(tagID, noteID)
    }

    fun deleteTagged(tagID: Int, noteID: Int) {

        return dbConnection.deleteTagged(tagID, noteID)
    }

    fun getTagsByNoteID(noteID: Int): List<Tag> {

        return dbConnection.getTagsByNoteID(noteID)
    }

    fun getNotesByTagID(tagID: Int): List<Note> {

        return dbConnection.getNotesByTagID(tagID)
    }


    override fun getItems(): List<Item> { return mutableListOf() }

    override fun getItemByID(itemID: Int): Item { return null as Item }

    override fun insertItem(item: Item) {}

    override fun updateItem(item: Item) {}

    override fun deleteItem(itemID: Int) {}

}