package com.ckestudios.lumonote.ui.home.notepreview.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ckestudios.lumonote.data.database.DatabaseHelper
import com.ckestudios.lumonote.databinding.FragmentNotePreviewViewBinding
import com.ckestudios.lumonote.ui.noteview.view.NoteViewActivity
import com.ckestudios.lumonote.ui.sharedviewmodel.AppSharedViewFactory
import com.ckestudios.lumonote.ui.sharedviewmodel.NoteAppSharedViewModel
import com.ckestudios.lumonote.ui.sharedviewmodel.TagAppSharedViewModel

class NotePreviewViewFragment : Fragment() {

    // Real binding variable that can be null when the view is destroyed
    // Naming it with an underscore (_notePrevViewBinding) is just a convention
    // → It signals: "don’t use me directly, I’m just the backing field"
    private var _notePrevViewBinding: FragmentNotePreviewViewBinding? = null

    // Safe-to-use version of binding
    // Uses Kotlin’s getter so we don’t need to write _notePrevViewBinding!! everywhere
    // The "!!" means it assumes _notePrevViewBinding is not null between onCreateView & onDestroyView
    private val notePrevViewBinding get() = _notePrevViewBinding!!

    private lateinit var notePreviewAdapter: NotePreviewAdapter
    private lateinit var tagDisplayAdapter: TagDisplayAdapter

    private lateinit var tagAppSharedViewModel: TagAppSharedViewModel
    private lateinit var noteAppSharedViewModel: NoteAppSharedViewModel


    // Called when the Fragment creates its view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {

