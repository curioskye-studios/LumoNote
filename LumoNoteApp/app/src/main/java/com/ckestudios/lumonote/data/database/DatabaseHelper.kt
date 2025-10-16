package com.ckestudios.lumonote.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.data.models.Tag


// This class handles all database operations needed for notes CRUD

// Inherits from SQLiteOpenHelper class to allow for sqlite database operates
class DatabaseHelper (context: Context)
    : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

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

    private val noteDatabaseHelper = NoteDatabaseHelper(
        NOTE_TABLE_NAME, NOTE_ID_COLUMN, NOTE_TITLE_COLUMN, NOTE_CONTENT_COLUMN,
        NOTE_CREATED_COLUMN, NOTE_MODIFIED_COLUMN, NOTE_PINNED_COLUMN
    )

    private val tagDatabaseHelper = TagDatabaseHelper(
        TAG_TABLE_NAME, TAG_ID_COLUMN, TAG_NAME_COLUMN
    )


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
    fun insertNote(note: Note) {

        val db = writableDatabase // Database manipulator object
        noteDatabaseHelper.insertNote(note, db)
    }
    fun getAllNotes(): List<Note> {

        val db = writableDatabase // Database manipulator object
        return noteDatabaseHelper.getAllNotes(db)
    }
    fun getNotesByDate(date: String): List<Note> {

        val db = readableDatabase
        return noteDatabaseHelper.getNotesByDate(date, db)
    }
    fun updateNote(note: Note){

        val db = writableDatabase // Database manipulator object
        noteDatabaseHelper.updateNote(note, db)
    }
    fun getNoteByID(noteID: Int): Note {

        val db = readableDatabase // Database accessor object
        return noteDatabaseHelper.getNoteByID(noteID, db)
    }
    fun deleteNote(noteID: Int) {

        val db = writableDatabase // Database manipulator object
        noteDatabaseHelper.deleteNote(noteID, db)
    }


    // TAG DATABASE FUNCTIONS
    fun insertTag(tag: Tag) {

        val db = writableDatabase // Database manipulator object
        tagDatabaseHelper.insertTag(tag, db)
    }
    fun getAllTags(): List<Tag> {

        val db = writableDatabase // Database manipulator object
        return tagDatabaseHelper.getAllTags(db)
    }
    fun updateTag(tag: Tag){

        val db = writableDatabase // Database manipulator object
        tagDatabaseHelper.updateTag(tag, db)
    }
    fun getTagByID(tagID: Int): Tag {

        val db = readableDatabase // Database accessor object
        return tagDatabaseHelper.getTagByID(tagID, db)
    }
    fun deleteTag(tagID: Int) {

        val db = writableDatabase // Database manipulator object
        tagDatabaseHelper.deleteTag(tagID, db)
    }

}