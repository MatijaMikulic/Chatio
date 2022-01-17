package com.ferit.matijam.chatio.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ferit.matijam.chatio.R
import com.ferit.matijam.chatio.items.ImageMessageFromItem
import com.ortiz.touchview.TouchImageView
import java.util.*

class ImageViewer : AppCompatActivity() {

    private lateinit var itemImageView: TouchImageView
    private lateinit var goBackImageView: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        supportActionBar?.hide()

        itemImageView= findViewById(R.id.touch_image_image_viewer)

        val imageurl = intent.getStringExtra(ImageMessageFromItem.URL)
        Glide.with(this).load(imageurl).into(itemImageView)

        goBackImageView=findViewById(R.id.goback_image_view_image_viewer)

        goBackImageView.setOnClickListener {
            finish()
        }

        itemImageView.setOnClickListener{
            showHide(goBackImageView)

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