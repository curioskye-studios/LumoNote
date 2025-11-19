package com.ckestudios.lumonote.data.repository

import android.content.Context
import com.ckestudios.lumonote.data.database.DatabaseHelper
import com.ckestudios.lumonote.data.models.Item
import com.ckestudios.lumonote.data.models.Tag

class TagRepository (private val context: Context) : Repository {

    private val dbConnection = DatabaseHelper(context)

    init {

        try {

            dbConnection.insertTag(Tag(1, "All Notes"))
        }

        catch (e: Exception) {}
    }

    override fun getItems(): List<Item> {

        return dbConnection.getAllTags()
    }

    override fun getItemByID(itemID: Int): Item? {

        return dbConnection.getTagByID(itemID)
    }

    override fun insertItem(item: Item) {

        if (item is Tag) {

            dbConnection.insertTag(item)
        }
    }

    override fun updateItem(item: Item) {

        if (item is Tag) {

            dbConnection.updateTag(item)
        }
    }

    override fun deleteItem(itemID: Int) {

        dbConnection.deleteTag(itemID)
    }

    fun getLastInsertedTag(): Tag? {

        return dbConnection.getLastInsertedTag()
    }

}