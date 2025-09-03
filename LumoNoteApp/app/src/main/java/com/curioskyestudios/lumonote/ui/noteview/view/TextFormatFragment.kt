package com.curioskyestudios.lumonote.ui.noteview.view

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.curioskyestudios.lumonote.R
import com.curioskyestudios.lumonote.data.models.TextSize
import com.curioskyestudios.lumonote.data.models.TextStyle
import com.curioskyestudios.lumonote.databinding.FragmentTextFormatBinding
import com.curioskyestudios.lumonote.ui.noteview.viewmodel.InputViewModel
import com.curioskyestudios.lumonote.ui.noteview.viewmodel.TextHelperViewModel
import com.curioskyestudios.lumonote.utils.general.GeneralUIHelper
import com.curioskyestudios.lumonote.utils.textformathelper.TextBulletHelper
import com.curioskyestudios.lumonote.utils.textformathelper.TextSizeHelper
import com.curioskyestudios.lumonote.utils.textformathelper.TextStyleHelper


class TextFormatFragment: Fragment() {

    // Real binding variable that can be null when the view is destroyed
    // Naming it with an underscore (_textFormatViewBinding) is just a convention
    // → It signals: "don’t use me directly, I’m just the backing field"
    private var _textFormatViewBinding: FragmentTextFormatBinding? = null

    // Safe-to-use version of binding
    // Uses Kotlin’s getter so we don’t need to write _textFormatViewBinding!! everywhere
    // The "!!" means it assumes _textFormatViewBinding is not null between onCreateView & onDestroyView
    private val textFormatViewBinding get() = _textFormatViewBinding!!

    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()

    private lateinit var inputViewModel: InputViewModel
    private lateinit var textHelperViewModel: TextHelperViewModel
    private var textStyleHelper: TextStyleHelper? = null
    private var textSizeHelper: TextSizeHelper? = null
    private var textBulletHelper: TextBulletHelper? = null


    // Called when the Fragment is created (before the UI exists)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        inputViewModel = ViewModelProvider(requireActivity()).get(InputViewModel::class.java)

        textHelperViewModel = ViewModelProvider(requireActivity()).get(TextHelperViewModel::class.java)

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

        textStyleHelper = textHelperViewModel.textStyleHelper.value
        textSizeHelper = textHelperViewModel.textSizeHelper.value
        textBulletHelper = textHelperViewModel.textBulletHelper.value

        observeUIInputVMValues()

        observeSpanInputVMValues()

