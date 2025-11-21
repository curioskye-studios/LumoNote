package com.ckestudios.lumonote.data.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class TaggedDatabaseHelper(
    private val taggedTableName: String,
    private val taggedTagIDColumn: String,
    private val taggedNoteIDColumn: String
) {

    // Safely insert a tag-note relation
    fun insertTagged(tagID: Int, noteID: Int, db: SQLiteDatabase) {

        try {

            val values = ContentValues().apply {
                put(taggedTagIDColumn, tagID)
                put(taggedNoteIDColumn, noteID)
            }

            db.insert(taggedTableName, null, values)
        }

        catch (e: Exception) {
            Log.e("TaggedDatabaseHelper", "Error inserting tagged record", e)
        }
    }

    // Safely delete a tag-note relation
    fun deleteTagged(tagID: Int, noteID: Int, db: SQLiteDatabase) {

        try {

            val whereClause = "$taggedTagIDColumn = ? AND $taggedNoteIDColumn = ?"
            val whereArgs = arrayOf(tagID.toString(), noteID.toString())

            db.delete(taggedTableName, whereClause, whereArgs)
        }

        catch (e: Exception) {
            Log.e("TaggedDatabaseHelper", "Error deleting tagged record", e)
        }
    }

    fun deleteTaggedByTagID(tagID: Int, db: SQLiteDatabase) {

        try {

            val whereClause = "$taggedTagIDColumn = ?"
            val whereArgs = arrayOf(tagID.toString())

            db.delete(taggedTableName, whereClause, whereArgs)
        }

        catch (e: Exception) {
            Log.e("TaggedDatabaseHelper", "Error deleting tagged record", e)
        }
    }

    fun deleteTaggedByNoteID(noteID: Int, db: SQLiteDatabase) {

        try {

            val whereClause = "$taggedNoteIDColumn = ?"
            val whereArgs = arrayOf(noteID.toString())

            db.delete(taggedTableName, whereClause, whereArgs)
        }

        catch (e: Exception) {
            Log.e("TaggedDatabaseHelper", "Error deleting tagged record", e)
        }
    }

    // Get all tag IDs linked to a specific note
    fun getTagsByNoteID(noteID: Int, db: SQLiteDatabase): List<Int> {

        val tagIDList = mutableListOf<Int>()
        var cursor: android.database.Cursor? = null

        try {

            val query = "SELECT $taggedTagIDColumn FROM $taggedTableName WHERE $taggedNoteIDColumn = ?"
            cursor = db.rawQuery(query, arrayOf(noteID.toString()))

            if (cursor.moveToFirst()) {

                do {

                    val tagID = cursor.getInt(cursor.getColumnIndexOrThrow(taggedTagIDColumn))
                    tagIDList.add(tagID)

                } while (cursor.moveToNext())
            }
        }

        catch (e: Exception) {
            Log.e("TaggedDatabaseHelper", "Error fetching tags by note ID", e)
        }

        finally {

            try {
                cursor?.close()
            } catch (e: Exception) {
                Log.e("TaggedDatabaseHelper", "Error closing cursor in getTagsByNoteID", e)
            }
        }

        return tagIDList
    }


    // Get all note IDs linked to a specific tag
    fun getNotesByTagID(tagID: Int, db: SQLiteDatabase): List<Int> {

        val noteIDList = mutableListOf<Int>()
        var cursor: android.database.Cursor? = null

        try {

            val query = "SELECT $taggedNoteIDColumn FROM $taggedTableName WHERE $taggedTagIDColumn = ?"
            cursor = db.rawQuery(query, arrayOf(tagID.toString()))

            if (cursor.moveToFirst()) {

                do {

                    val noteID = cursor.getInt(cursor.getColumnIndexOrThrow(taggedNoteIDColumn))
                    noteIDList.add(noteID)

                } while (cursor.moveToNext())
            }
        }

        catch (e: Exception) {
            Log.e("TaggedDatabaseHelper", "Error fetching notes by tag ID", e)
        }

        finally {

            try {
                cursor?.close()
            } catch (e: Exception) {
                Log.e("TaggedDatabaseHelper", "Error closing cursor in getNotesByTagID", e)
            }
        }

        return noteIDList
    }


}
