package com.ckestudios.lumonote.ui.noteview.view

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.databinding.FragmentEditInputBinding
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditInputViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.ckestudios.lumonote.utils.helpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.helpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.textformatting.SimpleChecklistFormatter
import com.ckestudios.lumonote.utils.textformatting.SimpleImageFormatter

//import com.ckestudios.lumonote.utils.textformatting.SimpleImageFormatter


class EditInputFragment : Fragment() {

    // Real binding variable that can be null when the view is destroyed
    // Naming it with an underscore (_editInputViewBinding) is just a convention
    // → It signals: "don’t use me directly, I’m just the backing field"
    private var _editInputViewBinding: FragmentEditInputBinding? = null

    // Safe-to-use version of binding
    // Uses Kotlin’s getter so we don’t need to write _editInputViewBinding!! everywhere
    // The "!!" means it assumes _editInputViewBinding is not null between onCreateView & onDestroyView
    private val editInputViewBinding get() = _editInputViewBinding!!

    private lateinit var editInputViewModel: EditInputViewModel
    private lateinit var inputSharedViewModel: InputSharedViewModel
    private lateinit var editContentSharedViewModel: EditContentSharedViewModel

    private val generalButtonIVHelper: GeneralButtonIVHelper = GeneralButtonIVHelper()
    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()

    private lateinit var noteContentET: CustomSelectionET
    private lateinit var simpleChecklistFormatter: SimpleChecklistFormatter
    private lateinit var simpleImageFormatter: SimpleImageFormatter
//    private lateinit var imageLineGuard: ImageLineGuard

    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    private var pickImage: Uri? = null


    // Called when the Fragment is created (before the UI exists)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        editInputViewModel = ViewModelProvider(this).get(EditInputViewModel::class.java)

        inputSharedViewModel = ViewModelProvider(requireActivity()).get(InputSharedViewModel::class.java)

        editContentSharedViewModel =
            ViewModelProvider(requireActivity()).get(EditContentSharedViewModel::class.java)

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let {

                    pickImage = it

                    // Insert image immediately once selected
                    simpleImageFormatter.processFormatting(it, requireContext())
                }
            }
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

        noteContentET =
            editContentSharedViewModel.noteContentEditTextView.value as CustomSelectionET


        simpleChecklistFormatter = SimpleChecklistFormatter(noteContentET)
        simpleImageFormatter = SimpleImageFormatter(noteContentET)
//        imageLineGuard = ImageLineGuard(noteContentET)

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

            checkListButtonIV.apply {

                setOnClickListener {

                    simpleChecklistFormatter.processFormatting()

                    updateChecklistActive()
                }
            }


            imageButtonIV.apply {

                setOnClickListener {

                    pickImageLauncher.launch("image/*")

                    generalButtonIVHelper.highlightButtonIV(imageButtonIV, requireContext())
                }
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

            noteContentIsEditing.observe(viewLifecycleOwner){ isTrue ->

                if (isTrue) {

                    val hasText = currentLineHasText.value!!
                    if (!hasText) {
                        generalButtonIVHelper.enableButtonIV(editInputViewBinding.imageButtonIV,
                            requireContext())
                    }

                    generalButtonIVHelper.enableButtonIV(editInputViewBinding.checkListButtonIV,
                        requireContext())
                    generalButtonIVHelper.enableButtonIV(editInputViewBinding.textFormatButtonIV,
                        requireContext())

                    // since automatically opens textformatter when editing
                    editInputViewModel.setTextFormatBtnActive(true)

                    updateChecklistActive()
                }

                else {

                    generalButtonIVHelper.disableButtonIV(editInputViewBinding.imageButtonIV,
                        requireContext())
                    generalButtonIVHelper.disableButtonIV(editInputViewBinding.checkListButtonIV,
                        requireContext())
                    generalButtonIVHelper.disableButtonIV(editInputViewBinding.textFormatButtonIV,
                        requireContext())
                }
            }

            isContentSelectionEmpty.observe(viewLifecycleOwner){

                updateChecklistActive()
            }

            currentLineHasText.observe(viewLifecycleOwner) { hasText ->

                when {

                    !hasText && noteContentIsEditing.value!! ->
                        generalButtonIVHelper.enableButtonIV(editInputViewBinding.imageButtonIV,
                            requireContext())

                    else ->
                        generalButtonIVHelper.disableButtonIV(editInputViewBinding.imageButtonIV,
                            requireContext())
                }
            }

            currentLineHasImage.observe(viewLifecycleOwner) { hasImage ->

                when {

                    !hasImage && noteContentIsEditing.value!! -> {

                        generalButtonIVHelper.enableButtonIV(editInputViewBinding.checkListButtonIV,
                            requireContext())
                    }


                    else ->
                        generalButtonIVHelper.disableButtonIV(editInputViewBinding.checkListButtonIV,
                            requireContext())
                }

                updateChecklistActive()
            }

        }
    }



    private fun observeEditInputVMValues() {

        editInputViewModel.apply {

            imageBtnIsActive.observe(viewLifecycleOwner) { isTrue ->

                if (editInputViewBinding.imageButtonIV.isEnabled) {

                    generalButtonIVHelper.updateButtonIVHighlight(
                        editInputViewBinding.imageButtonIV, isTrue, requireContext())
                }
            }

            checklistBtnIsActive.observe(viewLifecycleOwner) { isTrue ->

                if (editInputViewBinding.checkListButtonIV.isEnabled) {

                    generalButtonIVHelper.updateButtonIVHighlight(
                        editInputViewBinding.checkListButtonIV, isTrue, requireContext())
                }
            }

            textFormatBtnIsActive.observe(viewLifecycleOwner) { isTrue ->

                if (editInputViewBinding.textFormatButtonIV.isEnabled) {

                    generalButtonIVHelper.updateButtonIVHighlight(
                        editInputViewBinding.textFormatButtonIV, isTrue, requireContext())
                }
            }

        }
    }


    private fun observeEditContentVMValues() {

        editContentSharedViewModel.apply {

            removeChecklist.observe(viewLifecycleOwner) { shouldRemove ->

                simpleChecklistFormatter.updateRemoveChecklist(shouldRemove)
            }
        }

    }


    private fun updateChecklistActive() {

        val hasChecklist =
            simpleChecklistFormatter.checkCurrentLineHasChecklist(noteContentET.selectionStart)

        if (hasChecklist) {

            val checkedState =
                simpleChecklistFormatter.getCheckedState(noteContentET.selectionStart)

            when (checkedState) {
                ("☐") -> {
                    generalButtonIVHelper.changeButtonIVImage(
                        editInputViewBinding.checkListButtonIV,
                        R.drawable.check_box_outline_blank_24px)
                }
                ("☑") -> {
                    generalButtonIVHelper.changeButtonIVImage(
                        editInputViewBinding.checkListButtonIV,
                        R.drawable.check_box_24px)
                }
                null -> {}
            }
        }
        else {

            generalButtonIVHelper.changeButtonIVImage(editInputViewBinding.checkListButtonIV,
                R.drawable.check_box_24px)
        }

        editInputViewModel.setChecklistBtnActive(hasChecklist)
    }




}