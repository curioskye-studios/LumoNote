package com.ckestudios.lumonote.ui.noteview.view.taginput

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ckestudios.lumonote.data.repository.NoteRepository
import com.ckestudios.lumonote.data.repository.TagRepository
import com.ckestudios.lumonote.data.repository.TaggedRepository
import com.ckestudios.lumonote.databinding.FragmentTagInputBinding
import com.ckestudios.lumonote.ui.noteview.other.TaggedSaveHelper
import com.ckestudios.lumonote.ui.sharedviewmodel.AppSharedViewFactory
import com.ckestudios.lumonote.ui.sharedviewmodel.NoteAppSharedViewModel
import com.ckestudios.lumonote.ui.sharedviewmodel.TagAppSharedViewModel
import com.ckestudios.lumonote.ui.sharedviewmodel.TaggedAppSharedViewModel
import com.ckestudios.lumonote.ui.tagview.view.TagViewActivity
import com.ckestudios.lumonote.utils.basichelpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager


class TagInputFragment : Fragment() {

    private var _tagInputViewBinding: FragmentTagInputBinding? = null
    private val tagInputViewBinding get() = _tagInputViewBinding!!

    private lateinit var tagAppSharedViewModel: TagAppSharedViewModel
    private lateinit var taggedAppSharedViewModel: TaggedAppSharedViewModel
    private lateinit var noteAppSharedViewModel: NoteAppSharedViewModel

    private lateinit var tagInputDisplayAdapter: TagInputDisplayAdapter
    private lateinit var tagInputSelectorAdapter: TagInputSelectorAdapter

    private var noteID = -1
    private var tagIDList = mutableListOf<Int>()
    private lateinit var taggedSaveHelper: TaggedSaveHelper


    // Called when the Fragment is created (before the UI exists)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val tagRepository = TagRepository(requireContext()) // DB
        val taggedRepository = TaggedRepository(requireContext()) // DB
        val noteRepository = NoteRepository(requireContext()) // DB

        tagAppSharedViewModel = ViewModelProvider(requireActivity(), AppSharedViewFactory(tagRepository))
            .get(TagAppSharedViewModel::class.java)
        taggedAppSharedViewModel = ViewModelProvider(requireActivity(), AppSharedViewFactory(taggedRepository))
            .get(TaggedAppSharedViewModel::class.java)
        noteAppSharedViewModel = ViewModelProvider(requireActivity(), AppSharedViewFactory(noteRepository))
            .get(NoteAppSharedViewModel::class.java)

        taggedSaveHelper = TaggedSaveHelper(taggedAppSharedViewModel, tagAppSharedViewModel)
    }

    // Called when the Fragment creates its view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for requireContext() fragment
        _tagInputViewBinding = FragmentTagInputBinding.inflate(inflater, container, false)

        return tagInputViewBinding.root // return the root view for the fragment
    }

    // Called when the view is created (safe place to interact with UI)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)


        noteID = noteAppSharedViewModel.currentOpenNoteID.value!!

        GeneralUIHelper.changeViewVisibility(tagInputViewBinding.tagSelectorSectionRL, false)

        setupAdaptersDisplay()

        loadNoteTags()

        setOnClickListeners()

        observeTagAppVMValues()
    }


    override fun onResume() {
        super.onResume()

        tagAppSharedViewModel.loadAllTags()
        loadNoteTags()
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _tagInputViewBinding = null
    }


    private fun setupAdaptersDisplay() {

        tagInputDisplayAdapter = TagInputDisplayAdapter ()

        tagInputSelectorAdapter = TagInputSelectorAdapter (

            onTagClickedFunction = {
                tagIDList ->

                this.tagIDList = tagIDList

                val newTagList = tagIDList.map { tagAppSharedViewModel.getTag(it) }

                tagAppSharedViewModel.setCurrentNoteTagsSelected(newTagList)

                if (noteID != -1) {

                    taggedSaveHelper.commitNoteTags(newTagList, noteID)

                    GeneralUIHelper.displayFeedbackToast(requireContext(), "Selection Saved",
                        false)
                }

            }
        )


        tagInputViewBinding.apply {

            tagDisplayHolderRV.layoutManager = FlexboxLayoutManager(context).apply {
                    flexDirection = FlexDirection.ROW
                    flexWrap = FlexWrap.WRAP
                }

            tagInputViewBinding.tagDisplayHolderRV.adapter = tagInputDisplayAdapter


            tagSelectorHolderRV.layoutManager = LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false)

            tagSelectorHolderRV.adapter = tagInputSelectorAdapter
        }
    }

    private fun loadNoteTags() {

        if (noteID != -1) {

            val loadedTags = taggedAppSharedViewModel.getTagsByNoteID(noteID)
            val loadedTagIDs =
                loadedTags.map {
                    val tag = tagAppSharedViewModel.getTag(it.tagID)
                    tag.tagID
                }

            tagInputSelectorAdapter.setSelectedTagsList(loadedTagIDs)
            tagAppSharedViewModel.setCurrentNoteTagsSelected(loadedTags)

            Log.d("TagDebug", "loadedTags: ${loadedTags}.")
            Log.d("TagDebug", "loadedTagIDs: ${loadedTagIDs}.")
        }
    }


    private fun setOnClickListeners() {

        tagInputViewBinding.apply {

            addTagButtonIV.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(requireContext(), addTagButtonIV)

                Handler(Looper.getMainLooper()).postDelayed({

                    if (tagSelectorSectionRL.visibility != View.VISIBLE) {
                        GeneralUIHelper.changeViewVisibility(tagSelectorSectionRL, true)
                    } else {
                        GeneralUIHelper.changeViewVisibility(tagSelectorSectionRL, false)
                    }
                }, 300) // Delay in milliseconds (300ms = 0.3 seconds)

            }

            editTagButtonIV.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(requireContext(), editTagButtonIV)

                Handler(Looper.getMainLooper()).postDelayed({

                    // open tagviewactivity
                    val intent = Intent (activity, TagViewActivity::class.java)
                    activity?.startActivity(intent)

                }, 300) // Delay in milliseconds (300ms = 0.3 seconds)

            }

