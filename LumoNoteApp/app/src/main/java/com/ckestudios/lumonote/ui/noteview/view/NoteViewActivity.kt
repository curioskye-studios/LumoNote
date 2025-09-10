package com.ckestudios.lumonote.ui.noteview.view

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.data.database.DatabaseHelper
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.data.models.TextSize
import com.ckestudios.lumonote.data.models.TextStyle
import com.ckestudios.lumonote.databinding.ActivityNoteViewBinding
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.ckestudios.lumonote.ui.sharedviewmodel.AppSharedViewFactory
import com.ckestudios.lumonote.ui.sharedviewmodel.NoteAppSharedViewModel
import com.ckestudios.lumonote.utils.general.BasicUtilityHelper
import com.ckestudios.lumonote.utils.general.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.general.GeneralTextHelper
import com.ckestudios.lumonote.utils.general.GeneralUIHelper
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


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        noteViewBinding = ActivityNoteViewBinding.inflate(layoutInflater)
        setContentView(noteViewBinding.root)


        // Set up view models
        var dbConnection = DatabaseHelper(this)
        var appSharedViewFactory = AppSharedViewFactory(dbConnection)

        noteAppSharedViewModel = ViewModelProvider(this, appSharedViewFactory)
            .get(NoteAppSharedViewModel::class.java)

        inputSharedViewModel = ViewModelProvider(this).get(InputSharedViewModel::class.java)

        editContentSharedViewModel = ViewModelProvider(this).get(EditContentSharedViewModel::class.java)
        editContentSharedViewModel.setNoteContentEditTextView(noteViewBinding.noteContentET)


        // Check if working with existing note
        val isExistingNote = checkIfIsExistingNote()
        if (isExistingNote == null) {

            finish()
            return
        } else {

            populateUIWithNoteData(isExistingNote)
        }


        // Setup Functionality

        basicUtilityHelper.clearETViewFocusOnHideKeyboard(noteViewBinding.noteTitleET,
            noteViewBinding.root)
        basicUtilityHelper.clearETViewFocusOnHideKeyboard(noteViewBinding.noteContentET,
            noteViewBinding.root)

        notifyIfEditing()

        detectSelectionFormattingOnChange()

        setOnClickListeners()

        observeNoteAppVMValues()

        finalNoteFeedback()
    }


    private fun notifyIfEditing() {

        // For removing text formatter when text content not being edited
        noteViewBinding.noteContentET.setOnFocusChangeListener {_, hasFocus ->

            inputSharedViewModel.setNoteContentIsEditing(hasFocus)

//            Log.d("textFormatButtonEditingContent",
//                "content:" + inputSharedViewModel.noteContentIsEditing.value.toString())

        }
    }

    private fun detectSelectionFormattingOnChange() {

        noteViewBinding.noteContentET.onSelectionChange = { selectStart, selectEnd ->

            noteViewBinding.noteContentET.getSpanChecker().apply {

                setSelection(selectStart, selectEnd)

                when (getTextSizingType()) {

                    TextSize.H1 -> editContentSharedViewModel.setIsHeader1Sized(true)
                    TextSize.H2 ->  editContentSharedViewModel.setIsHeader2Sized(true)
                    TextSize.NORMAL ->  editContentSharedViewModel.setIsNormalSized(true)
                }

                val styleIsPresentValues =
                    getTextStylePresentValues(noteViewBinding.noteContentET.getStyleHelper())

                val isBold = styleIsPresentValues[TextStyle.BOLD] as Boolean
                val isItalics = styleIsPresentValues[TextStyle.ITALICS] as Boolean
                val isUnderlined = styleIsPresentValues[TextStyle.UNDERLINE] as Boolean

                editContentSharedViewModel.setIsBold(isBold)
                editContentSharedViewModel.setIsItalics(isItalics)
                editContentSharedViewModel.setIsUnderlined(isUnderlined)
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
            noteViewBinding.noteContentET.setText(retrievedNote.noteContent)

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

        noteViewBinding.backButtonIV.setOnClickListener {

            collectNoteData()

            finish()
        }


        // Calls reference to the button of id deleteButton in note_item.xml
        noteViewBinding.deleteButtonIV.setOnClickListener {

            noteAppSharedViewModel.deleteNote(noteID)
        }


        // Calls reference to the button of id pinButton in activity_note_view.xml
        noteViewBinding.pinButtonIV.setOnClickListener {

            val pinnedFlag = noteViewBinding.pinButtonIV.tag as Boolean

            noteViewBinding.pinButtonIV.tag = !pinnedFlag

            noteAppSharedViewModel.updateCurrentPinStatus(!pinnedFlag)
        }


        // Calls reference to the save button of id saveButton in activity_note_view.xml
        noteViewBinding.saveButtonIV.setOnClickListener {

            collectNoteData()
        }


        val backButtonPressedCallback =
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {
                    // Custom logic for back button press
                    collectNoteData()
                }
            }
        onBackPressedDispatcher.addCallback(this, backButtonPressedCallback)
    }


    private fun observeNoteAppVMValues() {

        noteAppSharedViewModel.currentNotePinned.observe(this){

            generalButtonIVHelper.updatePinHighlight(noteViewBinding.pinButtonIV, this)

            val pinStateFeedback =
                if (it == true) { "Note Pinned" }

                else { "Note Unpinned" }

            generalUIHelper.displayFeedbackToast(this, pinStateFeedback, false)
        }
    }



    // Submits note to database upon clicking save button
    private fun collectNoteData() {

        // Collect data from input fields, store in note object
        val title =  noteViewBinding.noteTitleET.text.toString()
        val content =  noteViewBinding.noteContentET.text.toString()
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