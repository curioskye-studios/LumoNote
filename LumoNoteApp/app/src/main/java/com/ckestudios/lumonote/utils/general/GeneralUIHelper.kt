package com.ckestudios.lumonote.utils.general

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
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
}