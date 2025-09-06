package com.curioskyestudios.lumonote.utils.general

import android.view.View
import com.curioskyestudios.lumonote.ui.noteview.other.SelectableEditText

class BasicUtilityHelper {

    fun clearETViewFocusOnHideKeyboard(editTextView: SelectableEditText, rootView: View){

        editTextView.clearFocusOnKeyboardHide(rootView)
    }
}