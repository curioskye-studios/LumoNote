package com.curioskyestudios.lumonote.ui.noteview.view

import android.os.Bundle
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.getSpans
import androidx.lifecycle.ViewModelProvider
import com.curioskyestudios.lumonote.R
import com.curioskyestudios.lumonote.data.database.DatabaseHelper
import com.curioskyestudios.lumonote.data.models.Note
import com.curioskyestudios.lumonote.databinding.ActivityNoteViewBinding
import com.curioskyestudios.lumonote.ui.noteview.other.NoteViewFactory
import com.curioskyestudios.lumonote.ui.noteview.viewmodel.InputViewModel
import com.curioskyestudios.lumonote.ui.noteview.viewmodel.NoteViewModel
import com.curioskyestudios.lumonote.utils.general.GeneralTextHelper
import com.curioskyestudios.lumonote.utils.general.GeneralUIHelper
import com.curioskyestudios.lumonote.utils.texthelper.TextBulletHelper
import com.curioskyestudios.lumonote.utils.texthelper.TextSizeHelper
import com.curioskyestudios.lumonote.utils.texthelper.TextStyleHelper
import java.time.LocalDate


class NoteViewActivity : AppCompatActivity() {

    private lateinit var noteViewBinding: ActivityNoteViewBinding
    private lateinit var textStyleHelper: TextStyleHelper
    private lateinit var textSizeHelper: TextSizeHelper
    private lateinit var textBulletHelper: TextBulletHelper
    private val generalTextHelper: GeneralTextHelper = GeneralTextHelper()
    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()

    // Stores reference to id of current note being updated, stays -1 if not found
    private var noteID: Int = -1
    private var existingNoteClicked: Boolean = false
    private lateinit var retrievedNote: Note

    private lateinit var inputViewModel: InputViewModel
    private lateinit var noteViewModel: NoteViewModel
    private var editInputFragment: EditInputFragment = EditInputFragment()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        noteViewBinding = ActivityNoteViewBinding.inflate(layoutInflater)
        setContentView(noteViewBinding.root)

        clearETViewFocusOnHideKeyboard()

        notifyIfEditingNoteContent()

        setupViewModels()

        setupTextHelpers()


        supportFragmentManager.beginTransaction().apply {

            replace(noteViewBinding.editSectionFC.id, editInputFragment)
        }


        existingNoteClicked = intent.hasExtra("note_id")

        // If an existing note was clicked on rather than the create button
        if (existingNoteClicked) {

            // Retrieve the data passed in from NotesAdapter.kt
            noteID = intent.getIntExtra("note_id", -1)

            // If no note found, exit activity
            if (noteID == -1) {
                finish()
                return
            }

            // Get existing note
            retrievedNote = noteViewModel.getNote(noteID)

            populateUIWithNoteData()

        } else {

            // Display the modified date as current date
            noteViewBinding.modifiedDateTV.text = generalTextHelper.formatDate(LocalDate.now())

            // set isPinned to false
            noteViewBinding.pinButtonIV.tag = false
        }


        setOnClickListeners()

        observeViewModels()

