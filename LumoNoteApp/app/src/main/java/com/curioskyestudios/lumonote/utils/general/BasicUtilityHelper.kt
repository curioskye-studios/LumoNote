package com.curioskyestudios.lumonote.utils.general

import android.view.View
import com.curioskyestudios.lumonote.ui.noteview.other.SelectableEditText

class BasicUtilityHelper {

    fun clearETViewFocusOnHideKeyboard(editTextViews: Array<SelectableEditText>, rootView: View){

        for (editTextView in editTextViews) {

            editTextView.clearFocusOnKeyboardHide(rootView)
            editTextView.clearFocusOnKeyboardHide(rootView)
        }
    }
}