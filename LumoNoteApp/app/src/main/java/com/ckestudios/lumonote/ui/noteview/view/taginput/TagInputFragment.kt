package com.ckestudios.lumonote.ui.noteview.view.taginput

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ckestudios.lumonote.data.repository.TagRepository
import com.ckestudios.lumonote.databinding.FragmentTagInputBinding
import com.ckestudios.lumonote.ui.sharedviewmodel.AppSharedViewFactory
import com.ckestudios.lumonote.ui.sharedviewmodel.TagAppSharedViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager


class TagInputFragment : Fragment() {

    private var _tagInputViewBinding: FragmentTagInputBinding? = null
    private val tagInputViewBinding get() = _tagInputViewBinding!!

    private lateinit var tagInputDisplayAdapter: TagInputDisplayAdapter

    private lateinit var tagAppSharedViewModel: TagAppSharedViewModel



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


        setupAdapterDisplay()

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

    private fun setupAdapterDisplay() {

        tagInputDisplayAdapter = TagInputDisplayAdapter (

            onTagClickedFunction = {
                position ->


            }
        )

        tagInputViewBinding.tagDisplayHolderRV.layoutManager =
            FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
            }
        tagInputViewBinding.tagDisplayHolderRV.adapter = tagInputDisplayAdapter

        tagInputViewBinding.tagSelectorHolderRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        tagInputViewBinding.tagSelectorHolderRV.adapter = tagInputDisplayAdapter
    }


    private fun setOnClickListeners() {

        tagInputViewBinding.apply {


        }
    }


    private fun observeTagAppVMValues() {

        tagAppSharedViewModel.apply {

            tags.observe(viewLifecycleOwner) { tags ->

                val tagsExcludingAllNotes = tags.filter { it.tagName != "All Notes" }

                tagInputDisplayAdapter.refreshData(tagsExcludingAllNotes)
            }

            notifyRefresh.observe(viewLifecycleOwner) { shouldRefresh ->

                if (shouldRefresh == true) {
                    loadAllTags()
                }
            }
        }
    }





}