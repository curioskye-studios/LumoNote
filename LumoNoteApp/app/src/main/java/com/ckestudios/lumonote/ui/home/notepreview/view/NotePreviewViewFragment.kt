package com.ckestudios.lumonote.ui.home.notepreview.view

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.data.models.Note
import com.ckestudios.lumonote.data.repository.NoteRepository
import com.ckestudios.lumonote.data.repository.TagRepository
import com.ckestudios.lumonote.data.repository.TaggedRepository
import com.ckestudios.lumonote.databinding.FragmentNotePreviewViewBinding
import com.ckestudios.lumonote.ui.home.notepreview.viewmodel.NotePrevViewModel
import com.ckestudios.lumonote.ui.noteview.view.NoteViewActivity
import com.ckestudios.lumonote.ui.sharedviewmodel.AppSharedViewFactory
import com.ckestudios.lumonote.ui.sharedviewmodel.NoteAppSharedViewModel
import com.ckestudios.lumonote.ui.sharedviewmodel.TagAppSharedViewModel
import com.ckestudios.lumonote.ui.sharedviewmodel.TaggedAppSharedViewModel
import com.ckestudios.lumonote.ui.tagview.view.TagViewActivity
import com.ckestudios.lumonote.utils.helpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.helpers.GeneralUIHelper

class NotePreviewViewFragment : Fragment() {

    // Real binding variable that can be null when the view is destroyed
    // Naming it with an underscore (_notePrevViewBinding) is just a convention
    // → It signals: "don’t use me directly, I’m just the backing field"
    private var _notePrevViewBinding: FragmentNotePreviewViewBinding? = null

    // Safe-to-use version of binding
    // Uses Kotlin’s getter so we don’t need to write _notePrevViewBinding!! everywhere
    // The "!!" means it assumes _notePrevViewBinding is not null between onCreateView & onDestroyView
    private val notePrevViewBinding get() = _notePrevViewBinding!!

    private lateinit var unpinnedNotePrevAdapter: NotePreviewAdapter
    private lateinit var tagDisplayAdapter: TagDisplayAdapter
    private lateinit var pinnedNotePrevAdapter: NotePreviewAdapter

    private lateinit var notePrevViewModel: NotePrevViewModel
    private lateinit var tagAppSharedViewModel: TagAppSharedViewModel
    private lateinit var noteAppSharedViewModel: NoteAppSharedViewModel
    private lateinit var taggedAppSharedViewModel: TaggedAppSharedViewModel


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

        notePrevViewModel = ViewModelProvider(this).get(NotePrevViewModel::class.java)

        val app: Application = requireActivity().application

        val noteRepository = NoteRepository(requireContext()) // DB
        val tagRepository = TagRepository(requireContext())
        val taggedRepository = TaggedRepository(requireContext())

        // Custom ViewModelProviders know how to build viewmodels w/ dbconnection dependency
        noteAppSharedViewModel = ViewModelProvider(requireActivity(),
            AppSharedViewFactory(app, noteRepository)).get(NoteAppSharedViewModel::class.java)
        tagAppSharedViewModel = ViewModelProvider(requireActivity(),
            AppSharedViewFactory(app, tagRepository)).get(TagAppSharedViewModel::class.java)
        taggedAppSharedViewModel = ViewModelProvider(requireActivity(),
            AppSharedViewFactory(app, taggedRepository)).get(TaggedAppSharedViewModel::class.java)
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

        observeNotePrevVMValues()

        observeNoteAppVMValues()

