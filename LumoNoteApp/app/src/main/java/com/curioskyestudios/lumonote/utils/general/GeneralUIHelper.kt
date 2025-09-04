package com.curioskyestudios.lumonote.utils.general

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.curioskyestudios.lumonote.R

class GeneralUIHelper {

    fun changeButtonIVColor(context: Context, buttonIV: ImageView, color: Int) {

        buttonIV.imageTintList = ContextCompat.getColorStateList(context, color)
    }

    fun highlightButtonIV(buttonIV: ImageView, context: Context) {

        // highlight button
        changeButtonIVColor(context, buttonIV, R.color.gold)
    }

    fun unhighlightButtonIV(buttonIV: ImageView, context: Context) {

        // unhighlight button
        changeButtonIVColor(context, buttonIV, R.color.light_grey_1)
    }

    fun updateButtonIVHighlight(buttonIV: ImageView, isActive: Boolean, context: Context) {

        if (isActive) {

            highlightButtonIV(buttonIV, context)
        } else {

            unhighlightButtonIV(buttonIV, context)
        }
    }

}