package com.ckestudios.lumonote.ui.noteview.view.textformatting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ckestudios.lumonote.data.models.TextSize
import com.ckestudios.lumonote.databinding.FragmentSizeFormatBinding
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.ui.noteview.viewmodel.InputSharedViewModel
import com.ckestudios.lumonote.utils.basichelpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.textformatting.SizeTextFormatter


class SizeFormatFragment: Fragment() {

    private var _sizeFormatViewBinding: FragmentSizeFormatBinding? = null
    private val sizeFormatViewBinding get() = _sizeFormatViewBinding!!


    private lateinit var inputSharedViewModel: InputSharedViewModel
    private lateinit var editContentSharedViewModel: EditContentSharedViewModel

    private val generalButtonIVHelper: GeneralButtonIVHelper = GeneralButtonIVHelper()
    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()

    private lateinit var noteContentET: CustomSelectionET
    private lateinit var sizeTextFormatter: SizeTextFormatter


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
        _sizeFormatViewBinding = FragmentSizeFormatBinding.inflate(inflater, container, false)
        return sizeFormatViewBinding.root // return the root view for the fragment
    }

    // Called when the Fragment creates its view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        noteContentET =
            editContentSharedViewModel.noteContentEditTextView.value as CustomSelectionET

        sizeTextFormatter = SizeTextFormatter(noteContentET)

        setOnClickListeners()

        observeEditContentVMValues()
    }


    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {

        super.onDestroyView()
        _sizeFormatViewBinding = null // prevent memory leaks by clearing reference
    }


    private fun setOnClickListeners() {

        sizeFormatViewBinding.apply {

            normalTextButtonIV.setOnClickListener {

                sizeTextFormatter.setSizeSpanType(TextSize.NORMAL)

                sizeTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd)

                updateHeaderActive(TextSize.NORMAL)
            }

            h1ButtonIV.setOnClickListener {

                sizeTextFormatter.setSizeSpanType(TextSize.H1)

                sizeTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd)

                updateHeaderActive(TextSize.H1)

            }

            h2ButtonIV.setOnClickListener {

                sizeTextFormatter.setSizeSpanType(TextSize.H2)

                sizeTextFormatter.processFormatting(noteContentET.selectionStart,
                    noteContentET.selectionEnd)

                updateHeaderActive(TextSize.H2)
            }

        }
    }


    private fun observeEditContentVMValues() {

        editContentSharedViewModel.apply {

            isNormalSized.observe(viewLifecycleOwner) {

                generalButtonIVHelper.updateButtonIVHighlight(
                    sizeFormatViewBinding.normalTextButtonIV, it, requireContext(), null)
            }

            isHeader1Sized.observe(viewLifecycleOwner) {

                generalButtonIVHelper.updateButtonIVHighlight(sizeFormatViewBinding.h1ButtonIV,
                    it, requireContext(), null)
            }

            isHeader2Sized.observe(viewLifecycleOwner) {

                generalButtonIVHelper.updateButtonIVHighlight(sizeFormatViewBinding.h2ButtonIV,
                    it, requireContext(), null)
            }
        }

    }


    private fun updateHeaderActive(sizeType: TextSize) {

        when (sizeType) {

            TextSize.H1 -> editContentSharedViewModel.setIsHeader1Sized(true)
            TextSize.H2 -> editContentSharedViewModel.setIsHeader2Sized(true)
            TextSize.NORMAL -> editContentSharedViewModel.setIsNormalSized(true)
        }
    }

}