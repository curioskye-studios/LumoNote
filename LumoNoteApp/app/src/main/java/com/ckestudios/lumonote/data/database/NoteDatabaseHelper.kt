package com.ckestudios.lumonote.data.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.ckestudios.lumonote.data.models.Note

class NoteDatabaseHelper (
    private val noteTableName: String,
    private val noteIDColumn: String,
    private val noteTitleColumn: String,
    private val noteContentColumn: String,
    private val noteCreatedColumn: String,
    private val noteModifiedColumn: String,
    private val notePinnedColumn: String
) {

    // Handles insertion of new notes into the database
    fun insertNote(note: Note, db: SQLiteDatabase) {

        // ContentValues class stores values associated with column names
        val values = ContentValues().apply {

            // ID not needed since sqlite provides unique ids
            put(noteTitleColumn, note.noteTitle)
            put(noteContentColumn, note.noteContent)
            put(noteCreatedColumn, note.noteCreatedDate)
            put(noteModifiedColumn, note.noteModifiedDate)
            put(notePinnedColumn, note.notePinned.toString())
        }

        db.insert(noteTableName, null, values)
        db.close()
    }

    // Retrieve all notes in the database
    fun getAllNotes(db: SQLiteDatabase): List<Note> {

        val notesList = mutableListOf<Note>()

        val query = "SELECT * FROM ${noteTableName}"
        val cursor = db.rawQuery(query, null)

        /*
            Move the cursor to the next row. This method will return false if the
            cursor is already past the last entry in the result set.
         */
        while (cursor.moveToNext()) {

            // Collect each note record from the database
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(noteIDColumn))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(noteTitleColumn))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(noteContentColumn))
            val createdDate = cursor.getString(cursor.getColumnIndexOrThrow(noteCreatedColumn))
            val modifiedDate = cursor.getString(cursor.getColumnIndexOrThrow(noteModifiedColumn))
            val isPinned = cursor.getString(cursor.getColumnIndexOrThrow(notePinnedColumn))

            val note = Note(id, title, content, createdDate, modifiedDate, isPinned.toBoolean())

            notesList.add(note)
        }

        cursor.close()
        db.close()

        return notesList
    }

    fun getNotesByDate(date: String, db: SQLiteDatabase): List<Note> {

        val notesList = mutableListOf<Note>()

        // Query notes where created date matches the given date
        val query = "SELECT * FROM ${noteTableName} WHERE ${noteCreatedColumn} = ?"
        val cursor = db.rawQuery(query, arrayOf(date))

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(noteIDColumn))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(noteTitleColumn))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(noteContentColumn))
            val createdDate = cursor.getString(cursor.getColumnIndexOrThrow(noteCreatedColumn))
            val modifiedDate = cursor.getString(cursor.getColumnIndexOrThrow(noteModifiedColumn))
            val isPinned = cursor.getString(cursor.getColumnIndexOrThrow(notePinnedColumn))

            val note = Note(id, title, content, createdDate, modifiedDate, isPinned.toBoolean())
            notesList.add(note)
        }

        cursor.close()
        db.close()

        return notesList
    }


    // Update a note in the database w/ the edited version
    fun updateNote(note: Note, db: SQLiteDatabase){

        val values = ContentValues().apply {
            put(noteTitleColumn, note.noteTitle)
            put(noteContentColumn, note.noteContent)
            put(noteModifiedColumn, note.noteModifiedDate)
            put(notePinnedColumn, note.notePinned.toString())
        }

        val whereClause = "${noteIDColumn} = ?"
        val whereArgs = arrayOf(note.noteID.toString())

        db.update(noteTableName, values, whereClause, whereArgs)
        db.close()
    }


    // Get a specific note from the database using its id
    fun getNoteByID(noteID: Int, db: SQLiteDatabase): Note {

        val query = "SELECT * FROM ${noteTableName} WHERE ${noteIDColumn} = $noteID"
        val cursor = db.rawQuery(query, null)

        /*
            Move the cursor to the first row. This method will return false
            if the cursor is empty.
            Returns: boolean - whether the move succeeded.
        */
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(noteIDColumn))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(noteTitleColumn))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(noteContentColumn))
        val created = cursor.getString(cursor.getColumnIndexOrThrow(noteCreatedColumn))
        val modified = cursor.getString(cursor.getColumnIndexOrThrow(noteModifiedColumn))
        val pinned = cursor.getString(cursor.getColumnIndexOrThrow(notePinnedColumn))

        cursor.close()
        db.close()

//        Log.d("getNoteModData", modified.toString())
//
//        Log.d("getNoteData", pinned.toString())

        return Note(id, title, content, created, modified, pinned.toBoolean())
    }


    // Remove a specific note from the database using its id
    fun deleteNote(noteID: Int, db: SQLiteDatabase) {

        val whereClause = "${noteIDColumn} = ?"
        val whereArgs = arrayOf(noteID.toString())

        db.delete(noteTableName, whereClause, whereArgs)
        db.close()
    }

}