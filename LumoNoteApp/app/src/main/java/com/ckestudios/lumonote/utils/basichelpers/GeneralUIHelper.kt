package com.ckestudios.lumonote.utils.basichelpers

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

object GeneralUIHelper {

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

    fun openKeyboardForView(window: Window, view: View) {

        // Get a controller that can manage system UI insets (keyboard, status bar, nav bar)
        // for the current window, using 'editText' as the reference view.
        val insetsController =
            WindowCompat.getInsetsController(window, view)

        // If the controller exists, request the system to show the IME (soft keyboard).
        // 'WindowInsetsCompat.Type.ime()' specifically represents the on-screen keyboard.
        insetsController?.show(WindowInsetsCompat.Type.ime())

    }


    fun displayFeedbackToast(context: Context, message: String, longDisplayPeriod: Boolean) {

        if (longDisplayPeriod) {

            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } else {

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

    }

    fun closeActivityWithFeedback(feedback: String, context: Context, activity: Activity,
                                  longDisplayPeriod: Boolean) {

        // Closes view note activity, pops from activity stack, returns to main below it
        activity.finish()

        displayFeedbackToast(context, feedback, longDisplayPeriod)
    }

}