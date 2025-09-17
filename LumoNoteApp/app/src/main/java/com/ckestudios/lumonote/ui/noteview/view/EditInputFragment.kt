package com.ckestudios.lumonote.ui.noteview.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.databinding.FragmentEditInputBinding
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditInputViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.ckestudios.lumonote.utils.helpers.GeneralButtonIVHelper


class EditInputFragment : Fragment() {

    // Real binding variable that can be null when the view is destroyed
    // Naming it with an underscore (_editInputViewBinding) is just a convention
    // → It signals: "don’t use me directly, I’m just the backing field"
    private var _editInputViewBinding: FragmentEditInputBinding? = null

    // Safe-to-use version of binding
    // Uses Kotlin’s getter so we don’t need to write _editInputViewBinding!! everywhere
    // The "!!" means it assumes _editInputViewBinding is not null between onCreateView & onDestroyView
    private val editInputViewBinding get() = _editInputViewBinding!!

    private val generalButtonIVHelper: GeneralButtonIVHelper = GeneralButtonIVHelper()

    private lateinit var editInputViewModel: EditInputViewModel
    private lateinit var inputSharedViewModel: InputSharedViewModel
    private lateinit var editContentSharedViewModel: EditContentSharedViewModel


    // Called when the Fragment is created (before the UI exists)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        editInputViewModel = ViewModelProvider(this).get(EditInputViewModel::class.java)

        inputSharedViewModel = ViewModelProvider(requireActivity()).get(InputSharedViewModel::class.java)

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

        setOnClickListeners()

        observeInputSharedVMValues()

        observeEditInputVMValues()

        observeEditContentVMValues()
    }



    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {

        super.onDestroyView()
        _editInputViewBinding = null // prevent memory leaks by clearing reference
    }


    private fun setOnClickListeners() {

        editInputViewBinding.apply {

            colorButtonIV.setOnClickListener {

                val colorActiveStatus =
                    editInputViewModel.colorBtnIsActive.value as Boolean

                editInputViewModel.setColorBtnActive(!colorActiveStatus)
            }


            checkListButtonIV.setOnClickListener {

                val checklistActiveStatus =
                    editInputViewModel.checklistBtnIsActive.value as Boolean

                editInputViewModel.setChecklistBtnActive(!checklistActiveStatus)
            }


            imageButtonIV.setOnClickListener {

                generalButtonIVHelper.highlightButtonIV(imageButtonIV, requireContext())

                Handler(Looper.getMainLooper()).postDelayed({
                    // Action here
                    generalButtonIVHelper.unhighlightButtonIV(imageButtonIV, requireContext())
                }, 1000) // 1000 ms = 1 seconds
            }

            textFormatButtonIV.setOnClickListener {

                val textFormatActiveStatus =
                    editInputViewModel.textFormatBtnIsActive.value as Boolean

                editInputViewModel.setTextFormatBtnActive(!textFormatActiveStatus)
                inputSharedViewModel.setShouldOpenFormatter(!textFormatActiveStatus)
            }
        }
    }


    private fun observeInputSharedVMValues() {

        inputSharedViewModel.apply {

            noteContentIsEditing.observe(viewLifecycleOwner){

                // disable buttons when not editing note content

                if (it == true) {

                    generalButtonIVHelper.enableButtonIV(editInputViewBinding.colorButtonIV,
                        requireContext())
                    generalButtonIVHelper.enableButtonIV(editInputViewBinding.checkListButtonIV,
                        requireContext())
                    generalButtonIVHelper.enableButtonIV(editInputViewBinding.imageButtonIV,
                        requireContext())
                    generalButtonIVHelper.enableButtonIV(editInputViewBinding.textFormatButtonIV,
                        requireContext())

                    // since automatically opens textformatter when editing
                    editInputViewModel.setTextFormatBtnActive(true)
                }
                else {

                    generalButtonIVHelper.disableButtonIV(editInputViewBinding.colorButtonIV,
                        requireContext())
                    generalButtonIVHelper.disableButtonIV(editInputViewBinding.checkListButtonIV,
                        requireContext())
                    generalButtonIVHelper.disableButtonIV(editInputViewBinding.imageButtonIV,
                        requireContext())
                    generalButtonIVHelper.disableButtonIV(editInputViewBinding.textFormatButtonIV,
                        requireContext())
                }

            }
        }
    }



    private fun observeEditInputVMValues() {

        editInputViewModel.apply {

            colorBtnIsActive.observe(viewLifecycleOwner) {

                if (editInputViewBinding.colorButtonIV.isEnabled) {

                    generalButtonIVHelper.updateButtonIVHighlight(editInputViewBinding.colorButtonIV,
                        it, requireContext())
                }
            }

            checklistBtnIsActive.observe(viewLifecycleOwner) {

                if (editInputViewBinding.checkListButtonIV.isEnabled) {

                    generalButtonIVHelper.updateButtonIVHighlight(editInputViewBinding.checkListButtonIV,
                        it, requireContext())
                }
            }

            textFormatBtnIsActive.observe(viewLifecycleOwner) {

                if (editInputViewBinding.textFormatButtonIV.isEnabled) {

                    generalButtonIVHelper.updateButtonIVHighlight(editInputViewBinding.textFormatButtonIV,
                        it, requireContext())
                }
            }

        }
    }


    private fun observeEditContentVMValues() {

        editContentSharedViewModel.apply {


        }

    }


}