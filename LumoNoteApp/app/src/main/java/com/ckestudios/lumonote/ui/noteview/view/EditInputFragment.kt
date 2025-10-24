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
import com.ckestudios.lumonote.ui.noteview.other.CustomImageSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditInputViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.ckestudios.lumonote.utils.basichelpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.state.ActionInterpreter
import com.ckestudios.lumonote.utils.state.StateManager
import com.ckestudios.lumonote.utils.state.TextStateWatcher
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
    private lateinit var noteContentStateManager: StateManager
    private lateinit var noteContentTextWatcher: TextStateWatcher
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
                    simpleImageFormatter.processFormatting(it)
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
        noteContentStateManager =
            editContentSharedViewModel.noteContentStateManager.value as StateManager

        noteContentTextWatcher = TextStateWatcher(noteContentET, noteContentStateManager)
        noteContentET.addTextChangedListener(noteContentTextWatcher)
        editContentSharedViewModel.setNoteContentTextWatcher(noteContentTextWatcher)

        simpleChecklistFormatter = SimpleChecklistFormatter(noteContentET, noteContentStateManager)
        simpleImageFormatter = SimpleImageFormatter(noteContentET, noteContentStateManager)

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

            checkListButtonIV.setOnClickListener {

                simpleChecklistFormatter.processFormatting()

                updateChecklistActive()
            }


            imageButtonIV.setOnClickListener {

                generalButtonIVHelper.playSelectionIndication(requireContext(),
                    imageButtonIV)

                pickImageLauncher.launch("image/*")

                generalButtonIVHelper.highlightButtonIV(imageButtonIV, requireContext(),
                    R.drawable.selected_background)
            }


            textFormatButtonIV.setOnClickListener {

                val textFormatActiveStatus =
                    editInputViewModel.textFormatBtnIsActive.value as Boolean

                editInputViewModel.setTextFormatBtnActive(!textFormatActiveStatus)
                inputSharedViewModel.setShouldOpenFormatter(!textFormatActiveStatus)
            }

            undoButtonIV.setOnClickListener {
                val actionInterpreter = ActionInterpreter(noteContentTextWatcher)

                noteContentStateManager.undoAction(actionInterpreter)

                editInputViewModel.setUndoBtnActive(!noteContentStateManager.checkIfUndoEmpty())
                editInputViewModel.setRedoBtnActive(!noteContentStateManager.checkIfRedoEmpty())
            }

            redoButtonIV.setOnClickListener {

                noteContentStateManager.redoAction()
                editInputViewModel.setUndoBtnActive(!noteContentStateManager.checkIfUndoEmpty())
                editInputViewModel.setRedoBtnActive(!noteContentStateManager.checkIfRedoEmpty())
            }
        }
    }


    private fun observeInputSharedVMValues() {

        inputSharedViewModel.apply {

            noteContentIsEditing.observe(viewLifecycleOwner){ isTrue ->

                if (isTrue) {

                    generalButtonIVHelper.enableButtonIV(editInputViewBinding.textFormatButtonIV,
                        requireContext(), null)

                    // since automatically opens textformatter when editing
                    editInputViewModel.setTextFormatBtnActive(true)
                } else {

                    generalButtonIVHelper.disableButtonIV(editInputViewBinding.textFormatButtonIV,
                        requireContext())
                }

                updateImageBtnDisplay()

                updateChecklistBtnDisplay()
                updateChecklistActive()

                updateUndoBtnDisplay()
                updateRedoBtnDisplay()
            }

            isContentSelectionEmpty.observe(viewLifecycleOwner){

                updateChecklistBtnDisplay()

                updateUndoBtnDisplay()
                updateRedoBtnDisplay()
            }

            currentLineHasText.observe(viewLifecycleOwner) {

                updateImageBtnDisplay()
            }

            currentLineHasImage.observe(viewLifecycleOwner) {

                updateImageBtnDisplay()
                updateChecklistBtnDisplay()
            }

        }
    }



    private fun observeEditInputVMValues() {

        editInputViewModel.apply {

            imageBtnIsActive.observe(viewLifecycleOwner) { isTrue ->

                if (editInputViewBinding.imageButtonIV.isEnabled) {

                    generalButtonIVHelper.updateButtonIVHighlight(
                        editInputViewBinding.imageButtonIV, isTrue, requireContext(), null,
                        R.drawable.selected_background)
                }
            }

            checklistBtnIsActive.observe(viewLifecycleOwner) { isTrue ->

                if (editInputViewBinding.checkListButtonIV.isEnabled) {

                    generalButtonIVHelper.updateButtonIVHighlight(
                        editInputViewBinding.checkListButtonIV, isTrue, requireContext(), null,
                        R.drawable.selected_background)
                }
            }

            textFormatBtnIsActive.observe(viewLifecycleOwner) { isTrue ->

                if (editInputViewBinding.textFormatButtonIV.isEnabled) {

                    generalButtonIVHelper.updateButtonIVHighlight(
                        editInputViewBinding.textFormatButtonIV, isTrue, requireContext(), null,
                        R.drawable.selected_background)
                }
            }

            undoBtnIsActive.observe(viewLifecycleOwner) { isTrue ->

                if (editInputViewBinding.undoButtonIV.isEnabled) {

                    generalButtonIVHelper.updateButtonIVHighlight(
                        editInputViewBinding.undoButtonIV, isTrue, requireContext(),
                            R.color.light_grey_3, R.drawable.selected_background)
                }
            }

            redoBtnIsActive.observe(viewLifecycleOwner) { isTrue ->

                if (editInputViewBinding.redoButtonIV.isEnabled) {

                    generalButtonIVHelper.updateButtonIVHighlight(
                        editInputViewBinding.redoButtonIV, isTrue, requireContext(),
                            R.color.light_grey_3, R.drawable.selected_background)
                }
            }

        }
    }


    private fun observeEditContentVMValues() {

        editContentSharedViewModel.apply {
        }

    }


    private fun updateImageBtnDisplay() {

        val isEditing = inputSharedViewModel.noteContentIsEditing.value!!
        val hasImage = inputSharedViewModel.currentLineHasImage.value!!
        val hasText = inputSharedViewModel.currentLineHasText.value!!

        if (!hasImage && !hasText && isEditing && imageCountLessThanOne()) {

            generalButtonIVHelper.enableButtonIV(editInputViewBinding.imageButtonIV,
                requireContext(), null)
        } else {

            generalButtonIVHelper.disableButtonIV(editInputViewBinding.imageButtonIV,
                requireContext())
        }
    }

    private fun updateChecklistBtnDisplay() {

        val isEditing = inputSharedViewModel.noteContentIsEditing.value!!
        val selectionIsEmpty = inputSharedViewModel.isContentSelectionEmpty.value!!
        val hasImage = inputSharedViewModel.currentLineHasImage.value!!

        if (selectionIsEmpty && !hasImage && isEditing) {

            generalButtonIVHelper.enableButtonIV(editInputViewBinding.checkListButtonIV,
                requireContext(), null)
        } else {

            generalButtonIVHelper.disableButtonIV(editInputViewBinding.checkListButtonIV,
                requireContext())
        }

        updateChecklistActive()
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


    private fun updateUndoBtnDisplay() {

        val isEditing = inputSharedViewModel.noteContentIsEditing.value!!
        val undoEmpty = noteContentStateManager.checkIfUndoEmpty()

        if (isEditing && !undoEmpty) {

            generalButtonIVHelper.enableButtonIV(editInputViewBinding.undoButtonIV,
                requireContext(), R.color.light_grey_3)

            editInputViewModel.setUndoBtnActive(!noteContentStateManager.checkIfUndoEmpty())
        } else {

            generalButtonIVHelper.disableButtonIV(editInputViewBinding.undoButtonIV,
                requireContext())
        }
    }


    private fun updateRedoBtnDisplay() {

        val isEditing = inputSharedViewModel.noteContentIsEditing.value!!
        val redoEmpty = noteContentStateManager.checkIfRedoEmpty()

        if (isEditing && !redoEmpty) {

            generalButtonIVHelper.enableButtonIV(editInputViewBinding.redoButtonIV,
                requireContext(), R.color.light_grey_3)
2
            editInputViewModel.setRedoBtnActive(!noteContentStateManager.checkIfRedoEmpty())
        } else {

            generalButtonIVHelper.disableButtonIV(editInputViewBinding.redoButtonIV,
                requireContext())
        }
    }

    private fun imageCountLessThanOne(): Boolean {

        val imageSpans =
            noteContentET.text?.getSpans(0, noteContentET.length(), CustomImageSpan::class.java)

        return imageSpans.isNullOrEmpty()
    }

}