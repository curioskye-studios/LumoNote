package com.ckestudios.lumonote.ui.noteview.view

import android.os.Bundle
import android.text.method.TextKeyListener
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.data.database.NoteRepository
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.data.models.TextSize
import com.ckestudios.lumonote.databinding.ActivityNoteViewBinding
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.ckestudios.lumonote.ui.sharedviewmodel.AppSharedViewFactory
import com.ckestudios.lumonote.ui.sharedviewmodel.NoteAppSharedViewModel
import com.ckestudios.lumonote.utils.helpers.BasicUtilityHelper
import com.ckestudios.lumonote.utils.helpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.helpers.GeneralTextHelper
import com.ckestudios.lumonote.utils.helpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.textformatting.TextSpanChecker
import java.time.LocalDate


class NoteViewActivity : AppCompatActivity() {

    private lateinit var noteViewBinding: ActivityNoteViewBinding
    private val generalTextHelper: GeneralTextHelper = GeneralTextHelper()
    private val generalButtonIVHelper: GeneralButtonIVHelper = GeneralButtonIVHelper()

    // Stores reference to id of current note being updated, stays -1 if not found
    private var noteID: Int = -1
    private var existingNoteClicked: Boolean = false
    private lateinit var retrievedNote: Note

    private val basicUtilityHelper = BasicUtilityHelper()
    private val generalUIHelper = GeneralUIHelper()

    private lateinit var inputSharedViewModel: InputSharedViewModel
    private lateinit var editContentSharedViewModel: EditContentSharedViewModel
    private lateinit var noteAppSharedViewModel: NoteAppSharedViewModel
    private var editInputFragment: EditInputFragment = EditInputFragment()

    private lateinit var textSpanChecker: TextSpanChecker


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        noteViewBinding = ActivityNoteViewBinding.inflate(layoutInflater)
        setContentView(noteViewBinding.root)


        // Set up view models
        var noteRepository = NoteRepository(this)
        var appSharedViewFactory = AppSharedViewFactory(noteRepository)

        noteAppSharedViewModel = ViewModelProvider(this, appSharedViewFactory)
            .get(NoteAppSharedViewModel::class.java)

        inputSharedViewModel = ViewModelProvider(this).get(InputSharedViewModel::class.java)

        editContentSharedViewModel =
            ViewModelProvider(this).get(EditContentSharedViewModel::class.java)
        editContentSharedViewModel.setNoteContentEditTextView(noteViewBinding.noteEditContentET)


        // Check if working with existing note
        val isExistingNote = checkIfIsExistingNote()
        if (isExistingNote == null) {

            finish()
            return
        } else {

            populateUIWithNoteData(isExistingNote)
        }


        // Setup Functionality

        textSpanChecker = TextSpanChecker(noteViewBinding.noteEditContentET)

        basicUtilityHelper.clearETViewFocusOnHideKeyboard(noteViewBinding.noteTitleET,
            noteViewBinding.root)
        basicUtilityHelper.clearETViewFocusOnHideKeyboard(noteViewBinding.noteEditContentET,
            noteViewBinding.root)

        notifyIfEditing()

        detectSelectionFormattingOnChange()

        setOnClickListeners()

        observeNoteAppVMValues()

        observeInputVMValues()

