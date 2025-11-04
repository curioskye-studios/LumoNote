package com.ckestudios.lumonote.ui.noteview.other

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.utils.basichelpers.GeneralTextHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.state.SpanProcessor
import java.time.LocalDate
import kotlin.concurrent.timer

object NoteDataHandler {

    fun runAutoSave(){

        stopAutoSave()

        // run every 5s
        autoSaveTimer = timer(initialDelay = 500, period = 15000) {

            runningAutoSave = true

            updateRetrievedNote()

            collectNoteData(noteViewBinding.noteTitleET,
                noteViewBinding.noteEditContentET, noteViewBinding.pinButtonIV)

            runningAutoSave = false
        }
    }

    private fun stopAutoSave(){

        autoSaveTimer?.cancel()
        autoSaveTimer = null
    }


    fun getNoteDataDict() : Map<String, String> {


    }


    // Collects and submits note to database upon clicking save button
    fun collectNoteData(noteTitle: EditText, noteEditContent: EditText, notePinButton: ImageView) {

        // Collect data from input fields, store in note object
        val title = noteTitle.text.toString()
        val content = noteEditContent.text.toString()
        val spans = SpanProcessor.extractSpans(noteEditContent)
        val pinned: Boolean = (notePinButton.tag as Boolean)

        Log.d("collectNoteData", pinned.toString())
        Log.d("SaveSpans", "spans: $spans")

        // Format: YYYY-MM-DD
        val currentDate = LocalDate.now().toString()

        val created = currentDate
        val modified = currentDate


        // If an existing note was clicked on rather than the create button
        if (existingNoteClicked) {

            if (retrievedNote.noteTitle == title && retrievedNote.noteContent == content &&
                retrievedNote.notePinned == pinned && retrievedNote.noteSpans == spans) return

            updateModifiedDate()

            if (runningAutoSave) noteAppSharedViewModel.setIsNewNoteAsync(false)
            else noteAppSharedViewModel.setIsNewNote(false)

            val updatedNote = Note(retrievedNote.noteID, title, content, spans,
                retrievedNote.noteCreatedDate, modified, pinned)

            noteAppSharedViewModel.saveNote(updatedNote, runningAutoSave)

            if (runningAutoSave) {

                // switch to main thread
                Handler(Looper.getMainLooper()).post {

                    GeneralUIHelper.displayFeedbackToast(this, "Auto saved",
                        false)
                }
            }

        } else {

            if (runningAutoSave) return

            noteAppSharedViewModel.setIsNewNote(true)

            val newNote = Note(0, title, content, spans, created, modified, pinned)

            noteAppSharedViewModel.saveNote(newNote, false)
        }

    }
}