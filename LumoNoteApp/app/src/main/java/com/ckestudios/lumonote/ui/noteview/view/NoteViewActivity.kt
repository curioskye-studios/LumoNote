package com.ckestudios.lumonote.ui.noteview.view

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.data.repository.NoteRepository
import com.ckestudios.lumonote.databinding.ActivityNoteViewBinding
import com.ckestudios.lumonote.ui.noteview.other.NoteSaveHelper
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.ckestudios.lumonote.ui.other.ConfirmationDialogFragment
import com.ckestudios.lumonote.ui.sharedviewmodel.AppSharedViewFactory
import com.ckestudios.lumonote.ui.sharedviewmodel.NoteAppSharedViewModel
import com.ckestudios.lumonote.utils.basichelpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralDateHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.state.SpanProcessor
import com.ckestudios.lumonote.utils.state.StateManager
import java.time.LocalDate


class NoteViewActivity : AppCompatActivity() {

    private lateinit var noteViewBinding: ActivityNoteViewBinding

    // Stores reference to id of current note being updated, stays -1 if not found
    private var noteID: Int = -1
    private var existingNoteClicked: Boolean = false
    private var retrievedNote: Note? = null

    private lateinit var inputSharedViewModel: InputSharedViewModel
    private lateinit var editContentSharedViewModel: EditContentSharedViewModel
    private lateinit var noteAppSharedViewModel: NoteAppSharedViewModel

    private var closeNote = false

    private lateinit var noteSaveHelper: NoteSaveHelper


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        noteViewBinding = ActivityNoteViewBinding.inflate(layoutInflater)
        setContentView(noteViewBinding.root)


        // Set up view models
        val noteRepository = NoteRepository(this)
        val appSharedViewFactory = AppSharedViewFactory(application, noteRepository)

        noteAppSharedViewModel = ViewModelProvider(this, appSharedViewFactory)
            .get(NoteAppSharedViewModel::class.java)

        noteSaveHelper = NoteSaveHelper(noteAppSharedViewModel)

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

