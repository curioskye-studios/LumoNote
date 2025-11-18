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
        } catch (e: Exception) {
            Log.e("TaggedDatabaseHelper", "Error inserting tagged record", e)
        } finally {
            try {
                db.close()
            } catch (e: Exception) {
                Log.e("TaggedDatabaseHelper", "Error closing DB after insert", e)
            }
        }
    }

    // Safely delete a tag-note relation
    fun deleteTagged(tagID: Int, noteID: Int, db: SQLiteDatabase) {
        try {
            val whereClause = "$taggedTagIDColumn = ? AND $taggedNoteIDColumn = ?"
            val whereArgs = arrayOf(tagID.toString(), noteID.toString())

            db.delete(taggedTableName, whereClause, whereArgs)
        } catch (e: Exception) {
            Log.e("TaggedDatabaseHelper", "Error deleting tagged record", e)
        } finally {
            try {
                db.close()
            } catch (e: Exception) {
                Log.e("TaggedDatabaseHelper", "Error closing DB after delete", e)
            }
        }
    }

    fun deleteTaggedByTagID(tagID: Int, db: SQLiteDatabase) {
        try {
            val whereClause = "$taggedTagIDColumn = ?"
            val whereArgs = arrayOf(tagID.toString())

            db.delete(taggedTableName, whereClause, whereArgs)
        } catch (e: Exception) {
            Log.e("TaggedDatabaseHelper", "Error deleting tagged record", e)
        } finally {
            try {
                db.close()
            } catch (e: Exception) {
                Log.e("TaggedDatabaseHelper", "Error closing DB after delete", e)
            }
        }
    }

    fun deleteTaggedByNoteID(noteID: Int, db: SQLiteDatabase) {
        try {
            val whereClause = "$taggedNoteIDColumn = ?"
            val whereArgs = arrayOf(noteID.toString())

            db.delete(taggedTableName, whereClause, whereArgs)
        } catch (e: Exception) {
            Log.e("TaggedDatabaseHelper", "Error deleting tagged record", e)
        } finally {
            try {
                db.close()
            } catch (e: Exception) {
                Log.e("TaggedDatabaseHelper", "Error closing DB after delete", e)
            }
        }
    }

    // Get all tag IDs linked to a specific note
    fun getTagsByNoteID(noteID: Int, db: SQLiteDatabase): List<Int> {
        val tagIDList = mutableListOf<Int>()
        var cursor: android.database.Cursor? = null

        try {
            val query = "SELECT $taggedTagIDColumn FROM $taggedTableName WHERE $taggedNoteIDColumn = ?"
            cursor = db.rawQuery(query, arrayOf(noteID.toString()))

            while (cursor.moveToNext()) {
                val tagID = cursor.getInt(cursor.getColumnIndexOrThrow(taggedTagIDColumn))
                tagIDList.add(tagID)
            }
        } catch (e: Exception) {
            Log.e("TaggedDatabaseHelper", "Error fetching tags by note ID", e)
        } finally {
            try {
                cursor?.close()
                db.close()
            } catch (e: Exception) {
                Log.e("TaggedDatabaseHelper", "Error closing DB or cursor in getTagsByNoteID", e)
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

            while (cursor.moveToNext()) {
                val noteID = cursor.getInt(cursor.getColumnIndexOrThrow(taggedNoteIDColumn))
                noteIDList.add(noteID)
            }
        } catch (e: Exception) {
            Log.e("TaggedDatabaseHelper", "Error fetching notes by tag ID", e)
        } finally {
            try {
                cursor?.close()
                db.close()
            } catch (e: Exception) {
                Log.e("TaggedDatabaseHelper", "Error closing DB or cursor in getNotesByTagID", e)
            }
        }

        return noteIDList
    }
}
