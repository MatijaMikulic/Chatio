package com.ferit.matijam.chatio.items


import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.ferit.matijam.chatio.R
import com.ferit.matijam.chatio.activities.HomeActivity
import com.ferit.matijam.chatio.activities.ImageViewer
import com.ferit.matijam.chatio.models.ChatMessage
import com.ferit.matijam.chatio.models.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class ImageMessageFromItem(val chatMessage: ChatMessage, val user: User,val context: Context): Item<GroupieViewHolder>() {
    companion object{
        const val URL="url"
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val userProfileImage=viewHolder.itemView.findViewById<CircleImageView>(R.id.user_image_image_from)

        Glide.with(viewHolder.root.context).load(user.imageUrl).into(userProfileImage)

        val imageMessage=viewHolder.itemView.findViewById<ImageView>(R.id.image_message_from)
        Glide.with(viewHolder.root.context).load(chatMessage.imageUrl).into(imageMessage)

        val timeTextView=viewHolder.itemView.findViewById<TextView>(R.id.time_image_from)
        timeTextView.text=chatMessage.localTime

        imageMessage.setOnClickListener {
            val intent=Intent(context,ImageViewer::class.java)
            intent.putExtra(URL,chatMessage.imageUrl)
            context.startActivity(intent)
        }


    }

    override fun getLayout(): Int {

        return R.layout.image_from
    }

}