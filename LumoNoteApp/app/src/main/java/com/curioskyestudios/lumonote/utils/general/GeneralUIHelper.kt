package com.curioskyestudios.lumonote.utils.general

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import android.widget.Toast
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

    fun updatePinHighlight(pinButtonIV: ImageView, context: Context){

        if (pinButtonIV.tag == true) {

            changeButtonIVColor(context, pinButtonIV, R.color.gold)
        } else {

            changeButtonIVColor(context, pinButtonIV, R.color.light_grey_3)
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