package com.ckestudios.lumonote.ui.noteview.view.textformatting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.databinding.FragmentTextFormatBinding
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.ckestudios.lumonote.utils.basichelpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.state.StateManager
import com.ckestudios.lumonote.utils.textformatting.BasicTextFormatter
import com.ckestudios.lumonote.utils.textformatting.BulletTextFormatter
import com.ckestudios.lumonote.utils.textformatting.TextFormatHelper
import com.ckestudios.lumonote.utils.textformatting.UnderlineTextFormatter


class TextFormatFragment: Fragment() {

    // Real binding variable that can be null when the view is destroyed
    // Naming it with an underscore (_textFormatViewBinding) is just a convention
    // → It signals: "don’t use me directly, I’m just the backing field"
    private var _textFormatViewBinding: FragmentTextFormatBinding? = null

    // Safe-to-use version of binding
    // Uses Kotlin’s getter so we don’t need to write _textFormatViewBinding!! everywhere
    // The "!!" means it assumes _textFormatViewBinding is not null between onCreateView & onDestroyView
    private val textFormatViewBinding get() = _textFormatViewBinding!!


    private lateinit var inputSharedViewModel: InputSharedViewModel
    private lateinit var editContentSharedViewModel: EditContentSharedViewModel

    private val generalButtonIVHelper: GeneralButtonIVHelper = GeneralButtonIVHelper()
    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()

    private lateinit var noteContentET: CustomSelectionET
    private lateinit var basicTextFormatter: BasicTextFormatter
    private lateinit var underlineTextFormatter: UnderlineTextFormatter
    private lateinit var bulletTextFormatter: BulletTextFormatter

    private val textFormatHelper = TextFormatHelper()


    // Called when the Fragment is created (before the UI exists)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        inputSharedViewModel = ViewModelProvider(requireActivity()).get(InputSharedViewModel::class.java)

        editContentSharedViewModel =
            ViewModelProvider(requireActivity()).get(EditContentSharedViewModel::class.java)
    }

    // Called when the view is created (safe place to interact with UI)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for requireContext() fragment
        _textFormatViewBinding = FragmentTextFormatBinding.inflate(inflater, container, false)
        return textFormatViewBinding.root // return the root view for the fragment
    }

    // Called when the Fragment creates its view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        noteContentET =
            editContentSharedViewModel.noteContentEditTextView.value as CustomSelectionET

        val stateManager =
            editContentSharedViewModel.noteContentStateManager.value as StateManager

        basicTextFormatter = BasicTextFormatter(noteContentET, stateManager)
        underlineTextFormatter = UnderlineTextFormatter(noteContentET, stateManager)
        bulletTextFormatter = BulletTextFormatter(noteContentET, stateManager)


        detectSelectionFormattingOnChange()

        observeInputSharedVMValues()
    }


    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {

        super.onDestroyView()
        _textFormatViewBinding = null // prevent memory leaks by clearing reference
    }

    private fun detectSelectionFormattingOnChange() {

        noteContentET.onSelectionChange = { selectStart, selectEnd ->

            if (selectStart == selectEnd || noteContentET.text.isNullOrEmpty()) {

                inputSharedViewModel.setContentSelectionIsEmpty(true)
            }
            else {

                inputSharedViewModel.setContentSelectionIsEmpty(false)
            }


            inputSharedViewModel.setCurrentLineHasText(

                textFormatHelper.checkIfCurrentLineHasText(noteContentET)
            )

            inputSharedViewModel.setCurrentLineHasImage(

                textFormatHelper.checkIfCurrentLineHasImage(noteContentET)
            )
        }
    }



    private fun observeInputSharedVMValues() {

        inputSharedViewModel.apply {

            noteContentIsEditing.observe(viewLifecycleOwner){ isEditing ->

                setShouldOpenFormatter(isEditing)
            }

            shouldOpenFormatter.observe(viewLifecycleOwner){ shouldOpen ->

                generalUIHelper.changeViewVisibility(
                    textFormatViewBinding.formatTextSectionRL, shouldOpen)
            }
        }

    }


}