package com.ckestudios.lumonote.ui.noteview.view.taginput

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ckestudios.lumonote.data.models.Tag
import com.ckestudios.lumonote.data.repository.TagRepository
import com.ckestudios.lumonote.databinding.FragmentTagInputBinding
import com.ckestudios.lumonote.ui.sharedviewmodel.AppSharedViewFactory
import com.ckestudios.lumonote.ui.sharedviewmodel.TagAppSharedViewModel
import com.ckestudios.lumonote.utils.basichelpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager


class TagInputFragment : Fragment() {

    private var _tagInputViewBinding: FragmentTagInputBinding? = null
    private val tagInputViewBinding get() = _tagInputViewBinding!!

    private lateinit var tagAppSharedViewModel: TagAppSharedViewModel

    private lateinit var tagInputDisplayAdapter: TagInputDisplayAdapter
    private lateinit var tagInputSelectorAdapter: TagInputSelectorAdapter



    // Called when the Fragment is created (before the UI exists)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val tagRepository = TagRepository(requireContext()) // DB

        tagAppSharedViewModel = ViewModelProvider(requireActivity(), AppSharedViewFactory(tagRepository))
            .get(TagAppSharedViewModel::class.java)

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


        GeneralUIHelper.changeViewVisibility(tagInputViewBinding.tagSelectorSectionRL, false)

        setupAdaptersDisplay()

        setOnClickListeners()

        observeTagAppVMValues()
    }


    override fun onResume() {
        super.onResume()

        tagAppSharedViewModel.loadAllTags()
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _tagInputViewBinding = null
    }

    private fun setupAdaptersDisplay() {

        tagInputDisplayAdapter = TagInputDisplayAdapter (

            onTagClickedFunction = {
                position ->

            }
        )

        tagInputSelectorAdapter = TagInputSelectorAdapter (

            onTagClickedFunction = {
                tagIDList ->

                val tagList = mutableListOf<Tag>()
                tagIDList.forEach { tagList.add(tagAppSharedViewModel.getTag(it)) }

                tagAppSharedViewModel.setCurrentNoteTagsSelected(tagList)
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

            closeSelectorButtonIV.setOnClickListener {

                GeneralButtonIVHelper.playSelectionIndication(requireContext(),
                    closeSelectorButtonIV)

                Handler(Looper.getMainLooper()).postDelayed({

                    GeneralUIHelper.changeViewVisibility(tagSelectorSectionRL, false)
                }, 300) // Delay in milliseconds (300ms = 0.3 seconds)
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
        }
    }





}