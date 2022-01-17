package com.ferit.matijam.chatio.utils

import android.app.Activity
import android.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import com.ferit.matijam.chatio.R
import com.google.firebase.auth.FirebaseUser

class DeleteAccountDialog(private val activity: Activity) {

    private lateinit var dialog: AlertDialog
    private lateinit var editPasswordEditText: EditText
    lateinit var confirmButton: Button
    lateinit var dismissButton: Button
    fun startLoading()
    {
        val inflater=activity.layoutInflater
        val dialogView=inflater.inflate(R.layout.delete_dialog, null)

        editPasswordEditText=dialogView.findViewById(R.id.edit_password_edit_text_dialog)
        confirmButton=dialogView.findViewById(R.id.confirm_button_dialog)
        dismissButton=dialogView.findViewById(R.id.dismiss_button_dialog)

        val builder= AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog=builder.create()
        dialog.show()
    }

    fun getPasswordText():String{
        return editPasswordEditText.text.toString();
    }
    fun dismiss()
    {
        dialog.dismiss()
    }

}