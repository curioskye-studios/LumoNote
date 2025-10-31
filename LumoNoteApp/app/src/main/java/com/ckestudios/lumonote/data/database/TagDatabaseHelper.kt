package com.ckestudios.lumonote.data.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.ckestudios.lumonote.data.models.Tag

class TagDatabaseHelper(
    private val tagTableName: String,
    private val tagIDColumn: String,
    private val tagNameColumn: String
) {

    // Handles insertion of new tags into the database
    fun insertTag(tag: Tag, db: SQLiteDatabase) {

        // ContentValues class stores values associated with column names
        val values = ContentValues().apply {

            // ID not needed since sqlite provides unique ids
            put(tagNameColumn, tag.tagName)
        }

        db.insert(tagTableName, null, values)
        db.close()
    }


    // Retrieve all tags in the database
    fun getAllTags(db: SQLiteDatabase): List<Tag> {

        val tagsList = mutableListOf<Tag>()

        val query = "SELECT * FROM $tagTableName"
        val cursor = db.rawQuery(query, null)

        /*
            Move the cursor to the next row. This method will return false if the
            cursor is already past the last entry in the result set.
         */
        while (cursor.moveToNext()) {

            val id = cursor.getInt(cursor.getColumnIndexOrThrow(tagIDColumn))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(tagNameColumn))

            val tag = Tag(id, name)

            tagsList.add(tag)
        }

        cursor.close()
        db.close()

        return tagsList
    }


    // Update a tag in the database w/ the edited version
    fun updateTag(tag: Tag, db: SQLiteDatabase){

        val values = ContentValues().apply {

            put(tagNameColumn, tag.tagName)
        }

        val whereClause = "$tagIDColumn = ?"
        val whereArgs = arrayOf(tag.tagID.toString())

        db.update(tagTableName, values, whereClause, whereArgs)
        db.close()
    }


    // Get a specific tag from the database using its id
    fun getTagByID(tagID: Int, db: SQLiteDatabase): Tag {

        val query = "SELECT * FROM $tagTableName WHERE $tagIDColumn = $tagID"
        val cursor = db.rawQuery(query, null)

        /*
            Move the cursor to the first row. This method will return false
            if the cursor is empty.
            Returns: boolean - whether the move succeeded.
        */
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(tagIDColumn))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(tagNameColumn))

        cursor.close()
        db.close()

        return Tag(id, name)
    }


    // Remove a specific tag from the database using its id
    fun deleteTag(tagID: Int, db: SQLiteDatabase) {

        val whereClause = "$tagIDColumn = ?"
        val whereArgs = arrayOf(tagID.toString())

        db.delete(tagTableName, whereClause, whereArgs)
        db.close()
    }
}