package com.curioskyestudios.lumonote.utils.general

import android.view.View

class GeneralUIHelper {

    fun changeViewVisibility(view: View, showView: Boolean) {

        if (showView) {

            view.visibility = View.VISIBLE
        } else {

            view.visibility = View.GONE
        }
    }
}