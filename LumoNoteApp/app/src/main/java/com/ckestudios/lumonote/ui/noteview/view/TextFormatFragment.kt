package com.ckestudios.lumonote.ui.noteview.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.data.models.BulletType
import com.ckestudios.lumonote.data.models.TextSize
import com.ckestudios.lumonote.data.models.TextStyle
import com.ckestudios.lumonote.databinding.FragmentTextFormatBinding
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletResource
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.ckestudios.lumonote.utils.helpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.helpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.textformatting.BasicTextFormatter
import com.ckestudios.lumonote.utils.textformatting.BulletTextFormatter
import com.ckestudios.lumonote.utils.textformatting.SizeTextFormatter
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
    private lateinit var sizeTextFormatter: SizeTextFormatter
    private lateinit var bulletTextFormatter: BulletTextFormatter

    private lateinit var customBulletLauncher: ActivityResultLauncher<Intent>
    private lateinit var textFormatCompanion: TextFormatCompanion


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

        textFormatCompanion = TextFormatCompanion(requireContext(), textFormatViewBinding,
            editContentSharedViewModel, noteContentET)

        basicTextFormatter = BasicTextFormatter(noteContentET)
        underlineTextFormatter = UnderlineTextFormatter(noteContentET)
        sizeTextFormatter = SizeTextFormatter(noteContentET)
        bulletTextFormatter = BulletTextFormatter(noteContentET)


        setOnClickListeners()

        observeCustomBulletValues()

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

                sizeTextFormatter.setSizeSpanType(TextSize.NORMAL)

                sizeTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd)

                textFormatCompanion.updateHeaderActive(TextSize.NORMAL)
            }
            h1ButtonIV.setOnClickListener {

                sizeTextFormatter.setSizeSpanType(TextSize.H1)

                sizeTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd)

                textFormatCompanion.updateHeaderActive(TextSize.H1)

            }
            h2ButtonIV.setOnClickListener {

                sizeTextFormatter.setSizeSpanType(TextSize.H2)

                sizeTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd)

                textFormatCompanion.updateHeaderActive(TextSize.H2)
            }


            boldButtonIV.setOnClickListener {

                basicTextFormatter.setBasicSpanType(TextStyle.BOLD)

                basicTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd)

                textFormatCompanion.updateBasicFormatActive(TextStyle.BOLD)
            }
            italicsButtonIV.setOnClickListener {

                basicTextFormatter.setBasicSpanType(TextStyle.ITALICS)

                basicTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd)

                textFormatCompanion.updateBasicFormatActive(TextStyle.ITALICS)
            }

            underlineButtonIV.setOnClickListener {

                underlineTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd)

                textFormatCompanion.updateUnderlineActive()
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


        textFormatViewBinding.clearFormatsButtonIV.setOnClickListener {

            basicTextFormatter.clearFormatting(noteContentET.selectionStart,
                noteContentET.selectionEnd)

            textFormatCompanion.updateBasicFormatActive(TextStyle.NONE)
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

            noteContentIsEditing.observe(viewLifecycleOwner){ isEditing ->

                setShouldOpenFormatter(isEditing)
            }

            isContentSelectionEmpty.observe(viewLifecycleOwner){ isEmpty ->

                toggleButtonsDisplay(isEmpty)

                if (!isEmpty) {
                    basicTextFormatter.setBasicSpanType(TextStyle.BOLD)
                    textFormatCompanion.updateBasicFormatActive(TextStyle.BOLD)
                    basicTextFormatter.setBasicSpanType(TextStyle.ITALICS)
                    textFormatCompanion.updateBasicFormatActive(TextStyle.ITALICS)
                    textFormatCompanion.updateUnderlineActive()
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

                generalButtonIVHelper.updateButtonIVHighlight(
                    textFormatViewBinding.normalTextButtonIV, it, requireContext())
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

            wasBulletBtnClicked.observe(viewLifecycleOwner){ wasClicked ->

                if (wasClicked) {
                    generalButtonIVHelper.changeButtonIVImage(textFormatViewBinding.bulletButtonIV,
                        R.drawable.baseline_format_list_numbered_24)
                } else {
                    generalButtonIVHelper.changeButtonIVImage(textFormatViewBinding.bulletButtonIV,
                        R.drawable.baseline_format_list_bulleted_24)
                }
            }
        }

    }

    fun toggleButtonsDisplay(shouldDisableButtons: Boolean) {

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