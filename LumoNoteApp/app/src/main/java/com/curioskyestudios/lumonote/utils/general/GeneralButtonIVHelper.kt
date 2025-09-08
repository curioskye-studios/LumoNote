package com.curioskyestudios.lumonote.utils.general

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.curioskyestudios.lumonote.R

class GeneralButtonIVHelper {

    fun getResourceDrawable(context: Context, drawable: Int, customColor: Int): Drawable? {

        val retrievedDrawable = ContextCompat.getDrawable(context, drawable)

        // Wrap and mutate so it won't affect other uses of the same drawable
        val tintedDrawable = retrievedDrawable?.mutate()

        if (tintedDrawable != null) {
            DrawableCompat.setTint(tintedDrawable, ContextCompat.getColor(context, customColor))
        }

        return tintedDrawable
    }

    fun changeButtonIVCustomColor(context: Context, buttonIV: ImageView, color: Int) {

        buttonIV.imageTintList = ContextCompat.getColorStateList(context, color)
    }


    fun disableButtonIV(buttonIV: ImageView, context: Context) {

        changeButtonIVCustomColor(context, buttonIV, R.color.light_grey_3)

        buttonIV.isEnabled = false
    }

    fun enableButtonIV(buttonIV: ImageView, context: Context) {

        changeButtonIVCustomColor(context, buttonIV, R.color.light_grey_1)

        buttonIV.isEnabled = true
    }


    fun highlightButtonIV(buttonIV: ImageView, context: Context) {

        changeButtonIVCustomColor(context, buttonIV, R.color.gold)
    }

    fun unhighlightButtonIV(buttonIV: ImageView, context: Context) {

        changeButtonIVCustomColor(context, buttonIV, R.color.light_grey_1)
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

            changeButtonIVCustomColor(context, pinButtonIV, R.color.gold)
        } else {

            changeButtonIVCustomColor(context, pinButtonIV, R.color.light_grey_3)
        }
    }


    fun displayFeedbackToast(context: Context, message: String, longDisplayPeriod: Boolean) {

        if (longDisplayPeriod) {

            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } else {

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

    }

    fun closeActivityWithFeedback(feedback: String, context: Context, activity: Activity) {

        // Closes view note activity, pops from activity stack, returns to main below it
        activity.finish()

        displayFeedbackToast(context, feedback, true)
    }

}