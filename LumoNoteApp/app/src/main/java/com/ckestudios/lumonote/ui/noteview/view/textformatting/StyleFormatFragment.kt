package com.ckestudios.lumonote.ui.noteview.view.textformatting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.databinding.FragmentStyleFormatBinding
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletResource
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.ckestudios.lumonote.utils.basichelpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.textformatting.BasicTextFormatter
import com.ckestudios.lumonote.utils.textformatting.BulletTextFormatter
import com.ckestudios.lumonote.utils.textformatting.TextFormatHelper
import com.ckestudios.lumonote.utils.textformatting.UnderlineTextFormatter


class StyleFormatFragment: Fragment() {

    private var _styleFormatViewBinding: FragmentStyleFormatBinding? = null
    private val styleFormatViewBinding get() = _styleFormatViewBinding!!


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
        _styleFormatViewBinding = FragmentStyleFormatBinding.inflate(inflater, container, false)
        return styleFormatViewBinding.root // return the root view for the fragment
    }

    // Called when the Fragment creates its view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        noteContentET =
            editContentSharedViewModel.noteContentEditTextView.value as CustomSelectionET

        basicTextFormatter = BasicTextFormatter(noteContentET)
        underlineTextFormatter = UnderlineTextFormatter(noteContentET)
        bulletTextFormatter = BulletTextFormatter(noteContentET)


        setOnClickListeners()

        observeCustomBulletValues()

        observeInputSharedVMValues()

        observeEditContentVMValues()
    }


    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {

        super.onDestroyView()
        _styleFormatViewBinding = null // prevent memory leaks by clearing reference
    }



    private fun setOnClickListeners() {

        styleFormatViewBinding.apply {

            boldButtonIV.setOnClickListener {

                basicTextFormatter.setBasicSpanType(SpanType.BOLD_SPAN,
                    noteContentET.selectionStart, noteContentET.selectionEnd)

                updateBasicFormatActive(SpanType.BOLD_SPAN)
            }

            italicsButtonIV.setOnClickListener {

                basicTextFormatter.setBasicSpanType(SpanType.ITALICS_SPAN,
                    noteContentET.selectionStart, noteContentET.selectionEnd)

                updateBasicFormatActive(SpanType.ITALICS_SPAN)
            }

            underlineButtonIV.setOnClickListener {

                underlineTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd)

                updateUnderlineActive()
            }


            bulletButtonIV.apply {

                setOnClickListener {

                    bulletTextFormatter.processAsDefaultBullet(noteContentET.selectionStart,
                        noteContentET.selectionEnd)

                    updateBulletedActive()
                }

                setOnLongClickListener {

                    val intent = Intent(requireContext(), CustomBulletInputActivity::class.java)
                    startActivity(intent)

                    updateBulletedActive()

                    true // return true to indicate the event was consumed
                }

            }
        }


        styleFormatViewBinding.clearFormatsButtonIV.setOnClickListener {

            generalButtonIVHelper.playSelectionIndication(requireContext(),
                styleFormatViewBinding.clearFormatsButtonIV)

            textFormatHelper.clearBasicFormatting(noteContentET.selectionStart,
                noteContentET.selectionEnd, noteContentET.text!!)

            editContentSharedViewModel.setIsBold(false)
            editContentSharedViewModel.setIsItalics(false)
            editContentSharedViewModel.setIsUnderlined(false)
        }
    }


    private fun observeCustomBulletValues() {

        CustomBulletResource.customBullet.observe(viewLifecycleOwner){ bullet ->

            bulletTextFormatter.processAsCustomBullet(noteContentET.selectionStart,
                noteContentET.selectionEnd, bullet)
        }
    }

    private fun observeInputSharedVMValues() {

        inputSharedViewModel.apply {

            isContentSelectionEmpty.observe(viewLifecycleOwner){ isEmpty ->

                toggleStyleButtonsDisplay()

                if (!isEmpty) {

                    updateUnderlineActive()
                    updateBasicFormatActive(SpanType.BOLD_SPAN)
                    updateBasicFormatActive(SpanType.ITALICS_SPAN)
                }
            }

            currentLineHasText.observe(viewLifecycleOwner){

                toggleBulletButtonDisplay()
            }

            currentLineHasImage.observe(viewLifecycleOwner) {

                toggleBulletButtonDisplay()
            }
        }
    }

    private fun observeEditContentVMValues() {

        editContentSharedViewModel.apply {

            isBold.observe(viewLifecycleOwner) { isTrue ->

                generalButtonIVHelper.updateButtonIVHighlight(
                    styleFormatViewBinding.boldButtonIV, isTrue, requireContext(), null)
            }

            isItalics.observe(viewLifecycleOwner) { isTrue ->

                generalButtonIVHelper.updateButtonIVHighlight(
                    styleFormatViewBinding.italicsButtonIV, isTrue, requireContext(), null)
            }

            isUnderlined.observe(viewLifecycleOwner) { isTrue ->

                generalButtonIVHelper.updateButtonIVHighlight(
                    styleFormatViewBinding.underlineButtonIV, isTrue, requireContext(), null)
            }

            isBulleted.observe(viewLifecycleOwner){ isTrue ->

                generalButtonIVHelper.updateButtonIVHighlight(
                    styleFormatViewBinding.bulletButtonIV, isTrue, requireContext(), null)
            }
        }

    }

    private fun toggleBulletButtonDisplay() {

        val hasImage = inputSharedViewModel.currentLineHasImage.value!!
        val hasText = inputSharedViewModel.currentLineHasText.value!!

        if (!hasImage && hasText) {

            generalButtonIVHelper.enableButtonIV(styleFormatViewBinding.bulletButtonIV,
                requireContext(), null)
        } else {

            generalButtonIVHelper.disableButtonIV(styleFormatViewBinding.bulletButtonIV,
                requireContext())
        }
    }


    private fun toggleStyleButtonsDisplay() {

        val selectionIsEmpty = inputSharedViewModel.isContentSelectionEmpty.value!!
        val hasImage = inputSharedViewModel.currentLineHasImage.value!!


        if (selectionIsEmpty || hasImage) {

            generalButtonIVHelper.disableButtonIV(styleFormatViewBinding.clearFormatsButtonIV,
                requireContext())
            generalButtonIVHelper.disableButtonIV(styleFormatViewBinding.boldButtonIV,
                requireContext())
            generalButtonIVHelper.disableButtonIV(styleFormatViewBinding.italicsButtonIV,
                requireContext())
            generalButtonIVHelper.disableButtonIV(styleFormatViewBinding.underlineButtonIV,
                requireContext())
        } else {

            generalButtonIVHelper.enableButtonIV(styleFormatViewBinding.clearFormatsButtonIV,
                requireContext(), null)
            generalButtonIVHelper.enableButtonIV(styleFormatViewBinding.boldButtonIV,
                requireContext(), null)
            generalButtonIVHelper.enableButtonIV(styleFormatViewBinding.italicsButtonIV,
                requireContext(), null)
            generalButtonIVHelper.enableButtonIV(styleFormatViewBinding.underlineButtonIV,
                requireContext(), null)

            updateUnderlineActive()
            updateBasicFormatActive(SpanType.BOLD_SPAN)
            updateBasicFormatActive(SpanType.ITALICS_SPAN)
            updateBulletedActive()
        }
    }


    private fun updateBasicFormatActive(spanType: SpanType) {

        val isFullyBasicSpanned =
            basicTextFormatter.isSelectionFullySpanned(noteContentET.selectionStart,
                noteContentET.selectionEnd, spanType)

        when (spanType) {

            SpanType.BOLD_SPAN -> editContentSharedViewModel.setIsBold(isFullyBasicSpanned)
            SpanType.ITALICS_SPAN -> editContentSharedViewModel.setIsItalics(isFullyBasicSpanned)
            else -> {}
        }
    }

    private fun updateUnderlineActive() {

        val isUnderlined =
            underlineTextFormatter.isSelectionFullySpanned(noteContentET.selectionStart,
                noteContentET.selectionEnd)

        editContentSharedViewModel.setIsUnderlined(isUnderlined)
    }

    private fun updateBulletedActive() {

        val isBulleted =
            bulletTextFormatter.isSelectionFullySpanned(noteContentET.selectionStart,
                noteContentET.selectionEnd)

        editContentSharedViewModel.setIsBulleted(isBulleted!!)
    }


}