//            saveTagButtonIV.setOnClickListener {
//
//                GeneralButtonIVHelper.playSelectionIndication(requireContext(), saveTagButtonIV)
//
//                val newTagList = tagIDList.map { tagAppSharedViewModel.getTag(it) }
//                val oldTags = taggedAppSharedViewModel.getTagsByNoteID(noteID)
//
//                if (noteID != -1) taggedSaveHelper.commitNoteTags(newTagList, noteID)
//
//                if (newTagList != oldTags) {
//                    GeneralUIHelper.displayFeedbackToast(requireContext(), "Selection Saved",
//                        false)
//                } else {
//                    GeneralUIHelper.displayFeedbackToast(requireContext(), "Up to date",
//                        false)
//                }
//
//                closeSelector()
//            }

            closeSelectorButtonIV.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(requireContext(),
                    closeSelectorButtonIV)

                closeSelector()
            }

        }
    }


    private fun observeTagAppVMValues() {

        tagAppSharedViewModel.apply {

            tags.observe(viewLifecycleOwner) { tags ->

                val tagsExcludingAllNotes = tags.filter { it.tagName != "All Notes" }
                tagInputSelectorAdapter.refreshData(tagsExcludingAllNotes)
            }

            notifyRefresh.observe(viewLifecycleOwner) { shouldRefresh ->

                if (shouldRefresh == true) {
                    loadAllTags()
                }
            }

            currentNoteTagsSelected.observe(viewLifecycleOwner) { tags ->

                tagInputDisplayAdapter.refreshData(tags)
            }

            noTagsAttached.observe(viewLifecycleOwner) { hasTags ->

                GeneralUIHelper.changeViewVisibility(tagInputViewBinding.noTagsMessageTV,
                    hasTags)
            }

            noTagsCreated.observe(viewLifecycleOwner) { isTrue ->

                GeneralUIHelper.changeViewVisibility(tagInputViewBinding.tagSelectorHolderRV,
                    !isTrue)
                GeneralUIHelper.changeViewVisibility(tagInputViewBinding.noTagsAvailableMsgTV,
                    isTrue)
            }
        }
    }


    private fun closeSelector() {

        Handler(Looper.getMainLooper()).postDelayed({

            if (tagInputViewBinding.tagSelectorSectionRL.visibility == View.VISIBLE) {
                GeneralUIHelper.changeViewVisibility(tagInputViewBinding.tagSelectorSectionRL,
                    false)
            }
        }, 300) // Delay in milliseconds (300ms = 0.3 seconds)
    }


}