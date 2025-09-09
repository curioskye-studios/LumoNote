package com.ckestudios.lumonote.utils.general

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat

class GeneralUIHelper {

    fun getResourceDrawable(context: Context, drawable: Int, customColor: Int): Drawable? {

        val retrievedDrawable = ContextCompat.getDrawable(context, drawable)

        // Wrap and mutate so it won't affect other uses of the same drawable
        val tintedDrawable = retrievedDrawable?.mutate()

        if (tintedDrawable != null) {
            DrawableCompat.setTint(tintedDrawable, ContextCompat.getColor(context, customColor))
        }

        return tintedDrawable
    }

    fun changeViewVisibility(view: View, showView: Boolean) {

        if (showView) {

            view.visibility = View.VISIBLE
        } else {

            view.visibility = View.GONE
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