package com.ckestudios.lumonote.utils.basichelpers

import android.view.View
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

object BasicUtilityHelper {

    fun clearETViewFocusOnHideKeyboard(editTextView: CustomSelectionET, rootView: View) {

        editTextView.clearFocusOnKeyboardHide(rootView)
    }

    fun convertDateToLocalDate(date: Date) : LocalDate {

        return date.toInstant()
            .atZone(ZoneId.systemDefault()) // or specify a zone
            .toLocalDate()
    }

    fun pairConsecutiveListItems(list: List<*>) : List<Pair<*, *>>  {

        return list.chunked(2).map {
            it[0] to it.getOrNull(1)
        }
    }

}