        setOnClickListeners()

    }


    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {

        super.onDestroyView()
        _textFormatViewBinding = null // prevent memory leaks by clearing reference
    }



    private fun observeSpanInputVMValues() {

        inputViewModel.relativeSizeSpans.observe(viewLifecycleOwner) {

            val relativeSizeSpans = inputViewModel.relativeSizeSpans.value

            if (!relativeSizeSpans.isNullOrEmpty()) {

                for (span in relativeSizeSpans) {

                    Log.d("Relative Spans", "Span class: ${span::class.java.name}")
                }

            } else {

                Log.d("Relative Spans", "None")
            }


            if (!relativeSizeSpans.isNullOrEmpty()) {

                if (relativeSizeSpans[0].sizeChange == TextSize.H1.scaleFactor){

                    generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.h1ButtonIV,
                        R.color.gold)
                    generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.h2ButtonIV,
                        R.color.light_grey_1)
                }
                else if (relativeSizeSpans[0].sizeChange == TextSize.H2.scaleFactor){

                    generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.h2ButtonIV,
                        R.color.gold)
                    generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.h1ButtonIV,
                        R.color.light_grey_1)
                }

                generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.normalTextButtonIV,
                    R.color.light_grey_1)
            }
            else {

                generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.normalTextButtonIV,
                    R.color.gold)

                generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.h1ButtonIV,
                    R.color.light_grey_1)

                generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.h2ButtonIV,
                    R.color.light_grey_1)
            }
        }


        inputViewModel.styleSpans.observe(viewLifecycleOwner){
            //Log.d("EditInput", "Point 2")

            val start = inputViewModel.selectionStart.value
            val end = inputViewModel.selectionEnd.value
            val styleSpans = inputViewModel.styleSpans.value ?: arrayOf()


            if (textStyleHelper!!.isAllSpanned(TextStyle.BOLD) ||
                (styleSpans.any { it.style == Typeface.BOLD } && start == end)) {
                //Log.d("FormatBold",  "Point 2")

                generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.boldButtonIV,
                    R.color.gold)
            } else {

                generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.boldButtonIV,
                    R.color.light_grey_1)
            }

            if (textStyleHelper!!.isAllSpanned(TextStyle.ITALICS) ||
                (styleSpans.any { it.style == Typeface.ITALIC } && start == end)) {
                //Log.d("FormatBold",  "Point 2")

                generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.italicsButtonIV,
                    R.color.gold)
            } else {

                generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.italicsButtonIV,
                    R.color.light_grey_1)
            }
        }

        inputViewModel.underlineSpans.observe(viewLifecycleOwner) {

            val start = inputViewModel.selectionStart.value
            val end = inputViewModel.selectionEnd.value
            val underlineSpans =
                inputViewModel.underlineSpans.value ?: arrayOf()

            if (textStyleHelper!!.isAllSpanned(TextStyle.UNDERLINE) ||
                (underlineSpans.isNotEmpty() && start == end) ) {

                generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.underlineButtonIV,
                    R.color.gold)
            } else {

                generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.underlineButtonIV,
                    R.color.light_grey_1)
            }
        }
    }


    private fun observeUIInputVMValues() {

        inputViewModel.isEditing.observe(viewLifecycleOwner){

            if (inputViewModel.isEditing.value == true) {

                textFormatterOn()
            } else {

                textFormatterOff()
            }
            //Log.d("EditInput", "Point 1")
        }

        inputViewModel.openFormatter.observe(viewLifecycleOwner){

            if (inputViewModel.openFormatter.value == true) {

                textFormatterOn()
            } else {

                textFormatterOff()
            }
        }

    }


    private fun setOnClickListeners() {

        textFormatViewBinding.apply {

            normalTextButtonIV.setOnClickListener {
                textSizeHelper!!.formatAsHeader(TextSize.NORMAL)
            }
            h1ButtonIV.setOnClickListener {
                textSizeHelper!!.formatAsHeader(TextSize.H1)
            }
            h2ButtonIV.setOnClickListener {
                textSizeHelper!!.formatAsHeader(TextSize.H2)
            }


            boldButtonIV.setOnClickListener {
                textStyleHelper!!.formatText(TextStyle.BOLD)
            }
            italicsButtonIV.setOnClickListener {
                textStyleHelper!!.formatText(TextStyle.ITALICS)
            }
            underlineButtonIV.setOnClickListener {
                textStyleHelper!!.formatText(TextStyle.UNDERLINE)
            }
            bulletButtonIV.setOnClickListener {
                textBulletHelper!!.formatBullet()
            }

        }


        textFormatViewBinding.clearFormatsButtonIV.setOnClickListener {

            textStyleHelper!!.clearTextStyles()

            generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.boldButtonIV,
                R.color.light_grey_1)
            generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.italicsButtonIV,
                R.color.light_grey_1)
            generalUIHelper.changeButtonIVColor(requireContext(), textFormatViewBinding.underlineButtonIV,
                R.color.light_grey_1)
        }
    }


    private fun textFormatterOn() {

        // Show the view
        textFormatViewBinding.formatTextSectionRL.visibility = View.VISIBLE
    }

    private fun textFormatterOff() {

        // Hide the view
        textFormatViewBinding.formatTextSectionRL.visibility = View.GONE
    }

    private fun toggleTextFormatter() {

        if (textFormatViewBinding.formatTextSectionRL.visibility == View.VISIBLE) {

            textFormatterOff()
        } else {

            textFormatterOn()
        }
    }


}