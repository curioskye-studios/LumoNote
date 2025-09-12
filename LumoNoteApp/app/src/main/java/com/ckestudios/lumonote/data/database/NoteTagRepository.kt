package com.ckestudios.lumonote.data.database

import android.content.Context
import com.ckestudios.lumonote.data.models.Item

class NoteTagRepository (context: Context) : Repository {

    private val dbConnection = DatabaseHelper(context)
    override fun getItems(): List<Item> {
        TODO("Not yet implemented")
    }

    override fun getItemByID(itemID: Int): Item {
        TODO("Not yet implemented")
    }

    override fun insertItem(item: Item) {
        TODO("Not yet implemented")
    }

    override fun updateItem(item: Item) {
        TODO("Not yet implemented")
    }

    override fun deleteItem(itemID: Int) {
        TODO("Not yet implemented")
    }

//    fun getNotesByTag(tag: Tag): List<Note> {
//
//        return dbConnection.getNotesByTag(tag)
//    }

}