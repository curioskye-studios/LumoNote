package com.curioskyestudios.lumonote.utils.general

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat

class GeneralUIHelper {
    fun changeButtonIVColor(context: Context, buttonIV: ImageView, color: Int) {
        buttonIV.imageTintList = ContextCompat.getColorStateList(context, color)
    }

}