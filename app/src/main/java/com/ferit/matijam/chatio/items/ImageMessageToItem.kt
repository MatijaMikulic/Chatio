package com.ferit.matijam.chatio.items

import android.content.Context
import android.content.Intent
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ferit.matijam.chatio.R
import com.ferit.matijam.chatio.activities.HomeActivity
import com.ferit.matijam.chatio.activities.ImageViewer
import com.ferit.matijam.chatio.models.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class ImageMessageToItem(val imageUrl:String?, val user: User,val context:Context): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val userProfileImage=viewHolder.itemView.findViewById<CircleImageView>(R.id.user_image_image_to)
        Glide.with(viewHolder.root.context).load(user.imageUrl).into(userProfileImage)

        val imageMessage=viewHolder.itemView.findViewById<ImageView>(R.id.image_message_to)
        Glide.with(viewHolder.root.context).load(imageUrl).into(imageMessage)

        imageMessage.setOnClickListener {
            val intent=Intent(context, ImageViewer::class.java) //for now its main activity
            intent.putExtra(ImageMessageFromItem.URL,imageUrl)
            context.startActivity(intent)
        }


    }

    override fun getLayout(): Int {
        return R.layout.image_to
    }

}