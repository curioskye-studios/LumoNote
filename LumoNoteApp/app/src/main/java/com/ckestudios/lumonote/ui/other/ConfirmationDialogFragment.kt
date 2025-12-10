package com.ckestudios.lumonote.ui.other

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModel
import com.ckestudios.lumonote.R
import com.ckestudios.lumonote.ui.sharedviewmodel.NoteAppSharedViewModel
import com.ckestudios.lumonote.ui.sharedviewmodel.TagAppSharedViewModel


class ConfirmationDialogFragment(private val viewSharedModel: ViewModel,
                                 private val confirmMessage: String,
                                 private val confirmButtonText: String) : DialogFragment() {

    private var dialog: AlertDialog? = null
    private lateinit var dialogView: View

    private lateinit var dialogTitle: TextView
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

        dialogView = layoutInflater.inflate(R.layout.dialog_custom_confirm, null)

        dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
    }

    private fun setupDialogUI() {

        dialogTitle = dialogView.findViewById(R.id.dialogTitleTV)
        cancelButton = dialogView.findViewById(R.id.cancelButtonBN)
        okButton = dialogView.findViewById(R.id.okButtonBN)

        dialogTitle.text = confirmMessage
        okButton.text = confirmButtonText
    }

    private fun setOnClickListeners() {

        cancelButton.setOnClickListener {

            dialog?.dismiss()
        }

        okButton.setOnClickListener {

            if (viewSharedModel is NoteAppSharedViewModel) {

                viewSharedModel.setDialogConfirmStatus(true)
            } else if (viewSharedModel is TagAppSharedViewModel) {

                viewSharedModel.setDeleteTagConfirmed(true)
            }

            dialog?.dismiss()
        }
    }

}
