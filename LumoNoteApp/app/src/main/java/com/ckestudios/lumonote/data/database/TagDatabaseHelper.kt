package com.ckestudios.lumonote.data.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ckestudios.lumonote.data.models.Tag

class TagDatabaseHelper(
    private val tagTableName: String,
    private val tagIDColumn: String,
    private val tagNameColumn: String
) {

    // Handles insertion of new tags into the database
    fun insertTag(tag: Tag, db: SQLiteDatabase) {

        val values = ContentValues().apply {
            put(tagNameColumn, tag.tagName)
        }

        try {
            db.insertOrThrow(tagTableName, null, values)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    // Retrieve all tags in the database safely
    fun getAllTags(db: SQLiteDatabase): List<Tag> {

        val tagsList = mutableListOf<Tag>()
        var cursor: Cursor? = null

        try {
            val query = "SELECT * FROM $tagTableName"
            cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {

                 do {

                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(tagIDColumn))
                    val name = cursor.getString(cursor.getColumnIndexOrThrow(tagNameColumn))

                    tagsList.add(Tag(id, name))

                } while (cursor.moveToNext())

            }

        }

        catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        return tagsList
    }

    // Retrieve last inserted tag safely
    fun getLastInsertedTag(db: SQLiteDatabase): Tag? {

        var tag: Tag? = null
        var cursor: Cursor? = null

        try {
            val query = "SELECT * FROM $tagTableName ORDER BY $tagIDColumn DESC LIMIT 1"
            cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {

                val id = cursor.getInt(cursor.getColumnIndexOrThrow(tagIDColumn))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(tagNameColumn))

                tag = Tag(id, name)
            }
        }

        catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        return tag
    }

    // Update a tag in the database safely
    fun updateTag(tag: Tag, db: SQLiteDatabase) {

        val values = ContentValues().apply {
            put(tagNameColumn, tag.tagName)
        }

        val whereClause = "$tagIDColumn = ?"
        val whereArgs = arrayOf(tag.tagID.toString())

        try {
            db.update(tagTableName, values, whereClause, whereArgs)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Get a specific tag by ID safely (returns null if not found)
    fun getTagByID(tagID: Int, db: SQLiteDatabase): Tag? {

        var tag: Tag? = null
        var cursor: Cursor? = null

        try {
            val query = "SELECT * FROM $tagTableName WHERE $tagIDColumn = ?"
            cursor = db.rawQuery(query, arrayOf(tagID.toString()))

            if (cursor.moveToFirst()) {

                val id = cursor.getInt(cursor.getColumnIndexOrThrow(tagIDColumn))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(tagNameColumn))

                tag = Tag(id, name)
            }
        }

        catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        return tag
    }

    // Remove a specific tag safely
    fun deleteTag(tagID: Int, db: SQLiteDatabase) {

        val whereClause = "$tagIDColumn = ?"
        val whereArgs = arrayOf(tagID.toString())

        try {
            db.delete(tagTableName, whereClause, whereArgs)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
