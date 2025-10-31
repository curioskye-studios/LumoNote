package com.ckestudios.lumonote.ui.noteview.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.data.repository.NoteRepository
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.databinding.ActivityNoteViewBinding
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.ckestudios.lumonote.ui.sharedviewmodel.AppSharedViewFactory
import com.ckestudios.lumonote.ui.sharedviewmodel.NoteAppSharedViewModel
import com.ckestudios.lumonote.utils.basichelpers.BasicUtilityHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralTextHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.state.StateManager
import java.time.LocalDate
import java.util.Timer
import kotlin.concurrent.timer


class NoteViewActivity : AppCompatActivity() {

    private lateinit var noteViewBinding: ActivityNoteViewBinding

    // Stores reference to id of current note being updated, stays -1 if not found
    private var noteID: Int = -1
    private var existingNoteClicked: Boolean = false
    private lateinit var retrievedNote: Note

    private lateinit var inputSharedViewModel: InputSharedViewModel
    private lateinit var editContentSharedViewModel: EditContentSharedViewModel
    private lateinit var noteAppSharedViewModel: NoteAppSharedViewModel

    private var autoSaveTimer: Timer? = null
    private var runningAutoSave = false
    private var runningManualSave = false
    private var closeNote = false


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        noteViewBinding = ActivityNoteViewBinding.inflate(layoutInflater)
        setContentView(noteViewBinding.root)


        // Set up view models
        val noteRepository = NoteRepository(this)
        val appSharedViewFactory = AppSharedViewFactory(noteRepository)

        noteAppSharedViewModel = ViewModelProvider(this, appSharedViewFactory)
            .get(NoteAppSharedViewModel::class.java)

        inputSharedViewModel = ViewModelProvider(this).get(InputSharedViewModel::class.java)

        editContentSharedViewModel =
            ViewModelProvider(this).get(EditContentSharedViewModel::class.java)

        editContentSharedViewModel.setNoteContentEditTextView(noteViewBinding.noteEditContentET)

        val stateManager = StateManager(noteViewBinding.noteEditContentET)
        editContentSharedViewModel.setNoteContentStateManager(stateManager)


        // Check if working with existing note
        val isExistingNote = checkIfIsExistingNote()
        if (isExistingNote == null) {

            finish()
            return
        } else {

            populateUIWithNoteData(isExistingNote)
        }


        // Setup Functionality

        BasicUtilityHelper.clearETViewFocusOnHideKeyboard(noteViewBinding.noteTitleET,
            noteViewBinding.root)
        BasicUtilityHelper.clearETViewFocusOnHideKeyboard(noteViewBinding.noteEditContentET,
            noteViewBinding.root)

        runAutoSave()

        notifyIfEditing()

        setOnClickListeners()

        observeNoteAppVMValues()

        observeInputVMValues()