            populateUIWithNoteData()
            if (!existingNoteClicked)  noteSaveHelper.backupNewNote()
        }


        // Setup Functionality

        GeneralUIHelper.clearETViewFocusOnHideKeyboard(noteViewBinding.noteTitleET,
            noteViewBinding.root)
        GeneralUIHelper.clearETViewFocusOnHideKeyboard(noteViewBinding.noteEditContentET,
            noteViewBinding.root)

        noteSaveHelper.runAutoSave { commitNoteChanges() }

        notifyIfEditing()

        setOnClickListeners()

        observeNoteAppVMValues()

        observeInputVMValues()

        finalNoteFeedback()
    }


    private fun updateModifiedDate(){

        // Format: YYYY-MM-DD
        val currentDate = LocalDate.parse(LocalDate.now().toString())
        val convertedDate = GeneralDateHelper.formatDate(currentDate)

        runOnUiThread {
            noteViewBinding.modifiedDateTV.text = convertedDate
        }
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

            // Retrieve the data passed in from UnpinnedNotePrevAdapter.kt
            noteID = intent.getIntExtra("note_id", -1)

            // If no note found, exit activity
            if (noteID == -1) {
                return null
            }

            updateRetrievedNote()
            noteAppSharedViewModel.setCurrentOpenNoteID(noteID)
        }

        return existingNoteClicked
    }

    private fun updateRetrievedNote() {

        val newNoteBackup = noteAppSharedViewModel.newNoteBackup.value

        if (existingNoteClicked) retrievedNote = noteAppSharedViewModel.getNote(noteID)
        if (newNoteBackup != null) retrievedNote = newNoteBackup
    }


    private fun commitNoteChanges() {

        val noteDataDict =
            noteSaveHelper.getNoteDataDict(noteViewBinding.noteTitleET,
                noteViewBinding.noteEditContentET,noteViewBinding.modifiedDateTV,
                noteViewBinding.pinButtonIV)

        updateRetrievedNote()

        noteSaveHelper.collectNoteData(retrievedNote, noteDataDict)

        if (!(noteSaveHelper.noteHasNotChanged(retrievedNote, noteDataDict))) {

            updateModifiedDate()
        }
    }


    private fun populateUIWithNoteData() {

        if (retrievedNote != null) {

            // Parse the modified date as a date object
            val convertedDate = LocalDate.parse(retrievedNote!!.noteModifiedDate);
            val retrievedNoteDate = GeneralDateHelper.formatDate(convertedDate)

            Log.d("noteData", retrievedNote!!.notePinned.toString())

            // Populate the view note activity UI w/ the pre-existing note data
            noteViewBinding.modifiedDateTV.text = retrievedNoteDate
            noteViewBinding.noteTitleET.setText(retrievedNote!!.noteTitle)
            noteViewBinding.noteEditContentET.setText(retrievedNote!!.noteContent)
            SpanProcessor.reapplySpansETV(retrievedNote!!.noteSpans, noteViewBinding.noteEditContentET)

            noteViewBinding.pinButtonIV.tag = retrievedNote!!.notePinned
            GeneralButtonIVHelper.updatePinHighlight(noteViewBinding.pinButtonIV, this,
                R.drawable.selected_background)
        }

        else {

            // Display the modified date as current date
            noteViewBinding.modifiedDateTV.text = GeneralDateHelper.formatDate(LocalDate.now())

            noteViewBinding.pinButtonIV.tag = false
        }
    }

    private fun setOnClickListeners(){

        noteViewBinding.apply {

            backButtonIV.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(this@NoteViewActivity,
                    backButtonIV)

                closeNote = true

                commitNoteChanges()

                finish()
            }


            deleteButtonIV.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(this@NoteViewActivity,
                    deleteButtonIV)

                ConfirmationDialogFragment(noteAppSharedViewModel,
                    "Are you sure you want to delete this note? It cannot be undone.",
                    "Yes, Delete")
                    .show(supportFragmentManager, "confirmNoteDialog")
            }


            pinButtonIV.setOnClickListener {

                val pinnedFlag = pinButtonIV.tag as Boolean

                pinButtonIV.tag = !pinnedFlag

                noteAppSharedViewModel.updateCurrentPinStatus(!pinnedFlag)
            }


            saveButtonIV.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(this@NoteViewActivity,
                    saveButtonIV)

                commitNoteChanges()

                val noteDataDict =
                    noteSaveHelper.getNoteDataDict(noteViewBinding.noteTitleET,
                        noteViewBinding.noteEditContentET, noteViewBinding.modifiedDateTV,
                        noteViewBinding.pinButtonIV)

                if (!(noteSaveHelper.noteHasNotChanged(retrievedNote, noteDataDict))) {
                    GeneralUIHelper.displayFeedbackToast(this@NoteViewActivity,
                        "Changes Saved", false)
                } else {
                    GeneralUIHelper.displayFeedbackToast(this@NoteViewActivity,
                        "Up to date", false)
                }
            }

            saveCloseButtonIV.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(this@NoteViewActivity,
                    saveCloseButtonIV)

                closeNote = true

                commitNoteChanges()

                finish()
            }


            val backButtonPressedCallback =
                object : OnBackPressedCallback(true) {
                    // Custom logic for back button press
                    override fun handleOnBackPressed() {

                        closeNote = true

                        commitNoteChanges()

                        finish()
                    }
                }
            onBackPressedDispatcher.addCallback(this@NoteViewActivity,
                backButtonPressedCallback)

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

            dialogConfirmStatus.observe(this@NoteViewActivity){ status ->

                if (status == true && existingNoteClicked) {
                    noteAppSharedViewModel.deleteNote(noteID)
                    setDialogConfirmStatus(false)
                }

                if (status == true && !existingNoteClicked && newNoteBackup.value != null) {
                    noteAppSharedViewModel.deleteNote(newNoteBackup.value!!.noteID)
                    setNewNoteBackup(null)
                    setDialogConfirmStatus(false)
                }
            }

            newNoteBackup.observe(this@NoteViewActivity) { note ->

                updateRetrievedNote()
                if (note != null) setCurrentOpenNoteID(note.noteID)
            }

            runningAutoSave.observe(this@NoteViewActivity) { isTrue ->

                val noteDataDict =
                    noteSaveHelper.getNoteDataDict(noteViewBinding.noteTitleET,
                        noteViewBinding.noteEditContentET, noteViewBinding.modifiedDateTV,
                        noteViewBinding.pinButtonIV)

                if (isTrue && !(noteSaveHelper.noteHasNotChanged(retrievedNote, noteDataDict))) {
                    GeneralUIHelper.displayFeedbackToast(this@NoteViewActivity,
                        "Auto saved", false)
                }
            }

        }

    }

    private fun observeInputVMValues() {

        inputSharedViewModel.apply {

            noteContentIsEditing.observe(this@NoteViewActivity){ isTrue ->

                noteViewBinding.noteEditContentET.isCursorVisible = isTrue

                if (isTrue) {
                    noteSaveHelper.runAutoSave { commitNoteChanges() }
                } else {
                    noteSaveHelper.stopAutoSave()
                }
            }

            shouldUpdateModifiedDate.observe(this@NoteViewActivity) { shouldUpdate ->

                if (shouldUpdate) {

                    updateModifiedDate()
                    commitNoteChanges()
                }
            }

        }
    }



    private fun finalNoteFeedback() {

        noteAppSharedViewModel.noteWasCreated.observe(this){ isTrue ->

            val runningAutoSave = noteAppSharedViewModel.runningAutoSave.value!!

            if (isTrue && closeNote && !runningAutoSave) {

                GeneralUIHelper.closeActivityWithFeedback("Note Created", this,
                    this, true)
            }
        }

        noteAppSharedViewModel.noteWasUpdated.observe(this){ isTrue ->

            val runningAutoSave = noteAppSharedViewModel.runningAutoSave.value!!

            if (isTrue && closeNote && !runningAutoSave) {

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