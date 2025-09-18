package com.ckestudios.lumonote.ui.noteview.view.textformatting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.data.models.BulletType
import com.ckestudios.lumonote.data.models.TextStyle
import com.ckestudios.lumonote.databinding.FragmentStyleFormatBinding
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletResource
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.ckestudios.lumonote.utils.helpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.helpers.GeneralUIHelper
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

                basicTextFormatter.setBasicSpanType(TextStyle.BOLD)

                basicTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd)

                updateBasicFormatActive(TextStyle.BOLD)
            }

            italicsButtonIV.setOnClickListener {

                basicTextFormatter.setBasicSpanType(TextStyle.ITALICS)

                basicTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd)

                updateBasicFormatActive(TextStyle.ITALICS)
            }

            underlineButtonIV.setOnClickListener {

                underlineTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd)

                updateUnderlineActive()
            }


            bulletButtonIV.apply {

                setOnClickListener {

//                    val textBulletHelper = TextBulletHelper(noteContentET)
//                    textBulletHelper.formatBullet()

                    editContentSharedViewModel.apply {

                        setWasBulletBtnClicked(!wasBulletBtnClicked.value!!)
                    }

                    bulletTextFormatter.setBulletType(BulletType.DEFAULT, null, null)

                    bulletTextFormatter.processFormatting(noteContentET.selectionStart,
                        noteContentET.selectionEnd)

//                    generalUIHelper.displayFeedbackToast(requireContext(),
//                        "Long press for Custom Bullet", true)
                }

                setOnLongClickListener {

                    var intent = Intent(requireContext(), CustomBulletInputActivity::class.java)
                    startActivity(intent)

                    true // return true to indicate the event was consumed
                }

            }
        }


        styleFormatViewBinding.clearFormatsButtonIV.setOnClickListener {

            basicTextFormatter.clearFormatting(noteContentET.selectionStart,
                noteContentET.selectionEnd)

            updateBasicFormatActive(TextStyle.NONE)
        }
    }


    private fun observeCustomBulletValues() {

        CustomBulletResource.customBullet.observe(viewLifecycleOwner){ bullet ->

            generalUIHelper.displayFeedbackToast(requireContext(),
                bullet.toString(), true)

            Log.d("textformatfrag", "tff bullet: $bullet")
        }

    }

    private fun observeInputSharedVMValues() {

        inputSharedViewModel.apply {

            isContentSelectionEmpty.observe(viewLifecycleOwner){ isEmpty ->

                toggleButtonsDisplay(isEmpty)

                if (!isEmpty) {

                    basicTextFormatter.setBasicSpanType(TextStyle.BOLD)
                    updateBasicFormatActive(TextStyle.BOLD)

                    basicTextFormatter.setBasicSpanType(TextStyle.ITALICS)
                    updateBasicFormatActive(TextStyle.ITALICS)

                    updateUnderlineActive()
                }
            }

        }

    }

    private fun observeEditContentVMValues() {

        editContentSharedViewModel.apply {

            isBold.observe(viewLifecycleOwner) {

                generalButtonIVHelper.updateButtonIVHighlight(styleFormatViewBinding.boldButtonIV,
                    it, requireContext())
            }

            isItalics.observe(viewLifecycleOwner) {

                generalButtonIVHelper.updateButtonIVHighlight(styleFormatViewBinding.italicsButtonIV,
                    it, requireContext())
            }

            isUnderlined.observe(viewLifecycleOwner) {

                generalButtonIVHelper.updateButtonIVHighlight(styleFormatViewBinding.underlineButtonIV,
                    it, requireContext())
            }

            wasBulletBtnClicked.observe(viewLifecycleOwner){ wasClicked ->

                if (wasClicked) {
                    generalButtonIVHelper.changeButtonIVImage(styleFormatViewBinding.bulletButtonIV,
                        R.drawable.baseline_format_list_numbered_24)
                } else {
                    generalButtonIVHelper.changeButtonIVImage(styleFormatViewBinding.bulletButtonIV,
                        R.drawable.baseline_format_list_bulleted_24)
                }
            }
        }

    }

    private fun toggleButtonsDisplay(shouldDisableButtons: Boolean) {

        if (shouldDisableButtons) {

            generalButtonIVHelper.disableButtonIV(styleFormatViewBinding.clearFormatsButtonIV,
                requireContext())
            generalButtonIVHelper.disableButtonIV(styleFormatViewBinding.boldButtonIV,
                requireContext())
            generalButtonIVHelper.disableButtonIV(styleFormatViewBinding.italicsButtonIV,
                requireContext())
            generalButtonIVHelper.disableButtonIV(styleFormatViewBinding.underlineButtonIV,
                requireContext())
        }
        else {

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

        val isFullySpanned =
            basicTextFormatter.isSelectionFullySpanned(noteContentET.selectionStart,
                noteContentET.selectionEnd, spanType)

        when (spanType) {

            TextStyle.BOLD -> editContentSharedViewModel.setIsBold(isFullySpanned)
            TextStyle.ITALICS -> editContentSharedViewModel.setIsItalics(isFullySpanned)
            else -> {
                editContentSharedViewModel.setIsBold(false)
                editContentSharedViewModel.setIsItalics(false)
                editContentSharedViewModel.setIsUnderlined(false)
            }
        }
    }

    private fun updateUnderlineActive() {

        val isFullySpanned =
            underlineTextFormatter.isSelectionFullySpanned(noteContentET.selectionStart,
                noteContentET.selectionEnd)

        editContentSharedViewModel.setIsUnderlined(isFullySpanned)
    }

}