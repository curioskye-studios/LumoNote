package com.ckestudios.lumonote.utils.helpers

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.ckestudios.lumonote.R

class GeneralButtonIVHelper {

    fun changeButtonIVResBackground(context: Context, buttonIV: ImageView, color: Int) {

        buttonIV.setBackgroundColor(ContextCompat.getColor(context, color))
    }
    fun changeButtonIVImage(buttonIV: ImageView, drawable: Int) {

        buttonIV.setImageResource(drawable)
    }

    fun changeButtonIVResTint(context: Context, buttonIV: ImageView, color: Int) {

        buttonIV.imageTintList = ContextCompat.getColorStateList(context, color)
    }


    fun disableButtonIV(buttonIV: ImageView, context: Context) {

        changeButtonIVResTint(context, buttonIV, R.color.light_grey_3)

        buttonIV.isEnabled = false
    }

    fun enableButtonIV(buttonIV: ImageView, context: Context) {

        changeButtonIVResTint(context, buttonIV, R.color.light_grey_1)

        buttonIV.isEnabled = true
    }


    fun highlightButtonIV(buttonIV: ImageView, context: Context) {

        changeButtonIVResTint(context, buttonIV, R.color.gold)
    }

    fun unhighlightButtonIV(buttonIV: ImageView, context: Context) {

        changeButtonIVResTint(context, buttonIV, R.color.light_grey_1)
    }

    fun updateButtonIVHighlight(buttonIV: ImageView, isActive: Boolean, context: Context) {

        if (!buttonIV.isEnabled) {
            return
        }

        if (isActive) {
            highlightButtonIV(buttonIV, context)
        } else {
            unhighlightButtonIV(buttonIV, context)
        }
    }

    fun updatePinHighlight(pinButtonIV: ImageView, context: Context){

        if (pinButtonIV.tag == true) {

            changeButtonIVResTint(context, pinButtonIV, R.color.gold)
        } else {

            changeButtonIVResTint(context, pinButtonIV, R.color.light_grey_3)
        }
    }

}