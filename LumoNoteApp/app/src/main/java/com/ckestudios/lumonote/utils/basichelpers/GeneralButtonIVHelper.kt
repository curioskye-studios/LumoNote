package com.ckestudios.lumonote.utils.basichelpers

import android.content.Context
import android.os.Handler
import android.os.Looper
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

    fun changeButtonBackground(context: Context, buttonIV: ImageView, color: Int) {

        buttonIV.setBackgroundColor(ContextCompat.getColor(context, color))
    }

    fun removeButtonBackground(buttonIV: ImageView) {

        buttonIV.background = null
    }

    fun playSelectionIndication(context: Context, buttonIV: ImageView) {

        changeButtonBackground(context, buttonIV, R.color.light_grey_3_selected)

        Handler(Looper.getMainLooper()).postDelayed({

            removeButtonBackground(buttonIV)
        }, 500) // Delay in milliseconds (500ms = 0.3 seconds)
    }


    fun disableButtonIV(buttonIV: ImageView, context: Context) {

        changeButtonIVResTint(context, buttonIV, R.color.light_grey_3)

        removeButtonBackground(buttonIV)

        buttonIV.isEnabled = false
    }

    fun enableButtonIV(buttonIV: ImageView, context: Context, color: Int?) {

        if (color == null) {

            changeButtonIVResTint(context, buttonIV, R.color.light_grey_1)
        } else {

            changeButtonIVResTint(context, buttonIV, color)
        }

        buttonIV.isEnabled = true
    }


    fun highlightButtonIV(buttonIV: ImageView, context: Context) {

        changeButtonIVResTint(context, buttonIV, R.color.gold)

        changeButtonBackground(context, buttonIV, R.color.light_grey_3_selected)
    }

    fun unhighlightButtonIV(buttonIV: ImageView, context: Context, color: Int?) {

        removeButtonBackground(buttonIV)

        if (color == null) {

            changeButtonIVResTint(context, buttonIV, R.color.light_grey_1)
        } else {

            changeButtonIVResTint(context, buttonIV, color)
        }
    }

    fun updateButtonIVHighlight(buttonIV: ImageView, isActive: Boolean, context: Context,
                                defaultColor: Int?) {

        if (!buttonIV.isEnabled) {
            return
        }

        if (isActive) {
            highlightButtonIV(buttonIV, context)
        } else {
            unhighlightButtonIV(buttonIV, context, defaultColor)
        }
    }

    fun updatePinHighlight(pinButtonIV: ImageView, context: Context){

        if (pinButtonIV.tag == true) {

            changeButtonIVResTint(context, pinButtonIV, R.color.gold)

            changeButtonBackground(context, pinButtonIV, R.color.light_grey_3_selected)
        } else {

            changeButtonIVResTint(context, pinButtonIV, R.color.light_grey_3)

            removeButtonBackground(pinButtonIV)
        }
    }

}