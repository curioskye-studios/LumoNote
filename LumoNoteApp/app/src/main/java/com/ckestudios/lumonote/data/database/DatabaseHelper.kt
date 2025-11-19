package com.ckestudios.lumonote.data.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.data.models.Tag

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "LumoNote.db"
        private const val DATABASE_VERSION = 2

        private const val NOTE_TABLE_NAME = "Notes"
        private const val NOTE_ID_COLUMN = "NoteID"
        private const val NOTE_TITLE_COLUMN = "NoteTitle"
        private const val NOTE_CONTENT_COLUMN = "NoteContent"
        private const val NOTE_SPANS_COLUMN = "NoteSpans"
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
        NOTE_SPANS_COLUMN, NOTE_CREATED_COLUMN, NOTE_MODIFIED_COLUMN, NOTE_PINNED_COLUMN
    )

    private val tagDatabaseHelper = TagDatabaseHelper(
        TAG_TABLE_NAME, TAG_ID_COLUMN, TAG_NAME_COLUMN
    )

    private val taggedDatabaseHelper = TaggedDatabaseHelper(
        TAGGED_TABLE_NAME, TAGGED_TAGID_COLUMN, TAGGED_NOTEID_COLUMN
    )

    override fun onCreate(db: SQLiteDatabase?) {
        try {
            db?.execSQL(
                "CREATE TABLE $NOTE_TABLE_NAME (" +
                        "$NOTE_ID_COLUMN INTEGER PRIMARY KEY, " +
                        "$NOTE_TITLE_COLUMN TEXT, " +
                        "$NOTE_CONTENT_COLUMN TEXT, " +
                        "$NOTE_SPANS_COLUMN TEXT, " +
                        "$NOTE_CREATED_COLUMN TEXT, " +
                        "$NOTE_MODIFIED_COLUMN TEXT, " +
                        "$NOTE_PINNED_COLUMN TEXT)"
            )

            db?.execSQL(
                "CREATE TABLE $TAG_TABLE_NAME (" +
                        "$TAG_ID_COLUMN INTEGER PRIMARY KEY, " +
                        "$TAG_NAME_COLUMN TEXT UNIQUE)"
            )

            db?.execSQL(
                "CREATE TABLE $TAGGED_TABLE_NAME (" +
                        "$TAGGED_TAGID_COLUMN INTEGER, " +
                        "$TAGGED_NOTEID_COLUMN INTEGER, " +
                        "PRIMARY KEY($TAGGED_TAGID_COLUMN, $TAGGED_NOTEID_COLUMN), " +
                        "FOREIGN KEY($TAGGED_TAGID_COLUMN) REFERENCES $TAG_TABLE_NAME($TAG_ID_COLUMN), " +
                        "FOREIGN KEY($TAGGED_NOTEID_COLUMN) REFERENCES $NOTE_TABLE_NAME($NOTE_ID_COLUMN))"
            )
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error creating tables", e)
        }
    }

    override fun onConfigure(db: SQLiteDatabase?) {
        try {
            super.onConfigure(db)
            db?.setForeignKeyConstraintsEnabled(true)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error configuring DB", e)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        try {
            db?.execSQL("DROP TABLE IF EXISTS $TAGGED_TABLE_NAME")
            db?.execSQL("DROP TABLE IF EXISTS $TAG_TABLE_NAME")
            db?.execSQL("DROP TABLE IF EXISTS $NOTE_TABLE_NAME")
            onCreate(db)
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error upgrading DB", e)
        }
    }



    // --- NOTE FUNCTIONS ---
    fun insertNote(note: Note) {
        try {
            writableDatabase.use { db ->
                noteDatabaseHelper.insertNote(note, db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting note", e)
        }
    }

    fun getAllNotes(): List<Note> {
        return try {
            readableDatabase.use { db ->
                noteDatabaseHelper.getAllNotes(db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error getting all notes", e)
            emptyList()
        }
    }

    fun getLastInsertedNote(): Note? {
        return try {
            readableDatabase.use { db ->
                noteDatabaseHelper.getLastInsertedNote(db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error getting last added tag", e)
            null
        }
    }

    fun getNotesByDate(date: String): List<Note> {
        return try {
            readableDatabase.use { db ->
                noteDatabaseHelper.getNotesByDate(date, db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error getting notes by date", e)
            emptyList()
        }
    }

    fun getNotesByPinnedStatus(getUnpinned: Boolean): List<Note> {
        return try {
            readableDatabase.use { db ->
                noteDatabaseHelper.getNotesByPinnedStatus(getUnpinned, db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error getting notes by pinned status", e)
            emptyList()
        }
    }

    fun updateNote(note: Note) {
        try {
            writableDatabase.use { db ->
                noteDatabaseHelper.updateNote(note, db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error updating note", e)
        }
    }

    fun getNoteByID(noteID: Int): Note? {
        return try {
            readableDatabase.use { db ->
                noteDatabaseHelper.getNoteByID(noteID, db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error getting note by ID", e)
            null
        }
    }

    fun deleteNote(noteID: Int) {
        try {
            deleteTaggedByNoteID(noteID)
            writableDatabase.use { db ->
                noteDatabaseHelper.deleteNote(noteID, db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error deleting note", e)
        }
    }




    // --- TAG FUNCTIONS ---
    fun insertTag(tag: Tag) {
        try {
            writableDatabase.use { db ->
                tagDatabaseHelper.insertTag(tag, db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting tag", e)
        }
    }

    fun getAllTags(): List<Tag> {
        return try {
            readableDatabase.use { db ->
                tagDatabaseHelper.getAllTags(db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error getting all tags", e)
            emptyList()
        }
    }

    fun getLastInsertedTag(): Tag? {
        return try {
            readableDatabase.use { db ->
                tagDatabaseHelper.getLastInsertedTag(db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error getting last added tag", e)
            null
        }
    }

    fun updateTag(tag: Tag) {
        try {
            writableDatabase.use { db ->
                tagDatabaseHelper.updateTag(tag, db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error updating tag", e)
        }
    }

    fun getTagByID(tagID: Int): Tag? {
        return try {
            readableDatabase.use { db ->
                tagDatabaseHelper.getTagByID(tagID, db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error getting tag by ID", e)
            null
        }
    }

    fun deleteTag(tagID: Int) {
        try {
            deleteTaggedByTagID(tagID)
            writableDatabase.use { db ->
                tagDatabaseHelper.deleteTag(tagID, db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error deleting tag", e)
        }
    }




    // --- TAGGED FUNCTIONS ---
    fun insertTagged(tagID: Int, noteID: Int) {
        try {
            writableDatabase.use { db ->
                taggedDatabaseHelper.insertTagged(tagID, noteID, db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error inserting tagged record", e)
        }
    }

    fun deleteTagged(tagID: Int, noteID: Int) {
        try {
            writableDatabase.use { db ->
                taggedDatabaseHelper.deleteTagged(tagID, noteID, db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error deleting tagged record", e)
        }
    }

    fun deleteTaggedByTagID(tagID: Int) {
        try {
            writableDatabase.use { db ->
                taggedDatabaseHelper.deleteTaggedByTagID(tagID, db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error deleting tagged record", e)
        }
    }

    fun deleteTaggedByNoteID(noteID: Int) {
        try {
            writableDatabase.use { db ->
                taggedDatabaseHelper.deleteTaggedByNoteID(noteID, db)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error deleting tagged record", e)
        }
    }

    fun getTagsByNoteID(noteID: Int): List<Tag> {
        return try {
            readableDatabase.use { db ->
                val tagIDs = taggedDatabaseHelper.getTagsByNoteID(noteID, db)
                tagIDs.mapNotNull { id -> getTagByID(id) }
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error getting tags by note ID", e)
            emptyList()
        }
    }

    fun getNotesByTagID(tagID: Int): List<Note> {
        return try {
            readableDatabase.use { db ->
                val noteIDs = taggedDatabaseHelper.getNotesByTagID(tagID, db)
                noteIDs.mapNotNull { id -> getNoteByID(id) }
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error getting notes by tag ID", e)
            emptyList()
        }
    }
}
