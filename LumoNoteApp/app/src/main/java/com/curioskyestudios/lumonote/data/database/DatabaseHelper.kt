package com.curioskyestudios.lumonote.data.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.curioskyestudios.lumonote.data.models.Note
import com.curioskyestudios.lumonote.data.models.Tag


// This class handles all database operations needed for notes CRUD

// Inherits from SQLiteOpenHelper class to allow for sqlite database operates
class DatabaseHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        // Stores constants used for database access

        private const val DATABASE_NAME = "LumoNote.db"
        private const val DATABASE_VERSION = 1

        private const val NOTE_TABLE_NAME = "Notes"
        private const val NOTE_ID_COLUMN = "NoteID"
        private const val NOTE_TITLE_COLUMN = "NoteTitle"
        private const val NOTE_CONTENT_COLUMN = "NoteContent"
        private const val NOTE_CREATED_COLUMN = "NoteCreated"
        private const val NOTE_MODIFIED_COLUMN = "NoteModified"
        private const val NOTE_PINNED_COLUMN = "NotePinned"

        private const val TAG_TABLE_NAME = "Tags"
        private const val TAG_ID_COLUMN = "TagID"
        private const val TAG_NAME_COLUMN = "TagName"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Initializes table in database as well as each column (id, title, content)
        val createNoteTableQuery = "CREATE TABLE $NOTE_TABLE_NAME " +
                "($NOTE_ID_COLUMN INTEGER PRIMARY KEY, $NOTE_TITLE_COLUMN TEXT, $NOTE_CONTENT_COLUMN TEXT," +
                "$NOTE_CREATED_COLUMN, $NOTE_MODIFIED_COLUMN, $NOTE_PINNED_COLUMN)"

        // Initializes table in database as well as each column (id, name)
        val createTagTableQuery = "CREATE TABLE $TAG_TABLE_NAME " +
                "($TAG_ID_COLUMN INTEGER PRIMARY KEY, $TAG_NAME_COLUMN TEXT UNIQUE)"

        db?.execSQL(createNoteTableQuery)
        db?.execSQL(createTagTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Prevents duplicate tables of the same name being created
        val dropNoteTableQuery = "DROP TABLE IF EXISTS $NOTE_TABLE_NAME"
        db?.execSQL(dropNoteTableQuery)

        val dropTagTableQuery = "DROP TABLE IF EXISTS $TAG_TABLE_NAME"
        db?.execSQL(dropTagTableQuery)
        onCreate(db)
    }



    // NOTE DATABASE FUNCTIONS

    // Handles insertion of new notes into the database
    fun insertNote(note: Note) {

        val db = writableDatabase // Database manipulator object

        // ContentValues class stores values associated with column names
        val values = ContentValues().apply {
            // ID not needed since sqlite provides unique ids
            put(NOTE_TITLE_COLUMN, note.noteTitle)
            put(NOTE_CONTENT_COLUMN, note.noteContent)
            put(NOTE_CREATED_COLUMN, note.noteCreatedDate)
            put(NOTE_MODIFIED_COLUMN, note.noteModifiedDate)
            put(NOTE_PINNED_COLUMN, note.notePinned.toString())
        }

        db.insert(NOTE_TABLE_NAME, null, values)
        db.close()
    }

    // Retrieve all notes in the database
    fun getAllNotes(): List<Note> {
        val notesList = mutableListOf<Note>() // Store note objects, similar to java arraylist
        val db = writableDatabase // Database manipulator object

        val query = "SELECT * FROM $NOTE_TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        /*
            Move the cursor to the next row. This method will return false if the
            cursor is already past the last entry in the result set.
         */
        while (cursor.moveToNext()) {
            // Collect each note record from the database, store data as a note object
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(NOTE_ID_COLUMN))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(NOTE_TITLE_COLUMN))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(NOTE_CONTENT_COLUMN))
            val createdDate = cursor.getString(cursor.getColumnIndexOrThrow(NOTE_CREATED_COLUMN))
            val modifiedDate = cursor.getString(cursor.getColumnIndexOrThrow(NOTE_MODIFIED_COLUMN))
            val isPinned = cursor.getString(cursor.getColumnIndexOrThrow(NOTE_PINNED_COLUMN))

            val note = Note(id, title, content, createdDate, modifiedDate, isPinned.toBoolean())

            // Add collected note to note list
            notesList.add(note)
        }

        cursor.close()
        db.close()

        return notesList
    }

    // Update a note in the database w/ the edited version
    fun updateNote(note: Note){
        val db = writableDatabase // Database manipulator object

        val values = ContentValues().apply {
            put(NOTE_TITLE_COLUMN, note.noteTitle)
            put(NOTE_CONTENT_COLUMN, note.noteContent)
            put(NOTE_MODIFIED_COLUMN, note.noteModifiedDate)
            put(NOTE_PINNED_COLUMN, note.notePinned.toString())
        }

        val whereClause = "$NOTE_ID_COLUMN = ?"
        val whereArgs = arrayOf(note.noteID.toString())

        db.update(NOTE_TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    // Get a specific note from the database using its id
    fun getNoteByID(noteID: Int): Note {
        val db = readableDatabase // Database accessor object

        val query = "SELECT * FROM $NOTE_TABLE_NAME WHERE $NOTE_ID_COLUMN = $noteID"
        val cursor = db.rawQuery(query, null)

        /*
            Move the cursor to the first row. This method will return false
            if the cursor is empty.
            Returns: boolean - whether the move succeeded.
        */
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(NOTE_ID_COLUMN))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(NOTE_TITLE_COLUMN))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(NOTE_CONTENT_COLUMN))
        val created = cursor.getString(cursor.getColumnIndexOrThrow(NOTE_CREATED_COLUMN))
        val modified = cursor.getString(cursor.getColumnIndexOrThrow(NOTE_MODIFIED_COLUMN))
        val pinned = cursor.getString(cursor.getColumnIndexOrThrow(NOTE_PINNED_COLUMN))

        cursor.close()
        db.close()

        Log.d("getNoteModData", modified.toString())

        Log.d("getNoteData", pinned.toString())

        return Note(id, title, content, created, modified, pinned.toBoolean())
    }

    // Remove a specific note from the database using its id
    fun deleteNote(noteID: Int) {
        val db = writableDatabase // Database manipulator object

        val whereClause = "$NOTE_ID_COLUMN = ?"
        val whereArgs = arrayOf(noteID.toString())

        db.delete(NOTE_TABLE_NAME, whereClause, whereArgs)
        db.close()
    }




    // TAG DATABASE FUNCTIONS

    // Handles insertion of new tags into the database
    fun insertTag(tag: Tag) {

        val db = writableDatabase // Database manipulator object

        // ContentValues class stores values associated with column names
        val values = ContentValues().apply {
            // ID not needed since sqlite provides unique ids
            put(TAG_NAME_COLUMN, tag.tagName)
        }

        db.insert(TAG_TABLE_NAME, null, values)
        db.close()
    }

    // Retrieve all tags in the database
    fun getAllTags(): List<Tag> {
        val tagsList = mutableListOf<Tag>() // Store tag objects, similar to java arraylist
        val db = writableDatabase // Database manipulator object

        val query = "SELECT * FROM $TAG_TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        /*
            Move the cursor to the next row. This method will return false if the
            cursor is already past the last entry in the result set.
         */
        while (cursor.moveToNext()) {
            // Collect each note record from the database, store data as a note object
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(TAG_ID_COLUMN))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(TAG_NAME_COLUMN))

            val tag = Tag(id, name)

            // Add collected note to note list
            tagsList.add(tag)
        }

        cursor.close()
        db.close()

        return tagsList
    }

    // Update a tag in the database w/ the edited version
    fun updateTag(tag: Tag){
        val db = writableDatabase // Database manipulator object

        val values = ContentValues().apply {
            put(TAG_NAME_COLUMN, tag.tagName)
        }

        val whereClause = "$TAG_ID_COLUMN = ?"
        val whereArgs = arrayOf(tag.tagID.toString())

        db.update(TAG_TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    // Get a specific tag from the database using its id
    fun getTagByID(tagID: Int): Tag {
        val db = readableDatabase // Database accessor object

        val query = "SELECT * FROM $TAG_TABLE_NAME WHERE $TAG_ID_COLUMN = $tagID"
        val cursor = db.rawQuery(query, null)

        /*
            Move the cursor to the first row. This method will return false
            if the cursor is empty.
            Returns: boolean - whether the move succeeded.
        */
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(TAG_ID_COLUMN))
        val name = cursor.getString(cursor.getColumnIndexOrThrow(TAG_NAME_COLUMN))

        cursor.close()
        db.close()

        return Tag(id, name)
    }

    // Remove a specific tag from the database using its id
    fun deleteTag(tagID: Int) {
        val db = writableDatabase // Database manipulator object

        val whereClause = "$TAG_ID_COLUMN = ?"
        val whereArgs = arrayOf(tagID.toString())

        db.delete(TAG_TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

}