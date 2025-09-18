package com.ckestudios.lumonote.ui.noteview.view

import android.content.Context
import com.ckestudios.lumonote.data.models.TextSize
import com.ckestudios.lumonote.data.models.TextStyle
import com.ckestudios.lumonote.databinding.FragmentTextFormatBinding
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import com.ckestudios.lumonote.ui.noteview.viewmodel.EditContentSharedViewModel
import com.ckestudios.lumonote.utils.helpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.helpers.GeneralUIHelper
import com.ckestudios.lumonote.utils.textformatting.BasicTextFormatter
import com.ckestudios.lumonote.utils.textformatting.BulletTextFormatter
import com.ckestudios.lumonote.utils.textformatting.SizeTextFormatter
import com.ckestudios.lumonote.utils.textformatting.UnderlineTextFormatter

class TextFormatCompanion (private val context: Context,
        private val textFormatViewBinding: FragmentTextFormatBinding,
        private val editContentSharedViewModel: EditContentSharedViewModel,
        private val noteContentET: CustomSelectionET) {

    private val generalButtonIVHelper: GeneralButtonIVHelper = GeneralButtonIVHelper()
    private val generalUIHelper: GeneralUIHelper = GeneralUIHelper()

    private var basicTextFormatter = BasicTextFormatter(noteContentET)
    private var underlineTextFormatter = UnderlineTextFormatter(noteContentET)
    private var sizeTextFormatter = SizeTextFormatter(noteContentET)
    private var bulletTextFormatter = BulletTextFormatter(noteContentET)


    fun updateHeaderActive(sizeType: TextSize) {

        when (sizeType) {

            TextSize.H1 -> editContentSharedViewModel.setIsHeader1Sized(true)
            TextSize.H2 -> editContentSharedViewModel.setIsHeader2Sized(true)
            TextSize.NORMAL -> editContentSharedViewModel.setIsNormalSized(true)
        }
    }

    fun updateBasicFormatActive(spanType: TextStyle) {

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

    fun updateUnderlineActive() {

        val isFullySpanned =
            underlineTextFormatter.isSelectionFullySpanned(noteContentET.selectionStart,
                noteContentET.selectionEnd)

        editContentSharedViewModel.setIsUnderlined(isFullySpanned)
    }


}