package com.ckestudios.lumonote.data.database

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.ckestudios.lumonote.data.models.Note

class NoteDatabaseHelper(
    private val noteTableName: String,
    private val noteIDColumn: String,
    private val noteTitleColumn: String,
    private val noteContentColumn: String,
    private val noteSpansColumn: String,
    private val noteCreatedColumn: String,
    private val noteModifiedColumn: String,
    private val notePinnedColumn: String
) {

    // Insert a new note safely
    fun insertNote(note: Note, db: SQLiteDatabase) {
        val values = ContentValues().apply {
            put(noteTitleColumn, note.noteTitle)
            put(noteContentColumn, note.noteContent)
            put(noteSpansColumn, note.noteSpans)
            put(noteCreatedColumn, note.noteCreatedDate)
            put(noteModifiedColumn, note.noteModifiedDate)
            put(notePinnedColumn, note.notePinned.toString())
        }

        try {
            db.insertOrThrow(noteTableName, null, values)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    // Retrieve all notes safely
    fun getAllNotes(db: SQLiteDatabase): List<Note> {
        val notesList = mutableListOf<Note>()
        var cursor: Cursor? = null

        try {
            val query = "SELECT * FROM $noteTableName"
            cursor = db.rawQuery(query, null)

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(noteIDColumn))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(noteTitleColumn))
                    val content = cursor.getString(cursor.getColumnIndexOrThrow(noteContentColumn))
                    val spans = cursor.getString(cursor.getColumnIndexOrThrow(noteSpansColumn))
                    val created = cursor.getString(cursor.getColumnIndexOrThrow(noteCreatedColumn))
                    val modified = cursor.getString(cursor.getColumnIndexOrThrow(noteModifiedColumn))
                    val pinned = cursor.getString(cursor.getColumnIndexOrThrow(notePinnedColumn))

                    notesList.add(
                        Note(id, title, content, spans, created, modified, pinned.toBoolean())
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }

        return notesList
    }

    // Retrieve notes by creation date safely
    fun getNotesByDate(date: String, db: SQLiteDatabase): List<Note> {
        val notesList = mutableListOf<Note>()
        var cursor: Cursor? = null

        try {
            val query = "SELECT * FROM $noteTableName WHERE $noteCreatedColumn = ?"
            cursor = db.rawQuery(query, arrayOf(date))

            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(noteIDColumn))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(noteTitleColumn))
                    val content = cursor.getString(cursor.getColumnIndexOrThrow(noteContentColumn))
                    val spans = cursor.getString(cursor.getColumnIndexOrThrow(noteSpansColumn))
                    val created = cursor.getString(cursor.getColumnIndexOrThrow(noteCreatedColumn))
                    val modified = cursor.getString(cursor.getColumnIndexOrThrow(noteModifiedColumn))
                    val pinned = cursor.getString(cursor.getColumnIndexOrThrow(notePinnedColumn))

                    notesList.add(
                        Note(id, title, content, spans, created, modified, pinned.toBoolean())
                    )
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }

        return notesList
    }

    // Update an existing note safely
    fun updateNote(note: Note, db: SQLiteDatabase) {
        val values = ContentValues().apply {
            put(noteTitleColumn, note.noteTitle)
            put(noteContentColumn, note.noteContent)
            put(noteSpansColumn, note.noteSpans)
            put(noteModifiedColumn, note.noteModifiedDate)
            put(notePinnedColumn, note.notePinned.toString())
        }

        val whereClause = "$noteIDColumn = ?"
        val whereArgs = arrayOf(note.noteID.toString())

        try {
            db.update(noteTableName, values, whereClause, whereArgs)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }

    // Get a specific note by ID safely (returns null if not found)
    fun getNoteByID(noteID: Int, db: SQLiteDatabase): Note? {
        var note: Note? = null
        var cursor: Cursor? = null

        try {
            val query = "SELECT * FROM $noteTableName WHERE $noteIDColumn = ?"
            cursor = db.rawQuery(query, arrayOf(noteID.toString()))

            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(noteIDColumn))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(noteTitleColumn))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(noteContentColumn))
                val spans = cursor.getString(cursor.getColumnIndexOrThrow(noteSpansColumn))
                val created = cursor.getString(cursor.getColumnIndexOrThrow(noteCreatedColumn))
                val modified = cursor.getString(cursor.getColumnIndexOrThrow(noteModifiedColumn))
                val pinned = cursor.getString(cursor.getColumnIndexOrThrow(notePinnedColumn))

                note = Note(id, title, content, spans, created, modified, pinned.toBoolean())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }

        return note
    }

    // Delete a specific note safely
    fun deleteNote(noteID: Int, db: SQLiteDatabase) {
        val whereClause = "$noteIDColumn = ?"
        val whereArgs = arrayOf(noteID.toString())

        try {
            db.delete(noteTableName, whereClause, whereArgs)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db.close()
        }
    }
}
