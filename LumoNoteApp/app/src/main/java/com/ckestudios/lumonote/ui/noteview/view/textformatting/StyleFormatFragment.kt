package com.ckestudios.lumonote.ui.noteview.view.textformatting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.data.models.TextStyle
import com.ckestudios.lumonote.databinding.FragmentStyleFormatBinding
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletResource
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.ckestudios.lumonote.utils.helpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.helpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.helpers.TextFormatHelper
import com.ckestudios.lumonote.utils.textformatting.BasicTextFormatter
import com.ckestudios.lumonote.utils.textformatting.BulletTextFormatter
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

                basicTextFormatter.setBasicSpanType(TextStyle.BOLD,
                    noteContentET.selectionStart, noteContentET.selectionEnd)

                updateBasicFormatActive(TextStyle.BOLD)
            }

            italicsButtonIV.setOnClickListener {

                basicTextFormatter.setBasicSpanType(TextStyle.ITALICS,
                    noteContentET.selectionStart, noteContentET.selectionEnd)

                updateBasicFormatActive(TextStyle.ITALICS)
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

                toggleStyleButtonsDisplay(isEmpty)

                if (!isEmpty) {

                    updateUnderlineActive()
                    updateBasicFormatActive(TextStyle.BOLD)
                    updateBasicFormatActive(TextStyle.ITALICS)
                }
            }

            currentLineHasText.observe(viewLifecycleOwner){ hasText ->

                toggleBulletButtonDisplay(hasText)

                updateBulletedActive()
            }
        }
    }

    private fun observeEditContentVMValues() {

        editContentSharedViewModel.apply {

            isBold.observe(viewLifecycleOwner) { isTrue ->

                generalButtonIVHelper.updateButtonIVHighlight(
                    styleFormatViewBinding.boldButtonIV, isTrue, requireContext())
            }

            isItalics.observe(viewLifecycleOwner) { isTrue ->

                generalButtonIVHelper.updateButtonIVHighlight(
                    styleFormatViewBinding.italicsButtonIV, isTrue, requireContext())
            }

            isUnderlined.observe(viewLifecycleOwner) { isTrue ->

                generalButtonIVHelper.updateButtonIVHighlight(
                    styleFormatViewBinding.underlineButtonIV, isTrue, requireContext())
            }

            isBulleted.observe(viewLifecycleOwner){ isTrue ->

                generalButtonIVHelper.updateButtonIVHighlight(
                    styleFormatViewBinding.bulletButtonIV, isTrue, requireContext())
            }
        }

    }

    private fun toggleBulletButtonDisplay(shouldDisable: Boolean) {

        if (shouldDisable) {

            generalButtonIVHelper.disableButtonIV(styleFormatViewBinding.bulletButtonIV,
                requireContext())
        } else {

            generalButtonIVHelper.enableButtonIV(styleFormatViewBinding.bulletButtonIV,
                requireContext())
        }
    }

    private fun toggleStyleButtonsDisplay(shouldDisableButtons: Boolean) {

        if (shouldDisableButtons) {

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
                requireContext())
            generalButtonIVHelper.enableButtonIV(styleFormatViewBinding.boldButtonIV,
                requireContext())
            generalButtonIVHelper.enableButtonIV(styleFormatViewBinding.italicsButtonIV,
                requireContext())
            generalButtonIVHelper.enableButtonIV(styleFormatViewBinding.underlineButtonIV,
                requireContext())
        }
    }


    private fun updateBasicFormatActive(spanType: TextStyle) {

        val isFullyBasicSpanned =
            basicTextFormatter.isSelectionFullySpanned(noteContentET.selectionStart,
                noteContentET.selectionEnd, spanType)

//        Log.d("styleformatfrag", "isFullyBasicSpanned: $isFullyBasicSpanned")

        when (spanType) {

            TextStyle.BOLD -> editContentSharedViewModel.setIsBold(isFullyBasicSpanned)
            TextStyle.ITALICS -> editContentSharedViewModel.setIsItalics(isFullyBasicSpanned)
            else -> {}
        }
    }

    private fun updateUnderlineActive() {

        val isUnderlined =
            underlineTextFormatter.isSelectionFullySpanned(noteContentET.selectionStart,
                noteContentET.selectionEnd)

//        Log.d("styleformatfrag", "isFullyUnderlinedSpanned: $isUnderlined")

        editContentSharedViewModel.setIsUnderlined(isUnderlined)
    }

    private fun updateBulletedActive() {

        val isBulleted =
            bulletTextFormatter.isSelectionFullySpanned(noteContentET.selectionStart,
                noteContentET.selectionEnd)

        editContentSharedViewModel.setIsBulleted(isBulleted!!)
    }


}