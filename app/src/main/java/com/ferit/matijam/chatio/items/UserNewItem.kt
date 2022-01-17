package com.ferit.matijam.chatio.items

import android.widget.TextView
import com.bumptech.glide.Glide
import com.ferit.matijam.chatio.R
import com.ferit.matijam.chatio.models.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class UserNewItem(val user: User): Item<GroupieViewHolder>()
{
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.findViewById<TextView>(R.id.user_item_username_newmessage).text=user.username
        val imageViewHolder=viewHolder.itemView.findViewById<CircleImageView>(R.id.user_item_image_newmessage)

        Glide.with(viewHolder.root.context).load(user.imageUrl).into(imageViewHolder)

    }

    override fun getLayout(): Int {

        return R.layout.user_item_new_message
    }

}