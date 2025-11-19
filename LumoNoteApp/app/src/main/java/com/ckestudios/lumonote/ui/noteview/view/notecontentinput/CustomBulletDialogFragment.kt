package com.ckestudios.lumonote.ui.noteview.view.notecontentinput

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.DialogFragment
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletResource
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class CustomBulletDialogFragment() : DialogFragment() {

    private var userInput = ""

    private var dialog: AlertDialog? = null
    private lateinit var dialogView: View

    private lateinit var inputLayout: TextInputLayout
    private lateinit var inputEditText: TextInputEditText

    private lateinit var cancelButton: Button
    private lateinit var okButton: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        setupDialog()

        setupDialogUI()

        setOnClickListeners()

        return dialog as AlertDialog
    }

    override fun onDestroy() {
        dialog?.dismiss()
        dialog = null
        super.onDestroy()
    }


    private fun setupDialog() {

        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_custom_bullet, null)

        dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
    }

    private fun setupDialogUI() {

        inputEditText = dialogView.findViewById(R.id.textInputET)
        inputLayout = dialogView.findViewById(R.id.textInputLayoutTL)

        cancelButton = dialogView.findViewById(R.id.cancelButtonBN)
        okButton = dialogView.findViewById(R.id.okButtonBN)
    }

    private fun setOnClickListeners() {

        cancelButton.setOnClickListener {

            dialog?.dismiss()
        }

        okButton.setOnClickListener {

            userInput = inputEditText.text.toString().trim()

            if (userInput.isEmpty()) {

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

    }

}



