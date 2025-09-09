package com.ckestudios.lumonote.utils.general

import android.view.View
import com.ckestudios.lumonote.ui.noteview.other.SelectableEditText

class BasicUtilityHelper {

    fun clearETViewFocusOnHideKeyboard(editTextView: SelectableEditText, rootView: View){

        editTextView.clearFocusOnKeyboardHide(rootView)
    }
}