package com.ckestudios.lumonote.ui.noteview.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.data.models.TextSize
import com.ckestudios.lumonote.data.models.TextStyle
import com.ckestudios.lumonote.databinding.FragmentTextFormatBinding
import com.ckestudios.lumonote.ui.noteview.other.SpannedCustomSelectionET
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.ckestudios.lumonote.utils.general.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.general.GeneralUIHelper
import com.ckestudios.lumonote.utils.textformatting.BasicTextFormatter
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

    private val generalButtonIVHelper: GeneralButtonIVHelper = GeneralButtonIVHelper()
    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()

    private lateinit var inputSharedViewModel: InputSharedViewModel
    private lateinit var editContentSharedViewModel: EditContentSharedViewModel

    private lateinit var noteContentET: SpannedCustomSelectionET
    private lateinit var basicTextFormatter: BasicTextFormatter
    private lateinit var underlineTextFormatter: UnderlineTextFormatter


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
            editContentSharedViewModel.noteContentEditTextView.value as SpannedCustomSelectionET

        basicTextFormatter = BasicTextFormatter(noteContentET)
        underlineTextFormatter = UnderlineTextFormatter(noteContentET)

        setOnClickListeners()

        observeInputSharedVMValues()

        observeEditContentVMValues()
    }


    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {

        super.onDestroyView()
        _textFormatViewBinding = null // prevent memory leaks by clearing reference
    }


    private fun setOnClickListeners() {

        textFormatViewBinding.apply {

            normalTextButtonIV.setOnClickListener {

                noteContentET.getSizeHelper().formatAsHeader(TextSize.NORMAL)
            }
            h1ButtonIV.setOnClickListener {

                noteContentET.getSizeHelper().formatAsHeader(TextSize.H1)
            }
            h2ButtonIV.setOnClickListener {

                noteContentET.getSizeHelper().formatAsHeader(TextSize.H2)
            }


            boldButtonIV.setOnClickListener {

                //noteContentET.getStyleHelper().formatText(TextStyle.BOLD)
                basicTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd, TextStyle.BOLD)

                updateBasicFormatActive(TextStyle.BOLD)
            }
            italicsButtonIV.setOnClickListener {

                //noteContentET.getStyleHelper().formatText(TextStyle.ITALICS)
                basicTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd, TextStyle.ITALICS)

                updateBasicFormatActive(TextStyle.ITALICS)
            }

            underlineButtonIV.setOnClickListener {

                //noteContentET.getStyleHelper().formatText(TextStyle.UNDERLINE)
                underlineTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd)

                updateUnderlineActive()
            }


            bulletButtonIV.setOnClickListener {

                noteContentET.getBulletHelper().formatBullet(noteContentET.getStyleHelper())
            }
        }


        textFormatViewBinding.clearFormatsButtonIV.setOnClickListener {

            noteContentET.getStyleHelper().clearTextStyles()

            editContentSharedViewModel.setIsBold(false)
            editContentSharedViewModel.setIsItalics(false)
            editContentSharedViewModel.setIsUnderlined(false)
        }
    }


    private fun observeInputSharedVMValues() {

        inputSharedViewModel.apply {

            noteContentIsEditing.observe(viewLifecycleOwner){ isEditing ->

                setShouldOpenFormatter(isEditing)
            }

            isContentSelectionEmpty.observe(viewLifecycleOwner){ isEmpty ->

                toggleButtonsDisplay(isEmpty)

                if (!isEmpty) {

                    updateBasicFormatActive(TextStyle.BOLD)
                    updateBasicFormatActive(TextStyle.ITALICS)
                    updateUnderlineActive()
                }
            }

            shouldOpenFormatter.observe(viewLifecycleOwner){ shouldOpen ->

                generalUIHelper.changeViewVisibility(
                    textFormatViewBinding.formatTextSectionRL, shouldOpen)
            }

        }

    }

    private fun observeEditContentVMValues() {

        editContentSharedViewModel.apply {

            isNormalSized.observe(viewLifecycleOwner) {

                generalButtonIVHelper.updateButtonIVHighlight(textFormatViewBinding.normalTextButtonIV,
                    it, requireContext())
            }

            isHeader1Sized.observe(viewLifecycleOwner) {

                generalButtonIVHelper.updateButtonIVHighlight(textFormatViewBinding.h1ButtonIV,
                    it, requireContext())
            }

            isHeader2Sized.observe(viewLifecycleOwner) {

                generalButtonIVHelper.updateButtonIVHighlight(textFormatViewBinding.h2ButtonIV,
                    it, requireContext())
            }


            isBold.observe(viewLifecycleOwner) {

                generalButtonIVHelper.updateButtonIVHighlight(textFormatViewBinding.boldButtonIV,
                    it, requireContext())
            }

            isItalics.observe(viewLifecycleOwner) {

                generalButtonIVHelper.updateButtonIVHighlight(textFormatViewBinding.italicsButtonIV,
                    it, requireContext())
            }

            isUnderlined.observe(viewLifecycleOwner) {

                generalButtonIVHelper.updateButtonIVHighlight(textFormatViewBinding.underlineButtonIV,
                   it, requireContext())
            }
        }

    }

    private fun updateBasicFormatActive(spanType: TextStyle) {

        val isFullySpanned =
            when (spanType) {

                TextStyle.BOLD -> basicTextFormatter.checkIfFullyBold(noteContentET.selectionStart,
                    noteContentET.selectionEnd)
                TextStyle.ITALICS -> basicTextFormatter.checkIfFullyItalics(noteContentET.selectionStart,
                    noteContentET.selectionEnd)
                else -> null
            }

        if (isFullySpanned != null) {

            when (spanType) {

                TextStyle.BOLD -> editContentSharedViewModel.setIsBold(isFullySpanned)
                TextStyle.ITALICS -> editContentSharedViewModel.setIsItalics(isFullySpanned)
                else -> null
            }
        }
    }

    private fun updateUnderlineActive() {

        val isFullySpanned =
            underlineTextFormatter.checkIfFullyUnderlined(noteContentET.selectionStart,
                noteContentET.selectionEnd)

        editContentSharedViewModel.setIsUnderlined(isFullySpanned)
    }

    private fun toggleButtonsDisplay(shouldDisableButtons: Boolean) {

        if (shouldDisableButtons) {

            generalButtonIVHelper.disableButtonIV(textFormatViewBinding.clearFormatsButtonIV,
                requireContext())
            generalButtonIVHelper.disableButtonIV(textFormatViewBinding.boldButtonIV,
                requireContext())
            generalButtonIVHelper.disableButtonIV(textFormatViewBinding.italicsButtonIV,
                requireContext())
            generalButtonIVHelper.disableButtonIV(textFormatViewBinding.underlineButtonIV,
                requireContext())
        }
        else {

            generalButtonIVHelper.enableButtonIV(textFormatViewBinding.clearFormatsButtonIV,
                requireContext())
            generalButtonIVHelper.enableButtonIV(textFormatViewBinding.boldButtonIV,
                requireContext())
            generalButtonIVHelper.enableButtonIV(textFormatViewBinding.italicsButtonIV,
                requireContext())
            generalButtonIVHelper.enableButtonIV(textFormatViewBinding.underlineButtonIV,
                requireContext())
        }
    }

}