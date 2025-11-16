package com.ckestudios.lumonote.data.repository

import android.content.Context
import com.ckestudios.lumonote.data.database.DatabaseHelper
import com.ckestudios.lumonote.data.models.Item
import com.ckestudios.lumonote.data.models.Note

class NoteRepository (context: Context) : Repository {

    private val dbConnection = DatabaseHelper(context)

    override fun getItems(): List<Note> {

        return dbConnection.getAllNotes()
    }

    override fun getItemByID(noteID: Int): Note? {

        return dbConnection.getNoteByID(noteID)
    }

    override fun insertItem(item: Item) {

        if (item is Note) {

            dbConnection.insertNote(item)
        }
    }

    override fun updateItem(item: Item) {

        if (item is Note) {

            dbConnection.updateNote(item)
        }
    }

    override fun deleteItem(itemID: Int) {

        dbConnection.deleteNote(itemID)
    }

    fun getNotesByDate(date: String): List<Note> {

        return dbConnection.getNotesByDate(date)
    }

}