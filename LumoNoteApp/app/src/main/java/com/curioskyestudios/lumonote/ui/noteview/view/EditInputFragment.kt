package com.curioskyestudios.lumonote.ui.noteview.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.curioskyestudios.lumonote.R
import com.curioskyestudios.lumonote.databinding.FragmentEditInputBinding
import com.curioskyestudios.lumonote.ui.noteview.viewmodel.InputViewModel
import com.curioskyestudios.lumonote.utils.general.GeneralUIHelper


class EditInputFragment : Fragment() {

    // Real binding variable that can be null when the view is destroyed
    // Naming it with an underscore (_editInputViewBinding) is just a convention
    // → It signals: "don’t use me directly, I’m just the backing field"
    private var _editInputViewBinding: FragmentEditInputBinding? = null

    // Safe-to-use version of binding
    // Uses Kotlin’s getter so we don’t need to write _editInputViewBinding!! everywhere
    // The "!!" means it assumes _editInputViewBinding is not null between onCreateView & onDestroyView
    private val editInputViewBinding get() = _editInputViewBinding!!

    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()

    private lateinit var inputViewModel: InputViewModel

    private var textFormatBtnActive = false
    private var colorBtnActive = false
    private var checklistBtnActive = false



    // Called when the Fragment is created (before the UI exists)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        inputViewModel = ViewModelProvider(requireActivity()).get(InputViewModel::class.java)

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

        inputViewModel.isEditing.observe(viewLifecycleOwner){

            textFormatBtnActive = inputViewModel.isEditing.value!!

            updateButtonIVHighlight(editInputViewBinding.textFormatButtonIV, textFormatBtnActive)

            //Log.d("EditInput", "Point 1")
        }


        setOnClickListeners()
    }


    // Called when the view is destroyed (e.g. when navigating away)
    override fun onDestroyView() {

        super.onDestroyView()
        _editInputViewBinding = null // prevent memory leaks by clearing reference
    }


    private fun setOnClickListeners() {

        editInputViewBinding.apply {

            colorButtonIV.setOnClickListener {

                colorBtnActive = !colorBtnActive
                updateButtonIVHighlight(colorButtonIV, colorBtnActive)
            }


            checkListButtonIV.setOnClickListener {

                checklistBtnActive = !checklistBtnActive
                updateButtonIVHighlight(checkListButtonIV, checklistBtnActive)
            }


            imageButtonIV.setOnClickListener {

                highlightButtonIV(imageButtonIV)

                Handler(Looper.getMainLooper()).postDelayed({
                    // Action here
                    unhighlightButtonIV(imageButtonIV)
                }, 1000) // 1000 ms = 1 seconds
            }

            textFormatButtonIV.setOnClickListener {

                textFormatBtnActive = !textFormatBtnActive
                updateButtonIVHighlight(textFormatButtonIV, textFormatBtnActive)
            }
        }
    }


    private fun highlightButtonIV(buttonIV: ImageView) {

        // highlight button
        generalUIHelper.changeButtonIVColor(requireContext(), buttonIV, R.color.gold)
    }

    private fun unhighlightButtonIV(buttonIV: ImageView) {

        // unhighlight button
        generalUIHelper.changeButtonIVColor(requireContext(), buttonIV, R.color.light_grey_1)

    }

    private fun updateButtonIVHighlight(buttonIV: ImageView, isActive: Boolean) {

        if (buttonIV == editInputViewBinding.textFormatButtonIV) {

            inputViewModel.setOpenFormatter(isActive)
        }

        if (isActive) {

            highlightButtonIV(buttonIV)
        } else {

            unhighlightButtonIV(buttonIV)
        }
    }


}