        // Inflate the layout using ViewBinding
        _notePrevViewBinding = FragmentNotePreviewViewBinding.inflate(inflater, container, false)
        return notePrevViewBinding.root // return the root view for the fragment
    }

    // Called when the Fragment is created (before the UI exists)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val dbConnection = DatabaseHelper(requireContext()) // DB
        val appSharedVMConstructor = AppSharedViewFactory(dbConnection) // Factory

        // Custom ViewModelProviders know how to build viewmodels w/ dbconnection dependency
        noteAppSharedViewModel = ViewModelProvider(this, appSharedVMConstructor)
            .get(NoteAppSharedViewModel::class.java)

        tagAppSharedViewModel = ViewModelProvider(this, appSharedVMConstructor)
            .get(TagAppSharedViewModel::class.java)
    }


    // Called when the view is created (safe place to interact with UI)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        /*
        * In an Activity, you can often use "requireContext()" as the context because
        * an Activity is a subclass of Context.
        * But in a Fragment, requireContext() refers to the Fragment itself, not an Activity or Context.
        * Since Fragment does not inherit from Context, the compiler complains.
        *
        * That’s why you need to explicitly get a Context or Activity from the Fragment:
        * requireContext() → safe way to get the Context (will throw if Fragment isn’t attached).
        */

        initializeAdapters()

        setupAdapterDisplays()

        setupListeners()

        observeNoteAppVMValues()

        observeTagAppVMValues()
    }

    override fun onResume() {
        super.onResume()

        noteAppSharedViewModel.loadAllNotes()
    }


    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {

        super.onDestroyView()
        _notePrevViewBinding = null // prevent memory leaks by clearing reference
    }


    private fun initializeAdapters() {

        notePreviewAdapter = NotePreviewAdapter (
            setNoteIDToOpen =
                { noteID ->

                    // notePreviewAdapter takes in a function as a parameter
                    // this is the functionality assigned whenever this runs in the adapter
                    openNoteViewActivity(noteID)
                },
            whenCurrentNotePinClicked =
                { isPinned, currentNoteID ->

//                    Log.d("NoteFrag", isPinned.toString())
//                    Log.d("NoteFrag", currentNoteID.toString())

                    noteAppSharedViewModel.setCurrentPreviewNoteID(currentNoteID)
                    noteAppSharedViewModel.updatePreviewPinStatus(isPinned)
                }
        )

        tagDisplayAdapter = TagDisplayAdapter (

            onTagClickedFunction =
            { position ->

                tagAppSharedViewModel.setCurrentTagPosition(position)
            }
        )
    }


    private fun setupAdapterDisplays() {

        // Define layout and adapter to use for notes display
        notePrevViewBinding.notesPreviewRV.layoutManager = StaggeredGridLayoutManager(2,
            StaggeredGridLayoutManager.VERTICAL)

        notePrevViewBinding.notesPreviewRV.adapter = notePreviewAdapter


        // Define layout and adapter to use for tag display
        notePrevViewBinding.tagsHolderRV.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)

        //notePrevViewBinding.tagsHolderRV.setHasFixedSize(true) // optional but avoids measurement issues
        notePrevViewBinding.tagsHolderRV.adapter = tagDisplayAdapter
    }



    private fun setupListeners() {

        // Calls reference to the create note floating button
        notePrevViewBinding.createButtonIV.setOnClickListener {

            var intent = Intent(requireContext(), NoteViewActivity::class.java)
            startActivity(intent)
        }

        // Add scroll listener to ensure add tag button scrolls with the tags
        notePrevViewBinding.tagsHolderRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                super.onScrolled(recyclerView, dx, dy)

                // Move the Add button by same scroll distance
                notePrevViewBinding.tagAddButtonIV.translationX =
                    -recyclerView.computeHorizontalScrollOffset().toFloat()
            }
        })
    }

    private fun openNoteViewActivity(noteID: Int) {

        // Open Note View of note by clicking on one
        val intent = Intent(requireContext(), NoteViewActivity::class.java).apply {
            // Also pass in the id of the note interacted w/ for later retrieval in note view
            putExtra("note_id", noteID)
        }

        // Starts the update note activity
        requireContext().startActivity(intent)
    }


    private fun observeNoteAppVMValues() {

        // Observe changes
        noteAppSharedViewModel.notes.observe(viewLifecycleOwner) { notes ->
            notePreviewAdapter.refreshData(notes)
        }

        // Observe changes
        noteAppSharedViewModel.notifyRefresh.observe(viewLifecycleOwner) { shouldRefresh ->

            if (shouldRefresh == true) {
                noteAppSharedViewModel.loadAllNotes()
            }
        }

        // Observe changes
        noteAppSharedViewModel.previewNotePinned.observe(viewLifecycleOwner) { isPinned ->

            //update note in database with new pinned status
            if (noteAppSharedViewModel.currentPreviewNoteID.value != -1) {

                var noteData = noteAppSharedViewModel.getNote(
                    noteAppSharedViewModel.currentPreviewNoteID.value!!
                )

                noteData.notePinned = isPinned

                Log.d("NoteFrag", "$noteData")

                noteAppSharedViewModel.setIsNewNote(false)
                noteAppSharedViewModel.saveNote(noteData)

                noteAppSharedViewModel.setCurrentPreviewNoteID(-1)
            }

            givePinnedStatusToast(isPinned)

            noteAppSharedViewModel.setNotifyRefresh(true)
            noteAppSharedViewModel.setNotifyRefresh(false)
        }
    }


    private fun observeTagAppVMValues() {

        // Observe changes
        tagAppSharedViewModel.tags.observe(viewLifecycleOwner) { tags ->
            tagDisplayAdapter.refreshData(tags)
        }

        // Observe selection
        tagAppSharedViewModel.selectedTagPosition.observe(viewLifecycleOwner) { position ->
            tagDisplayAdapter.setSelectedPosition(position)
        }
    }


    private fun givePinnedStatusToast(isPinned: Boolean) {

        if (isPinned) {

            // Put small notification popup at bottom of screen
            Toast.makeText(requireContext(), "Note Pinned", Toast.LENGTH_SHORT).show()
        } else {

            // Put small notification popup at bottom of screen
            Toast.makeText(requireContext(), "Note Unpinned", Toast.LENGTH_SHORT).show()
        }
    }


}
