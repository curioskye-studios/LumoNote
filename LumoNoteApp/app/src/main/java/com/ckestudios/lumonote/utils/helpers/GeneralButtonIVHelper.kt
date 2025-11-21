package com.ckestudios.lumonote.utils.helpers

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.ckestudios.lumonote.R

object GeneralButtonIVHelper {

    fun changeButtonIVResBackground(context: Context, buttonIV: ImageView, color: Int) {

        buttonIV.setBackgroundColor(ContextCompat.getColor(context, color))
    }
    fun changeButtonIVImage(buttonIV: ImageView, drawable: Int) {

        buttonIV.setImageResource(drawable)
    }

    fun changeButtonIVResTint(context: Context, buttonIV: ImageView, color: Int) {

        buttonIV.imageTintList = ContextCompat.getColorStateList(context, color)
    }

    fun changeBtnBackgroundColor(context: Context, buttonIV: ImageView, color: Int) {

        buttonIV.setBackgroundColor(ContextCompat.getColor(context, color))
    }

    fun changeBtnBackgroundRes(context: Context, buttonIV: ImageView, drawable: Int,
                               customColor: Int?) {

        buttonIV.background = GeneralUIHelper.getResourceDrawable(context, drawable,
                customColor)
    }

    fun removeButtonBackground(buttonIV: ImageView) {

        buttonIV.background = null
    }

    fun playSelectionIndication(context: Context, buttonIV: ImageView) {

        indicateAsSelection(context, buttonIV)
    }

    fun indicateAsSelection(context: Context, buttonIV: ImageView) {

        Handler(Looper.getMainLooper()).postDelayed({

        removeAsSelection(buttonIV)
    }, 500) // Delay in milliseconds (500ms = 0.5 seconds)

        changeBtnBackgroundRes(context, buttonIV, R.drawable.selected_background,
            R.color.light_grey_2)
    }
    fun removeAsSelection(buttonIV: ImageView) {

        removeButtonBackground(buttonIV)
    }


    fun playSelectionIndicationTint(buttonIV: ImageView, tint: Int) {

        indicateAsSelectionTint(buttonIV, tint)
    }

    fun indicateAsSelectionTint(buttonIV: ImageView, tint: Int) {

        Handler(Looper.getMainLooper()).postDelayed({

            GeneralUIHelper.removeViewBackgroundTint(buttonIV)
        }, 500) // Delay in milliseconds (500ms = 0.5 seconds)

        GeneralUIHelper.changeViewBackgroundTint(buttonIV, tint)
    }


    fun playSelectionIndicationRes(context: Context, buttonIV: ImageView, defaultDrawable: Int,
                                   selectedDrawable: Int) {

        indicateAsSelectionRes(context, buttonIV, defaultDrawable, selectedDrawable)
    }

    fun indicateAsSelectionRes(context: Context, buttonIV: ImageView, defaultDrawable: Int,
                               selectedDrawable: Int) {

        Handler(Looper.getMainLooper()).postDelayed({

            changeBtnBackgroundRes(context, buttonIV, defaultDrawable, null)
        }, 500) // Delay in milliseconds (500ms = 0.5 seconds)

        changeBtnBackgroundRes(context, buttonIV, selectedDrawable, null)
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


    fun highlightButtonIV(buttonIV: ImageView, context: Context, drawable: Int) {

        changeButtonIVResTint(context, buttonIV, R.color.gold)

        changeBtnBackgroundRes(context, buttonIV, drawable, R.color.light_grey_2)

//        changeBtnBackgroundColor(context, buttonIV, R.color.light_grey_3_selected)
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
                                defaultColor: Int?, drawable: Int) {

        if (!buttonIV.isEnabled) {
            return
        }

        if (isActive) {
            highlightButtonIV(buttonIV, context, drawable)
        } else {
            unhighlightButtonIV(buttonIV, context, defaultColor)
        }
    }

    fun updatePinHighlight(pinButtonIV: ImageView, context: Context, drawable: Int){

        if (pinButtonIV.tag == true) {

            changeButtonIVResTint(context, pinButtonIV, R.color.gold)

            changeBtnBackgroundRes(context, pinButtonIV, drawable, R.color.light_grey_2)

        } else {

            changeButtonIVResTint(context, pinButtonIV, R.color.light_grey_3)

            removeButtonBackground(pinButtonIV)
        }
    }

}