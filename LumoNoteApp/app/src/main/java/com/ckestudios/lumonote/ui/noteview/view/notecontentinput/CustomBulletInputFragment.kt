package com.ckestudios.lumonote.ui.noteview.view.notecontentinput

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.DialogFragment
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletResource
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class CustomBulletInputFragment() : DialogFragment() {

    private var userInput = ""

    private var dialog: AlertDialog? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_custom_bullet, null)

        dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val inputLayout =
            dialogView.findViewById<TextInputLayout>(R.id.textInputLayoutTL)
        val inputEditText =
            dialogView.findViewById<TextInputEditText>(R.id.textInputET)

        val cancelButton =
            dialogView.findViewById<Button>(R.id.cancelButtonBN)
        val okButton =
            dialogView.findViewById<Button>(R.id.okButtonBN)


        cancelButton.setOnClickListener {

            dialog?.dismiss()
        }

        okButton.setOnClickListener {

            userInput = inputEditText.text.toString().trim()

            if (userInput.isNullOrEmpty()) {

                inputLayout.error = "Bullet cannot be empty"
            }
            else if (userInput.length > 3) {

                inputLayout.error = "Bullet cannot be more than 3 characters"
            }
            else if (userInput.isDigitsOnly()) {

                inputLayout.error = "Bullet cannot be primarily numbers"
            } else {

                inputLayout.error = null

                CustomBulletResource.setCustomBullet(userInput)

                dialog?.dismiss()
            }
        }

        return dialog as AlertDialog
    }

    override fun onDestroy() {
        dialog?.dismiss()
        dialog = null
        super.onDestroy()
    }

}
