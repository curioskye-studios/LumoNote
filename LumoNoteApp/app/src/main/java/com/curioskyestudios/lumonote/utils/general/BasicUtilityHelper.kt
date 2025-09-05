package com.curioskyestudios.lumonote.utils.general

import android.view.View
import com.curioskyestudios.lumonote.ui.noteview.other.SpanningSelectableEditText

class BasicUtilityHelper {

    fun clearETViewFocusOnHideKeyboard(editTextView: SpanningSelectableEditText, rootView: View){

        editTextView.clearFocusOnKeyboardHide(rootView)
    }
}