        noteViewBinding.noteContentET.onSelectionChange = { start, end ->
            updateSelectionAndSpans(start, end)
        }
    }


    private fun clearETViewFocusOnHideKeyboard(){
        noteViewBinding.noteContentET.clearFocusOnKeyboardHide(noteViewBinding.root)
        noteViewBinding.noteTitleET.clearFocusOnKeyboardHide(noteViewBinding.root)
    }

    private fun notifyIfEditingNoteContent() {
        // For removing text formatter when text content not being edited
        noteViewBinding.noteContentET.setOnFocusChangeListener {_, hasFocus ->
            inputViewModel.setEditing(hasFocus)
        }
    }

    private fun setupViewModels() {

        var dbConnection = DatabaseHelper(this)
        var noteViewFactory = NoteViewFactory(dbConnection)
        noteViewModel = ViewModelProvider(this, noteViewFactory).get(NoteViewModel::class.java)

        inputViewModel = ViewModelProvider(this).get(InputViewModel::class.java)
    }

    private fun setupTextHelpers() {

        textStyleHelper = TextStyleHelper(noteViewBinding.noteContentET)
        textSizeHelper = TextSizeHelper(noteViewBinding.noteContentET)
        textBulletHelper = TextBulletHelper(noteViewBinding.noteContentET)

        inputViewModel.setTextStyleHelper(textStyleHelper)
        inputViewModel.setTextSizeHelper(textSizeHelper)
        inputViewModel.setTextBulletHelper(textBulletHelper)
    }


    private fun populateUIWithNoteData() {

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
        updatePinHighlight()
    }


    private fun observeViewModels() {

        noteViewModel.noteWasCreated.observe(this){

            if (noteViewModel.noteWasCreated.value == true) {

                // Closes view note activity, pops from activity stack, returns to main below it
                finish()

                // Put small notification popup at bottom of screen
                Toast.makeText(this, "Note Created", Toast.LENGTH_LONG).show()
            }
        }


        noteViewModel.noteWasUpdated.observe(this){

            if (noteViewModel.noteWasUpdated.value == true) {

                /// Closes view note activity, pops from activity stack, returns to main below it
                finish()

                // Put small notification popup at bottom of screen
                Toast.makeText(this, "Changes Saved", Toast.LENGTH_LONG).show()
            }
        }

        noteViewModel.noteWasDeleted.observe(this){

            if (noteViewModel.noteWasDeleted.value == true) {

                finish()

                // Put small notification popup at bottom of screen
                Toast.makeText(this, "Note Deleted", Toast.LENGTH_SHORT).show()
            }
        }

        noteViewModel.currentNotePinned.observe(this){

            updatePinHighlight()

            if (noteViewModel.currentNotePinned.value == true) {

                // Put small notification popup at bottom of screen
                Toast.makeText(this, "Note Pinned", Toast.LENGTH_SHORT).show()
            } else {

                // Put small notification popup at bottom of screen
                Toast.makeText(this, "Note Unpinned", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setOnClickListeners(){

        noteViewBinding.backButtonIV.setOnClickListener {

            collectNoteData()

            finish()
        }


        // Calls reference to the button of id deleteButton in note_item.xml
        noteViewBinding.deleteButtonIV.setOnClickListener {

            noteViewModel.deleteNote(noteID)
        }


        // Calls reference to the button of id pinButton in activity_note_view.xml
        noteViewBinding.pinButtonIV.setOnClickListener {

            val pinnedFlag = noteViewBinding.pinButtonIV.tag as Boolean

            noteViewBinding.pinButtonIV.tag = !pinnedFlag

            noteViewModel.updateCurrentPinStatus(!pinnedFlag)
        }


        // Calls reference to the save button of id saveButton in activity_note_view.xml
        noteViewBinding.saveButtonIV.setOnClickListener {

            collectNoteData()
        }


        val backButtonPressedCallback = object : OnBackPressedCallback(true) {

            override fun handleOnBackPressed() {
                // Custom logic for back button press
                collectNoteData()
            }
        }
        onBackPressedDispatcher.addCallback(this, backButtonPressedCallback)
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

            noteViewModel.setIsNewNote(false)

            var updatedNote = Note(retrievedNote.noteID, title, content, retrievedNote.noteCreatedDate,
                modified, pinned
            )

            noteViewModel.saveNote(updatedNote)

        } else {

            noteViewModel.setIsNewNote(true)

            var newNote = Note(0, title, content, created, modified, pinned)

            noteViewModel.saveNote(newNote)
        }
    }


    private fun updateSelectionAndSpans(selectStart: Int, selectEnd: Int) {

        val noteContent = noteViewBinding.noteContentET
        val selected = noteContent.text?.substring(selectStart, selectEnd)
        Log.d("Selection", "Selected: $selected")

        val styleSpans = noteContent.text?.getSpans<StyleSpan>(selectStart, selectEnd)
        val underlineSpans =
            noteContent.text?.getSpans<TextStyleHelper.CustomUnderlineSpan>(selectStart, selectEnd)

        val relativeSizeSpans = noteContent.text?.getSpans(selectStart,
            selectEnd, RelativeSizeSpan::class.java)
        Log.d("relativeSizeSpan", relativeSizeSpans?.contentToString() ?: "null")

        inputViewModel.setSelectionStart(selectStart)
        inputViewModel.setSelectionEnd(selectEnd)

        inputViewModel.setStyleSpans(styleSpans)
        inputViewModel.setUnderlineSpans(underlineSpans)
        inputViewModel.setRelativeSizeSpans(relativeSizeSpans)

    }


    private fun updatePinHighlight(){

        if (noteViewBinding.pinButtonIV.tag == true) {

            generalUIHelper.changeButtonIVColor(this, noteViewBinding.pinButtonIV,
                R.color.gold)
        } else {

            generalUIHelper.changeButtonIVColor(this, noteViewBinding.pinButtonIV,
                R.color.light_grey_3)
        }
    }

}