        finalNoteFeedback()
    }


    private fun updateModifiedDate(){

        // Format: YYYY-MM-DD
        val currentDate = LocalDate.parse(LocalDate.now().toString())
        val convertedDate = GeneralTextHelper.formatDate(currentDate)
        noteViewBinding.modifiedDateTV.text = convertedDate
    }


    private fun runAutoSave(){

        stopAutoSave()

        // run every 5s
        autoSaveTimer = timer(initialDelay = 500, period = 15000) {

            runningAutoSave = true

            updateRetrievedNote()

            collectNoteData()

            runningAutoSave = false
        }
    }

    private fun stopAutoSave(){

        autoSaveTimer?.cancel()
        autoSaveTimer = null
    }


    private fun notifyIfEditing() {

        // For removing text formatter when text content not being edited
        noteViewBinding.noteEditContentET.setOnFocusChangeListener { _, hasFocus ->

            inputSharedViewModel.setNoteContentIsEditing(hasFocus)
        }
    }

    private fun checkIfIsExistingNote() : Boolean? {

        existingNoteClicked = intent.hasExtra("note_id")

        // If an existing note was clicked on rather than the create button
        if (existingNoteClicked) {

            // Retrieve the data passed in from NotePreviewAdapter.kt
            noteID = intent.getIntExtra("note_id", -1)

            // If no note found, exit activity
            if (noteID == -1) {
                return null
            }

            updateRetrievedNote()
        }

        return existingNoteClicked
    }

    private fun updateRetrievedNote() {

        if (existingNoteClicked) retrievedNote = noteAppSharedViewModel.getNote(noteID)
    }


    private fun populateUIWithNoteData(usingExistingNote: Boolean) {

        if (usingExistingNote) {

//            Log.d("populateUIWithNoteData", retrievedNote.noteModifiedDate)

            // Parse the modified date as a date object
            val convertedDate = LocalDate.parse(retrievedNote.noteModifiedDate);
            val retrievedNoteDate = GeneralTextHelper.formatDate(convertedDate)

            Log.d("noteData", retrievedNote.notePinned.toString())

            // Populate the view note activity UI w/ the pre-existing note data
            noteViewBinding.modifiedDateTV.text = retrievedNoteDate
            noteViewBinding.noteTitleET.setText(retrievedNote.noteTitle)
            noteViewBinding.noteEditContentET.setText(retrievedNote.noteContent)

            noteViewBinding.pinButtonIV.tag = retrievedNote.notePinned

            GeneralButtonIVHelper.updatePinHighlight(noteViewBinding.pinButtonIV, this,
                R.drawable.selected_background)
        }

        else {

            // Display the modified date as current date
            noteViewBinding.modifiedDateTV.text = GeneralTextHelper.formatDate(LocalDate.now())

            // set isPinned to false
            noteViewBinding.pinButtonIV.tag = false
        }
    }

    private fun setOnClickListeners(){

        noteViewBinding.apply {

            backButtonIV.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(this@NoteViewActivity,
                    backButtonIV)

                runningManualSave = true

                collectNoteData()

                finish()
            }


            deleteButtonIV.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(this@NoteViewActivity,
                    deleteButtonIV)

                noteAppSharedViewModel.deleteNote(noteID)
            }


            pinButtonIV.setOnClickListener {

                val pinnedFlag = pinButtonIV.tag as Boolean

                pinButtonIV.tag = !pinnedFlag

                noteAppSharedViewModel.updateCurrentPinStatus(!pinnedFlag)
            }


            saveButtonIV.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(this@NoteViewActivity,
                    saveButtonIV)

                runningManualSave = true

                updateRetrievedNote()

                collectNoteData()


                val title =  noteViewBinding.noteTitleET.text.toString()
                val content =  noteViewBinding.noteEditContentET.text.toString()
                val pinned: Boolean =  (noteViewBinding.pinButtonIV.tag as Boolean)

                if (existingNoteClicked) {

                    if (!(retrievedNote.noteTitle == title && retrievedNote.noteContent == content &&
                        retrievedNote.notePinned == pinned)) {
                        GeneralUIHelper.displayFeedbackToast(this@NoteViewActivity,
                            "Changes Saved", false)
                    } else {
                        GeneralUIHelper.displayFeedbackToast(this@NoteViewActivity,
                            "Up to date", false)
                    }
                }
            }

            saveCloseButtonIV.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(this@NoteViewActivity,
                    saveCloseButtonIV)

                runningManualSave = true
                closeNote = true

                collectNoteData()

                finish()
            }


            val backButtonPressedCallback =
                object : OnBackPressedCallback(true) {
                    // Custom logic for back button press
                    override fun handleOnBackPressed() {

                        runningManualSave = true
                        closeNote = true

                        collectNoteData()

                        finish()
                    }
                }
            onBackPressedDispatcher.addCallback(this@NoteViewActivity, backButtonPressedCallback)

        }


    }


    private fun observeNoteAppVMValues() {

        noteAppSharedViewModel.apply {

            currentNotePinned.observe(this@NoteViewActivity){ currentlyPinned ->

                GeneralButtonIVHelper.updatePinHighlight(noteViewBinding.pinButtonIV,
                    this@NoteViewActivity, R.drawable.selected_background)

                val pinStateFeedback =
                    if (currentlyPinned) { "Note Pinned" }

                    else { "Note Unpinned" }

                GeneralUIHelper.displayFeedbackToast(this@NoteViewActivity,
                    pinStateFeedback, false)
            }
        }

    }

    private fun observeInputVMValues() {

        inputSharedViewModel.apply {

            noteContentIsEditing.observe(this@NoteViewActivity){ isTrue ->

                noteViewBinding.noteEditContentET.isCursorVisible = isTrue

                if (isTrue) {
                    runAutoSave()
                } else {
                    stopAutoSave()
                    collectNoteData()
                }
            }

        }
    }



    // Collects and submits note to database upon clicking save button
    private fun collectNoteData() {

        // Collect data from input fields, store in note object
        val title =  noteViewBinding.noteTitleET.text.toString()
        val content =  noteViewBinding.noteEditContentET.text.toString()
        val pinned: Boolean =  (noteViewBinding.pinButtonIV.tag as Boolean)

        Log.d("collectNoteData", pinned.toString())

        // Format: YYYY-MM-DD
        val currentDate = LocalDate.now().toString()

        val created = currentDate
        val modified = currentDate


        // If an existing note was clicked on rather than the create button
        if (existingNoteClicked) {

            if (retrievedNote.noteTitle == title && retrievedNote.noteContent == content &&
                retrievedNote.notePinned == pinned) return

            updateModifiedDate()

            if (runningAutoSave) noteAppSharedViewModel.setIsNewNoteAsync(false)
            else noteAppSharedViewModel.setIsNewNote(false)

            val updatedNote = Note(retrievedNote.noteID, title, content,
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

            val newNote = Note(0, title, content, created, modified, pinned)

            noteAppSharedViewModel.saveNote(newNote, false)
        }

    }



    private fun finalNoteFeedback() {

        noteAppSharedViewModel.noteWasCreated.observe(this){ isTrue ->

            if (isTrue && runningManualSave && closeNote) {

                GeneralUIHelper.closeActivityWithFeedback("Note Created", this,
                    this, true)
            }
        }

        noteAppSharedViewModel.noteWasUpdated.observe(this){ isTrue ->

            if (isTrue && runningManualSave && closeNote) {

                GeneralUIHelper.closeActivityWithFeedback("Note Updated", this,
                    this, true)
            }
        }

        noteAppSharedViewModel.noteWasDeleted.observe(this){ isTrue ->

            if (isTrue) {

                GeneralUIHelper.closeActivityWithFeedback("Note Deleted", this,
                    this, true)
            }
        }
    }

}