package com.curioskyestudios.lumonote.ui.noteview.other

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.curioskyestudios.lumonote.utils.edittexthelper.TextBulletHelper
import com.curioskyestudios.lumonote.utils.edittexthelper.TextSizeHelper
import com.curioskyestudios.lumonote.utils.edittexthelper.TextSpanChecker
import com.curioskyestudios.lumonote.utils.edittexthelper.TextStyleHelper

class SpanningSelectableEditText(context: Context, attrs: AttributeSet?)
    : AppCompatEditText(context, attrs) {

    private val textStyleHelper = TextStyleHelper(this)
    private val textSizeHelper = TextSizeHelper(this)
    private val textBulletHelper = TextBulletHelper(this)
    private val textSpanChecker = TextSpanChecker(this)

    var onSelectionChange: ((Int, Int) -> Unit)? = null

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {

        super.onSelectionChanged(selStart, selEnd)
        onSelectionChange?.invoke(selStart, selEnd)
    }

    fun getStyleHelper() : TextStyleHelper {

        return textStyleHelper
    }

    fun getSizeHelper() : TextSizeHelper {

        return textSizeHelper
    }

    fun getBulletHelper() : TextBulletHelper {

        return textBulletHelper
    }

    fun getSpanChecker() : TextSpanChecker {

        return textSpanChecker
    }

    fun clearFocusOnKeyboardHide(rootView: View) {

        // Variable to track the previous state of the keyboard
        var wasKeyboardVisible = false

        // Add a listener to detect layout changes (including when the keyboard appears/disappears)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {

            // rect will hold the visible area of the window
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)

            // The total height of the root view
            val screenHeight = rootView.rootView.height

            // Calculate how much of the screen is obscured (usually by the keyboard)
            val keypadHeight = screenHeight - rect.bottom

            // Determine if the keyboard is visible
            // 0.15 threshold means if more than 15% of screen is covered, consider the keyboard visible
            val isKeyboardVisible = keypadHeight > screenHeight * 0.15

            // Only run this block if the keyboard **was visible before and now is hidden**
            // This prevents clearing focus while the keyboard is just opening
            if (wasKeyboardVisible && !isKeyboardVisible) {

                // Post the action to the message queue so it runs **after current focus events finish**
                // This avoids interfering with the cursor showing when user taps the EditText
                this.post {
                    // Double-check the EditText still has focus before clearing it
                    if (this.hasFocus()) {
                        this.clearFocus()
                    }
                }
            }

            // Update the previous keyboard state for the next layout change
            wasKeyboardVisible = isKeyboardVisible
        }
    }

}
