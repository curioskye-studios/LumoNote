package com.ckestudios.lumonote.data.database

import android.content.Context
import com.ckestudios.lumonote.data.models.Item
import com.ckestudios.lumonote.data.models.Tag

class TagRepository (private val context: Context) : Repository {

    private val dbConnection = DatabaseHelper(context)

    init {

        try {

            dbConnection.insertTag(Tag(1, "All Notes"))
            dbConnection.insertTag(Tag(2, "School"))
            dbConnection.insertTag(Tag(3, "Work"))
            dbConnection.insertTag(Tag(4, "Korean"))
            dbConnection.insertTag(Tag(5, "Japanese"))
            dbConnection.insertTag(Tag(6, "Italian"))
        }
        catch (e: Exception) {}
    }

    override fun getItems(): List<Item> {

        return dbConnection.getAllTags()
    }

    override fun getItemByID(itemID: Int): Item {

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

}