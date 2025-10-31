package com.ckestudios.lumonote.data.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

class TaggedDatabaseHelper(
    private val taggedTableName: String,
    private val taggedTagIDColumn: String,
    private val taggedNoteIDColumn: String
) {

    fun insertTagged(tagID: Int, noteID: Int, db: SQLiteDatabase) {

        // ContentValues class stores values associated with column names
        val values = ContentValues().apply {

            // ID not needed since sqlite provides unique ids
            put(taggedTagIDColumn, tagID)
            put(taggedNoteIDColumn, noteID)
        }

        db.insert(taggedTableName, null, values)
        db.close()
    }

    fun getTagsByNoteID(noteID: Int, db: SQLiteDatabase) : List<Int> {

        val tagIDList = mutableListOf<Int>()

        val query = "SELECT * FROM $taggedTableName WHERE $taggedNoteIDColumn = $noteID"
        val cursor = db.rawQuery(query, null)

        /*
            Move the cursor to the next row. This method will return false if the
            cursor is already past the last entry in the result set.
         */
        while (cursor.moveToNext()) {

            val tagID = cursor.getInt(cursor.getColumnIndexOrThrow(taggedTagIDColumn))

            tagIDList.add(tagID)
        }

        cursor.close()
        db.close()

        return tagIDList
    }

    fun deleteTagged(tagID: Int, noteID: Int, db: SQLiteDatabase) {

        val whereClause = "$taggedTagIDColumn = ? AND $taggedNoteIDColumn = ?"
        val whereArgs = arrayOf(tagID.toString(), noteID.toString())

        db.delete(taggedTableName, whereClause, whereArgs)
        db.close()
    }
}