        observeTagAppVMValues()
    }

    override fun onResume() {
        super.onResume()

        noteAppSharedViewModel.loadAllNotes()
        tagAppSharedViewModel.loadAllTags()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        // Fragment became visible
        noteAppSharedViewModel.loadAllNotes()
    }



    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {

        super.onDestroyView()
        _notePrevViewBinding = null // prevent memory leaks by clearing reference
    }


    private fun initializeAdapters() {

        unpinnedNotePrevAdapter = NotePreviewAdapter (

            setNoteIDToOpen =
            { noteID ->
                // this is the functionality assigned whenever this runs in the adapter
                openNoteViewActivity(noteID)
            },
            whenCurrentNotePinClicked =
            { isPinned, currentNoteID ->

                noteAppSharedViewModel.setCurrentPreviewNoteID(currentNoteID)
                noteAppSharedViewModel.updatePreviewPinStatus(isPinned)
            }
        )

        pinnedNotePrevAdapter = NotePreviewAdapter (

            setNoteIDToOpen =
            { noteID ->
                openNoteViewActivity(noteID)
            },
            whenCurrentNotePinClicked =
            { isPinned, currentNoteID ->

                noteAppSharedViewModel.setCurrentPreviewNoteID(currentNoteID)
                noteAppSharedViewModel.updatePreviewPinStatus(isPinned)
            }
        )


        tagDisplayAdapter = TagDisplayAdapter (

            onTagClickedFunction =
                { position, tagID ->

                    tagAppSharedViewModel.setCurrentNotePreviewTagPos(position)

                    val tag = tagAppSharedViewModel.getTag(tagID)

                    if (tag.tagName != "All Notes") {
                        notePrevViewModel.setCurrentSelectedTag(tag)
                    } else {
                        notePrevViewModel.setCurrentSelectedTag(null)
                    }
                }
        )

    }


    private fun setupAdapterDisplays() {

        // Define layout and adapter to use for notes display
        notePrevViewBinding.notesPreviewRV.layoutManager = StaggeredGridLayoutManager(2,
            StaggeredGridLayoutManager.VERTICAL)

        notePrevViewBinding.notesPreviewRV.adapter = unpinnedNotePrevAdapter


        // Define layout and adapter to use for pinned notes display
        notePrevViewBinding.pinnedNotesPreviewRV.layoutManager = StaggeredGridLayoutManager(2,
            StaggeredGridLayoutManager.VERTICAL)

        notePrevViewBinding.pinnedNotesPreviewRV.adapter =  pinnedNotePrevAdapter


        // Define layout and adapter to use for tag display
        notePrevViewBinding.tagsHolderRV.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false)

        notePrevViewBinding.tagsHolderRV.adapter = tagDisplayAdapter
    }



    private fun setupListeners() {

        notePrevViewBinding.apply {

            createButtonIV.setOnClickListener {

                val intent = Intent(requireContext(), NoteViewActivity::class.java)

                GeneralButtonIVHelper.playSelectionIndicationRes(requireContext(), createButtonIV,
                    R.drawable.gold_fab, R.drawable.gold_fab_selected)

                Handler(Looper.getMainLooper()).postDelayed({

                    startActivity(intent)
                }, 600) // Delay in milliseconds (500ms = 0.5 seconds)
            }

            tagEditButtonIV.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(requireContext(),
                    tagEditButtonIV)

                val intent = Intent(requireContext(), TagViewActivity::class.java)
                startActivity(intent)
            }


            // Add scroll listener to ensure edit tag button scrolls with the tags
            tagsHolderRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                    super.onScrolled(recyclerView, dx, dy)

                    // Move the Add button by same scroll distance
                    notePrevViewBinding.tagEditButtonIV.translationX =
                        -recyclerView.computeHorizontalScrollOffset().toFloat()
                }
            })
        }
    }

    private fun openNoteViewActivity(noteID: Int) {

        // Open Note View of note by clicking on one
        val intent = Intent(requireContext(), NoteViewActivity::class.java).apply {
            // Also pass in the id of the note interacted w/ for later retrieval in note view
            putExtra("note_id", noteID)
        }

        requireContext().startActivity(intent)
    }


    private fun observeNotePrevVMValues() {

        notePrevViewModel.apply {

            currentSelectedTag.observe(viewLifecycleOwner) {

                noteAppSharedViewModel.setNotifyRefresh(true)
                noteAppSharedViewModel.setNotifyRefresh(false)

                val notes = noteAppSharedViewModel.notes.value!!

                updateDisplayedNotes(notes)
            }

            noNotes.observe(viewLifecycleOwner) { isTrue ->

                GeneralUIHelper.changeViewVisibility(notePrevViewBinding.unpinnedSectionLayoutRL,
                    !isTrue)
                GeneralUIHelper.changeViewVisibility(notePrevViewBinding.noNotesMessageTV,
                    isTrue)
            }

            noPinnedNotes.observe(viewLifecycleOwner) { isTrue ->

                GeneralUIHelper.changeViewVisibility(notePrevViewBinding.pinnedSectionLayoutRL,
                    !isTrue)
                GeneralUIHelper.changeViewVisibility(notePrevViewBinding.unpinnedLabelTV,
                    !isTrue)
            }

            noUnpinnedNotes.observe(viewLifecycleOwner) { isTrue ->

                GeneralUIHelper.changeViewVisibility(notePrevViewBinding.unpinnedSectionLayoutRL,
                    !isTrue)
                GeneralUIHelper.changeViewVisibility(notePrevViewBinding.unpinnedLabelTV,
                    !isTrue)
            }
        }

    }


    private fun observeNoteAppVMValues() {

        noteAppSharedViewModel.apply {

            notes.observe(viewLifecycleOwner) { notes ->

                updateDisplayedNotes(notes)
            }

            notifyRefresh.observe(viewLifecycleOwner) { shouldRefresh ->

                if (shouldRefresh == true) {
                    loadAllNotes()
                }
            }

            previewNotePinned.observe(viewLifecycleOwner) { isPinned ->

                //update note in database with new pinned status
                if (currentPreviewNoteID.value != -1) {

                    val noteData = getNote(currentPreviewNoteID.value!!)

                    if (noteData == null) return@observe

                    noteData.notePinned = isPinned

//                    Log.d("NoteFrag", "$noteData")

                    setIsNewNote(false)
                    saveNote(noteData)

                    setCurrentPreviewNoteID(-1)
                }

                givePinnedStatusToast(isPinned)

                setNotifyRefresh(true)
                setNotifyRefresh(false)
            }

            emptyNoteDiscarded.observe(viewLifecycleOwner) { wasDiscarded ->

                if (wasDiscarded) {
                    GeneralUIHelper.displayFeedbackToast(requireContext(),
                        "Empty note(s) discarded", false)
                }
            }

        }
    }


    private fun observeTagAppVMValues() {

        tagAppSharedViewModel.apply {

            tags.observe(viewLifecycleOwner) { tags ->
                tagDisplayAdapter.refreshData(tags)
            }

            notifyRefresh.observe(viewLifecycleOwner) { shouldRefresh ->

                if (shouldRefresh == true) {
                    loadAllTags()
                }
            }

            selectedNotePreviewTagPos.observe(viewLifecycleOwner) { position ->
                tagDisplayAdapter.setSelectedPosition(position)
            }
        }
    }

    private fun updateNoNotesMessage(hasSelectedTag: Boolean) {

        val message =
            if (hasSelectedTag) {
                "No notes with this tag selected yet."
            } else {
                "No notes. Tap + below to create a new note."
            }

        notePrevViewBinding.noNotesMessageTV.text = message
    }


    private fun updateDisplayedNotes(notes: List<Note>) {

        val selectedTag = notePrevViewModel.currentSelectedTag.value

        val notesToDisplay =
            if (selectedTag != null) {
                taggedAppSharedViewModel.getNotesByTagID(selectedTag.tagID)
            } else {
                notes
            }

        val unfilteredPinned = notesToDisplay.filter { !it.notePinned }
        val filteredPinned = notesToDisplay.filter { it.notePinned }

        unpinnedNotePrevAdapter.refreshData(unfilteredPinned)
        pinnedNotePrevAdapter.refreshData(filteredPinned)

        updateNoNotesMessage(selectedTag != null)
        notePrevViewModel.updateNoNotesFlag(notesToDisplay)

        notePrevViewModel.updateNoPinnedNotesFlag(filteredPinned)
        notePrevViewModel.updateNoUnpinnedNotesFlag(unfilteredPinned)
    }


    private fun givePinnedStatusToast(isPinned: Boolean) {

        val status = if (isPinned) "Note Pinned" else "Note Unpinned"

        GeneralUIHelper.displayFeedbackToast(requireContext(), status, false)
    }


}
