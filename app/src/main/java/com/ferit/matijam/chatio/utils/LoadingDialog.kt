package com.ferit.matijam.chatio.utils

import android.app.Activity
import android.app.AlertDialog
import android.widget.TextView
import com.ferit.matijam.chatio.R

class LoadingDialog(private val activity: Activity,private val text:String) {

    private lateinit var dialog: AlertDialog
    private lateinit var loadingText:TextView

    fun startLoading()
    {
        val inflater=activity.layoutInflater
        val dialogView=inflater.inflate(R.layout.loading_item, null)

        loadingText=dialogView.findViewById(R.id.loading_text_view)
        loadingText.text=text

        val builder=AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog=builder.create()
        dialog.show()
    }
    fun dismiss()
    {
        dialog.dismiss()
    }

}