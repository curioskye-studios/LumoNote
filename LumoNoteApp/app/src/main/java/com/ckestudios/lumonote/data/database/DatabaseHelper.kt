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

        private const val TAGGED_TABLE_NAME = "Tagged"
        private const val TAGGED_TAGID_COLUMN = "TagID"
        private const val TAGGED_NOTEID_COLUMN = "NoteID"

    }

    private val noteDatabaseHelper = NoteDatabaseHelper(
        NOTE_TABLE_NAME, NOTE_ID_COLUMN, NOTE_TITLE_COLUMN, NOTE_CONTENT_COLUMN,
        NOTE_CREATED_COLUMN, NOTE_MODIFIED_COLUMN, NOTE_PINNED_COLUMN
    )

    private val tagDatabaseHelper = TagDatabaseHelper(
        TAG_TABLE_NAME, TAG_ID_COLUMN, TAG_NAME_COLUMN
    )

    private val taggedDatabaseHelper = TaggedDatabaseHelper(
        TAGGED_TABLE_NAME, TAGGED_TAGID_COLUMN, TAGGED_NOTEID_COLUMN
    )


    override fun onCreate(db: SQLiteDatabase?) {

        val createNoteTableQuery = "CREATE TABLE $NOTE_TABLE_NAME " +
            "(" +
                "$NOTE_ID_COLUMN INTEGER PRIMARY KEY, " +
                "$NOTE_TITLE_COLUMN TEXT, " +
                "$NOTE_CONTENT_COLUMN TEXT, " +
                "$NOTE_CREATED_COLUMN, " +
                "$NOTE_MODIFIED_COLUMN, $NOTE_PINNED_COLUMN" +
            ")"

        val createTagTableQuery = "CREATE TABLE $TAG_TABLE_NAME " +
            "(" +
                "$TAG_ID_COLUMN INTEGER PRIMARY KEY, " +
                "$TAG_NAME_COLUMN TEXT UNIQUE" +
            ")"

        val createTaggedTableQuery = "CREATE TABLE $TAGGED_TABLE_NAME " +
            "(" +
                "$TAGGED_TAGID_COLUMN INTEGER, " +
                "$TAGGED_NOTEID_COLUMN INTEGER, " +
                "PRIMARY KEY($TAGGED_TAGID_COLUMN, $TAGGED_NOTEID_COLUMN), " +
                "FOREIGN KEY($TAGGED_TAGID_COLUMN) REFERENCES $TAG_TABLE_NAME($TAG_ID_COLUMN), " +
                "FOREIGN KEY($TAGGED_NOTEID_COLUMN) REFERENCES $NOTE_TABLE_NAME($NOTE_ID_COLUMN)" +
            ")"


        db?.execSQL(createNoteTableQuery)
        db?.execSQL(createTagTableQuery)
        db?.execSQL(createTaggedTableQuery)
    }

    override fun onConfigure(db: SQLiteDatabase?) {

        super.onConfigure(db)
        db?.setForeignKeyConstraintsEnabled(true)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        // Prevents duplicate tables of the same name being created
        val dropNoteTableQuery = "DROP TABLE IF EXISTS $NOTE_TABLE_NAME"
        val dropTagTableQuery = "DROP TABLE IF EXISTS $TAG_TABLE_NAME"
        val dropTaggedTableQuery = "DROP TABLE IF EXISTS $TAGGED_TABLE_NAME"

        db?.execSQL(dropNoteTableQuery)
        db?.execSQL(dropTagTableQuery)
        db?.execSQL(dropTaggedTableQuery)
        onCreate(db)
    }


    // NOTE DATABASE FUNCTIONS
    fun insertNote(note: Note) {
        val db = writableDatabase // Database manipulator object
        noteDatabaseHelper.insertNote(note, db)
    }
    fun getAllNotes(): List<Note> {
        val db = writableDatabase
        return noteDatabaseHelper.getAllNotes(db)
    }
    fun getNotesByDate(date: String): List<Note> {
        val db = readableDatabase
        return noteDatabaseHelper.getNotesByDate(date, db)
    }
    fun updateNote(note: Note){
        val db = writableDatabase
        noteDatabaseHelper.updateNote(note, db)
    }
    fun getNoteByID(noteID: Int): Note {
        val db = readableDatabase
        return noteDatabaseHelper.getNoteByID(noteID, db)
    }
    fun deleteNote(noteID: Int) {
        val db = writableDatabase
        noteDatabaseHelper.deleteNote(noteID, db)
    }


    // TAG DATABASE FUNCTIONS
    fun insertTag(tag: Tag) {
        val db = writableDatabase // Database manipulator object
        tagDatabaseHelper.insertTag(tag, db)
    }
    fun getAllTags(): List<Tag> {
        val db = writableDatabase
        return tagDatabaseHelper.getAllTags(db)
    }
    fun updateTag(tag: Tag){
        val db = writableDatabase
        tagDatabaseHelper.updateTag(tag, db)
    }
    fun getTagByID(tagID: Int): Tag {
        val db = readableDatabase
        return tagDatabaseHelper.getTagByID(tagID, db)
    }
    fun deleteTag(tagID: Int) {
        val db = writableDatabase
        tagDatabaseHelper.deleteTag(tagID, db)
    }


    // TAGGED DATABASE FUNCTIONS
    fun insertTagged(tagID: Int, noteID: Int) {
        val db = writableDatabase // Database manipulator object
        taggedDatabaseHelper.insertTagged(tagID, noteID, db)
    }
    fun getTagsByNoteID(noteID: Int): List<Tag> {
        val db = readableDatabase
        val tagList = mutableListOf<Tag>()
        val tagIDs = taggedDatabaseHelper.getTagsByNoteID(noteID, db)

        //get corresponding tags by ID and add to list
        for (tagID in tagIDs) {
            tagList.add(getTagByID(tagID))
        }

        return tagList
    }
    fun deleteTagged(tagID: Int, noteID: Int) {
        val db = writableDatabase
        taggedDatabaseHelper.deleteTagged(tagID, noteID, db)
    }


}