package com.example.lumonote.ui.home.notepreview.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.lumonote.data.database.DatabaseHelper
import com.example.lumonote.databinding.FragmentNotePreviewViewBinding
import com.example.lumonote.ui.home.notepreview.other.NotePreviewViewFactory
import com.example.lumonote.ui.home.notepreview.other.TagViewModelFactory
import com.example.lumonote.ui.home.notepreview.viewmodel.NotePreviewViewModel
import com.example.lumonote.ui.home.notepreview.viewmodel.TagViewModel
import com.example.lumonote.ui.noteview.view.NoteViewActivity

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

    private lateinit var notePreviewViewModel: NotePreviewViewModel
    private lateinit var tagViewModel: TagViewModel


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
        val tagViewModelConstructor = TagViewModelFactory(dbConnection)  // Factory
        val notePreviewViewModelConstructor = NotePreviewViewFactory(dbConnection)  // Factory

        // Custom ViewModelProviders know how to build viewmodels w/ dbconnection dependency
        tagViewModel = ViewModelProvider(this, tagViewModelConstructor).get(TagViewModel::class.java)

        notePreviewViewModel = ViewModelProvider(this,
            notePreviewViewModelConstructor).get(NotePreviewViewModel::class.java)
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

        observeViewModels()

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
            shouldHighlightNotePin =
                {

                },
            whenCurrentNotePinClicked =
                { isPinned ->
                    //notePreviewViewModel.updateIsCurrentNotePinned(isPinned)
                }
        )

        tagDisplayAdapter = TagDisplayAdapter (
            onTagClickedFunction =
            { position ->
                tagViewModel.setCurrentTagPosition(position)
            }
        )
    }

    private fun openNoteViewActivity(noteID: Int) {

        // Open Note View of note by clicking on one
        val intent = Intent(requireContext(), NoteViewActivity::class.java).apply {
            // Also pass in the id of the note interacted w/ for later retrieval in update note
            putExtra("note_id", noteID)
        }

        // Starts the update note activity
        requireContext().startActivity(intent)
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

    private fun observeViewModels() {

        // Observe changes
        notePreviewViewModel.notes.observe(viewLifecycleOwner) { notes ->
            notePreviewAdapter.refreshData(notes)
        }

        // Observe changes
        tagViewModel.tags.observe(viewLifecycleOwner) { tags ->
            tagDisplayAdapter.refreshData(tags)
        }

        // Observe selection
        tagViewModel.selectedTagPosition.observe(viewLifecycleOwner) { position ->
            tagDisplayAdapter.setSelectedPosition(position)
        }
    }


}
