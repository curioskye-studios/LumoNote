package com.curioskyestudios.lumonote.ui.noteview.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.curioskyestudios.lumonote.databinding.FragmentEditInputBinding
import com.curioskyestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.curioskyestudios.lumonote.ui.noteview.viewmodel.EditInputViewModel
import com.curioskyestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.curioskyestudios.lumonote.utils.general.GeneralUIHelper


class EditInputFragment : Fragment() {

    // Real binding variable that can be null when the view is destroyed
    // Naming it with an underscore (_editInputViewBinding) is just a convention
    // → It signals: "don’t use me directly, I’m just the backing field"
    private var _editInputViewBinding: FragmentEditInputBinding? = null

    // Safe-to-use version of binding
    // Uses Kotlin’s getter so we don’t need to write _editInputViewBinding!! everywhere
    // The "!!" means it assumes _editInputViewBinding is not null between onCreateView & onDestroyView
    private val editInputViewBinding get() = _editInputViewBinding!!

    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()

    private lateinit var editInputViewModel: EditInputViewModel
    private lateinit var inputSharedViewModel: InputSharedViewModel
    private lateinit var editContentSharedViewModel: EditContentSharedViewModel


    // Called when the Fragment is created (before the UI exists)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        editInputViewModel = ViewModelProvider(this).get(EditInputViewModel::class.java)

        inputSharedViewModel = ViewModelProvider(this).get(InputSharedViewModel::class.java)

        editContentSharedViewModel =
            ViewModelProvider(requireActivity()).get(EditContentSharedViewModel::class.java)
    }

    // Called when the Fragment creates its view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for requireContext() fragment
        _editInputViewBinding = FragmentEditInputBinding.inflate(inflater, container, false)

        return editInputViewBinding.root // return the root view for the fragment
    }

    // Called when the view is created (safe place to interact with UI)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        observeEditInputVMValues()

        observeEditContentVMValues()

        setOnClickListeners()
    }



    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {

        super.onDestroyView()
        _editInputViewBinding = null // prevent memory leaks by clearing reference
    }



    private fun observeEditInputVMValues() {

        editInputViewModel.apply {


            textFormatBtnActive.observe(viewLifecycleOwner) {

                val isActive = it
                generalUIHelper.updateButtonIVHighlight(editInputViewBinding.textFormatButtonIV,
                    isActive, requireContext())
            }

            colorBtnActive.observe(viewLifecycleOwner) {

                val isActive = it
                generalUIHelper.updateButtonIVHighlight(editInputViewBinding.colorButtonIV,
                    isActive, requireContext())
            }

            checklistBtnActive.observe(viewLifecycleOwner) {

                val isActive = it
                generalUIHelper.updateButtonIVHighlight(editInputViewBinding.checkListButtonIV,
                    isActive, requireContext())
            }
        }
    }


    private fun observeEditContentVMValues() {

        editContentSharedViewModel.apply {

            // watch to know when editing note content
            isEditing.observe(viewLifecycleOwner){

                val isEditingStatus = it
                val textFormatIsActive =
                    editInputViewModel.textFormatBtnActive.value as Boolean

                // ensure status is consistent
                if (isEditingStatus != textFormatIsActive) {

                    editInputViewModel.setTextFormatBtnActive(!textFormatIsActive)
                }

                generalUIHelper.updateButtonIVHighlight(editInputViewBinding.textFormatButtonIV,
                    isEditingStatus, requireContext())
            }

        }

    }


    private fun setOnClickListeners() {

        editInputViewBinding.apply {

            colorButtonIV.setOnClickListener {

                val colorActiveStatus =
                    editInputViewModel.colorBtnActive.value as Boolean

                editInputViewModel.setColorBtnActive(!colorActiveStatus)

                generalUIHelper.updateButtonIVHighlight(colorButtonIV, !colorActiveStatus,
                    requireContext())
            }


            checkListButtonIV.setOnClickListener {

                val checklistActiveStatus =
                    editInputViewModel.checklistBtnActive.value as Boolean

                editInputViewModel.setChecklistBtnActive(!checklistActiveStatus)

                generalUIHelper.updateButtonIVHighlight(checkListButtonIV, !checklistActiveStatus,
                    requireContext())
            }


            imageButtonIV.setOnClickListener {

                generalUIHelper.highlightButtonIV(imageButtonIV, requireContext())

                Handler(Looper.getMainLooper()).postDelayed({
                    // Action here
                    generalUIHelper.unhighlightButtonIV(imageButtonIV, requireContext())
                }, 1000) // 1000 ms = 1 seconds
            }

            textFormatButtonIV.setOnClickListener {

                val textFormatActiveStatus =
                    editInputViewModel.textFormatBtnActive.value as Boolean

                editInputViewModel.setTextFormatBtnActive(!textFormatActiveStatus)

                inputSharedViewModel.setOpenFormatter(!textFormatActiveStatus)

                generalUIHelper.updateButtonIVHighlight(textFormatButtonIV,
                    !textFormatActiveStatus, requireContext())
            }
        }
    }

}