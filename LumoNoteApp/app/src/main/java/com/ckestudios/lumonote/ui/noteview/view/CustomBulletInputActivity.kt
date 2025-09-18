package com.ckestudios.lumonote.ui.noteview.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.databinding.ActivityCustomBulletBinding
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletResource
import com.ckestudios.lumonote.ui.sharedviewmodel.NoteAppSharedViewModel
import com.ckestudios.lumonote.utils.helpers.BasicUtilityHelper
import com.ckestudios.lumonote.utils.helpers.GeneralButtonIVHelper
import com.ckestudios.lumonote.utils.helpers.GeneralTextHelper
import com.ckestudios.lumonote.utils.helpers.GeneralUIHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


class CustomBulletInputActivity() : AppCompatActivity() {

    private lateinit var customBulletBinding: ActivityCustomBulletBinding

    private val generalTextHelper: GeneralTextHelper = GeneralTextHelper()
    private val generalButtonIVHelper: GeneralButtonIVHelper = GeneralButtonIVHelper()
    private val basicUtilityHelper = BasicUtilityHelper()
    private val generalUIHelper = GeneralUIHelper()

    private lateinit var noteAppSharedViewModel: NoteAppSharedViewModel

    private var userInput = ""


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        customBulletBinding = ActivityCustomBulletBinding.inflate(layoutInflater)
        setContentView(customBulletBinding.root)

        showAlertDialog()
    }

    private fun showAlertDialog() {

        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_custom_bullet, null)

        val dialog = AlertDialog.Builder(this)
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

            dialog.dismiss()
            finish()
        }

        okButton.setOnClickListener {

            userInput = inputEditText.text.toString().trim()

            if (userInput.isNullOrEmpty()) {

                inputLayout.error = "Bullet cannot be empty"
            } else if (userInput.isDigitsOnly()) {

                inputLayout.error = "Bullet cannot be primarily numbers"
            } else {

                inputLayout.error = null

                CustomBulletResource.setCustomBullet(userInput)

                dialog.dismiss()
                finish()
            }
        }

        dialog.show()
    }


}