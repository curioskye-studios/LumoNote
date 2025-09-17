package com.ckestudios.lumonote.utils.helpers

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.ckestudios.lumonote.R

class GeneralButtonIVHelper {

    fun changeButtonIVResBackground(context: Context, buttonIV: ImageView, color: Int) {

        buttonIV.setBackgroundColor(ContextCompat.getColor(context, color))
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
        //changeButtonIVResBackground(context, buttonIV, R.color.black)
    }

    fun unhighlightButtonIV(buttonIV: ImageView, context: Context) {

        changeButtonIVResTint(context, buttonIV, R.color.light_grey_1)
        //buttonIV.setBackgroundResource(0)
    }

    fun updateButtonIVHighlight(buttonIV: ImageView, isActive: Boolean, context: Context) {

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