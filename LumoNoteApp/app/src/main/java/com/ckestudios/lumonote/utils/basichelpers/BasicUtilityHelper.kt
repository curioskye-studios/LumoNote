package com.ckestudios.lumonote.utils.basichelpers

import android.view.View
import com.ckestudios.lumonote.ui.noteview.other.CustomSelectionET
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import kotlin.random.Random

object BasicUtilityHelper {

    fun clearETViewFocusOnHideKeyboard(editTextView: CustomSelectionET, rootView: View){

        editTextView.clearFocusOnKeyboardHide(rootView)
    }

    fun convertDateToLocalDate(date: Date) : LocalDate {

        return date.toInstant()
            .atZone(ZoneId.systemDefault()) // or specify a zone
            .toLocalDate()
    }

    fun generateRandomString(length: Int): String {

        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = Random.Default

        if (length <= 0) return ""

        return (1..length)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }

}