        finalNoteFeedback()
    }


    private fun notifyIfEditing() {

        // For removing text formatter when text content not being edited
        noteViewBinding.noteEditContentET.setOnFocusChangeListener { view, hasFocus ->

            inputSharedViewModel.setNoteContentIsEditing(hasFocus)
        }
    }

    private fun detectSelectionFormattingOnChange() {

        noteViewBinding.noteEditContentET.onSelectionChange = { selectStart, selectEnd ->

            if (selectStart == selectEnd) {

                inputSharedViewModel.setContentSelectionIsEmpty(true)
            }
            else {

                inputSharedViewModel.setContentSelectionIsEmpty(false)
            }

            textSpanChecker.apply {

                setSelection(selectStart, selectEnd)

                when (getTextSizingType()) {

                    TextSize.H1 -> editContentSharedViewModel.setIsHeader1Sized(true)
                    TextSize.H2 -> editContentSharedViewModel.setIsHeader2Sized(true)
                    TextSize.NORMAL -> editContentSharedViewModel.setIsNormalSized(true)
                }
            }

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

            // Get existing note
            retrievedNote = noteAppSharedViewModel.getNote(noteID)
        }

        return existingNoteClicked
    }


    private fun populateUIWithNoteData(usingExistingNote: Boolean) {

        if (usingExistingNote) {

            Log.d("populateUIWithNoteData", retrievedNote.noteModifiedDate)

            // Parse the modified date as a date object
            val convertedDate = LocalDate.parse(retrievedNote.noteModifiedDate);
            val retrievedNoteDate = generalTextHelper.formatDate(convertedDate)

            Log.d("noteData", retrievedNote.notePinned.toString())

            // Populate the view note activity UI w/ the pre-existing note data
            noteViewBinding.modifiedDateTV.text = retrievedNoteDate
            noteViewBinding.noteTitleET.setText(retrievedNote.noteTitle)
            noteViewBinding.noteEditContentET.setText(retrievedNote.noteContent)

            noteViewBinding.pinButtonIV.tag = retrievedNote.notePinned

            generalButtonIVHelper.updatePinHighlight(noteViewBinding.pinButtonIV, this)
        }

        else {

            // Display the modified date as current date
            noteViewBinding.modifiedDateTV.text = generalTextHelper.formatDate(LocalDate.now())

            // set isPinned to false
            noteViewBinding.pinButtonIV.tag = false
        }
    }

    private fun setOnClickListeners(){

        noteViewBinding.apply {

            backButtonIV.setOnClickListener {

                collectNoteData()

                finish()
            }


            deleteButtonIV.setOnClickListener {

                noteAppSharedViewModel.deleteNote(noteID)
            }


            pinButtonIV.setOnClickListener {

                val pinnedFlag = pinButtonIV.tag as Boolean

                pinButtonIV.tag = !pinnedFlag

                noteAppSharedViewModel.updateCurrentPinStatus(!pinnedFlag)
            }


            saveButtonIV.setOnClickListener {

                collectNoteData()
            }


            val backButtonPressedCallback =
                object : OnBackPressedCallback(true) {

                    override fun handleOnBackPressed() {
                        // Custom logic for back button press
                        collectNoteData()
                    }
                }
            onBackPressedDispatcher.addCallback(this@NoteViewActivity, backButtonPressedCallback)

        }


    }


    private fun observeNoteAppVMValues() {

        noteAppSharedViewModel.apply {

            currentNotePinned.observe(this@NoteViewActivity){ currentlyPinned ->

                generalButtonIVHelper.updatePinHighlight(noteViewBinding.pinButtonIV,
                    this@NoteViewActivity)

                val pinStateFeedback =
                    if (currentlyPinned) { "Note Pinned" }

                    else { "Note Unpinned" }

                generalUIHelper.displayFeedbackToast(this@NoteViewActivity,
                    pinStateFeedback, false)
            }
        }

    }

    private fun observeInputVMValues() {

        inputSharedViewModel.apply {

            noteContentIsEditing.observe(this@NoteViewActivity){

                if (it == true) {

                    noteViewBinding.noteEditContentET.isCursorVisible = true          // show cursor
                    noteViewBinding.noteEditContentET.keyListener =
                        TextKeyListener.getInstance()  // re-enable typing
                } else {

                    noteViewBinding.noteEditContentET.isCursorVisible = false      // hide cursor
                    noteViewBinding.noteEditContentET.keyListener = null           // disables input method
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

        var created = currentDate
        var modified = currentDate


        // If an existing note was clicked on rather than the create button
        if (existingNoteClicked) {

            noteAppSharedViewModel.setIsNewNote(false)

            var updatedNote = Note(retrievedNote.noteID, title, content, retrievedNote.noteCreatedDate,
                modified, pinned
            )

            noteAppSharedViewModel.saveNote(updatedNote)

        } else {

            noteAppSharedViewModel.setIsNewNote(true)

            var newNote = Note(0, title, content, created, modified, pinned)

            noteAppSharedViewModel.saveNote(newNote)
        }
    }



    private fun finalNoteFeedback() {

        noteAppSharedViewModel.noteWasCreated.observe(this){

            if (it == true) {

                generalUIHelper.closeActivityWithFeedback("Note Created", this,
                    this, true)
            }
        }

        noteAppSharedViewModel.noteWasUpdated.observe(this){

            if (it == true) {

                generalUIHelper.closeActivityWithFeedback("Note Updated", this,
                    this, true)
            }
        }

        noteAppSharedViewModel.noteWasDeleted.observe(this){

            if (it == true) {

                generalUIHelper.closeActivityWithFeedback("Note Deleted", this,
                    this, true)
            }
        }
    }

}