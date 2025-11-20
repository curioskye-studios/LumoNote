package com.ckestudios.lumonote.ui.noteview.other

import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.ui.sharedviewmodel.NoteAppSharedViewModel
import com.ckestudios.lumonote.utils.basichelpers.GeneralDateHelper
import com.ckestudios.lumonote.utils.state.SpanProcessor
import java.time.LocalDate
import java.util.Timer
import kotlin.concurrent.timer

class NoteSaveHelper (private val noteAppSharedViewModel: NoteAppSharedViewModel) {


    private var autoSaveTimer: Timer? = null

    fun runAutoSave(onTimerEndFunction: () -> Unit){

        stopAutoSave()

        // run every 5s
        autoSaveTimer = timer(initialDelay = 500, period = 15000) {

            noteAppSharedViewModel.setRunningAutoSave(true)

            onTimerEndFunction()

            noteAppSharedViewModel.setRunningAutoSave(false)
        }
    }

    fun stopAutoSave(){

        autoSaveTimer?.cancel()
        autoSaveTimer = null
    }


    fun getNoteDataDict(noteTitleET: EditText, noteContentET: EditText, modifiedDateTV: TextView,
                        pinButtonIV: ImageView): MutableMap<String, Any> {

        val noteDataDict = mutableMapOf<String, Any>()

        noteDataDict["title"] = noteTitleET.text.toString()
        noteDataDict["content"] = noteContentET.text.toString()
        noteDataDict["modified"] = modifiedDateTV.text.toString()
        noteDataDict["spans"] = SpanProcessor.extractSpans(noteContentET)
        noteDataDict["pinned"] = (pinButtonIV.tag as Boolean)

        return noteDataDict
    }

    fun noteHasNotChanged(retrievedNote: Note?, noteDataDict: MutableMap<String, Any>): Boolean {

        val localDateModifiedDate =
            GeneralDateHelper.convertFormattedDateToLocalDate(noteDataDict["modified"] as String)

        return when (retrievedNote) {

            null -> false

            else -> {
                retrievedNote.noteTitle == noteDataDict["title"] &&
                retrievedNote.noteContent == noteDataDict["content"] &&
                retrievedNote.noteModifiedDate == localDateModifiedDate.toString() &&
                retrievedNote.notePinned == noteDataDict["pinned"] &&
                retrievedNote.noteSpans == noteDataDict["spans"]
            }
        }
    }

    fun backupNewNote() {

        val currentDate = LocalDate.now().toString()
        val created = currentDate
        val modified = currentDate

        noteAppSharedViewModel.setIsNewNote(true)

        val newNote = Note(
            0,
            "",
            "",
            "",
            created,
            modified,
            false
        )

        noteAppSharedViewModel.saveNote(newNote)
    }



    fun collectNoteData(retrievedNote: Note?, noteDataDict: MutableMap<String, Any>) {

        val runningAutoSave = noteAppSharedViewModel.runningAutoSave.value!!

        // Format: YYYY-MM-DD
        val currentDate = LocalDate.now().toString()
        val modified = currentDate

        // If an existing note was clicked on rather than the create button
        if (retrievedNote != null) {

            if (noteHasNotChanged(retrievedNote, noteDataDict)) return

            noteAppSharedViewModel.setIsNewNoteAsync(false)

            val updatedNote = Note(
                retrievedNote.noteID,
                noteDataDict["title"] as String,
                noteDataDict["content"] as String,
                noteDataDict["spans"] as String,
                retrievedNote.noteCreatedDate,
                modified,
                noteDataDict["pinned"] as Boolean
            )

            noteAppSharedViewModel.saveNote(updatedNote)
        }

    }

}