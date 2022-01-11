package com.ferit.matijam.chatio.items

import android.widget.TextView
import com.bumptech.glide.Glide
import com.ferit.matijam.chatio.R
import com.ferit.matijam.chatio.models.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class MessageToItem(val text:String?,val user: User): Item<GroupieViewHolder>()
{
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.text_message_to).text=text
        val imageViewHolder=viewHolder.itemView.findViewById<CircleImageView>(R.id.user_image_message_to)
       // Picasso.get().load(user.image).into(imageViewHolder)
        Glide.with(viewHolder.root.context).load(user.imageUrl).into(imageViewHolder)

    }

    override fun getLayout(): Int {

            return R.layout.message_to

    }

}