package com.ferit.matijam.chatio.activities

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.ferit.matijam.chatio.R
import com.ferit.matijam.chatio.items.ImageMessageFromItem
import com.ortiz.touchview.TouchImageView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*

class ImageViewer : AppCompatActivity() {

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        val item = findViewById<TouchImageView>(R.id.touch_image_image_viewer)

        val imageurl = intent.getStringExtra(ImageMessageFromItem.URL)
        Glide.with(this).load(imageurl).into(item)

       // val time = java.util.Calendar.getInstance()
       val time = SimpleDateFormat( "yyyy/MM/dd   HH:mm:ss" ).format( Calendar.getInstance().time)
       // supportActionBar?.title=time

        supportActionBar?.hide()
        val goBack=findViewById<ImageView>(R.id.imageView2)
        val save=findViewById<ImageView>(R.id.imageView3)

        goBack.setOnClickListener {
            finish()
        }

        item.setOnClickListener{
            showHide(goBack)
            showHide(save)
        }

    }
    fun showHide(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE){
            View.INVISIBLE
        } else{
            View.VISIBLE
        }
    }

}