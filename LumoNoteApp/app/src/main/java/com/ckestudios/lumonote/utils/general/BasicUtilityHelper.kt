package com.ckestudios.lumonote.utils.general

import android.view.View
import com.ckestudios.lumonote.ui.noteview.other.SelectableEditText
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class BasicUtilityHelper {

    fun clearETViewFocusOnHideKeyboard(editTextView: SelectableEditText, rootView: View){

        editTextView.clearFocusOnKeyboardHide(rootView)
    }

    fun convertDateToLocalDate(date: Date) : LocalDate {

        return date.toInstant()
            .atZone(ZoneId.systemDefault()) // or specify a zone
            .toLocalDate()
    }
}