package com.ferit.matijam.chatio.items

import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.ferit.matijam.chatio.R
import com.ferit.matijam.chatio.models.ChatMessage
import com.ferit.matijam.chatio.models.User
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class MessageToItem(private val chatMessage: ChatMessage,val user: User): Item<GroupieViewHolder>()
{

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val textMessageTextView=viewHolder.itemView.findViewById<TextView>(R.id.text_message_to)
        val timeTextView=viewHolder.itemView.findViewById<TextView>(R.id.time_message_to)
        textMessageTextView.text=chatMessage.text
        timeTextView.text=chatMessage.localTime
        val imageViewHolder=viewHolder.itemView.findViewById<CircleImageView>(R.id.user_image_message_to)
        Glide.with(viewHolder.root.context).load(user.imageUrl).into(imageViewHolder)

        textMessageTextView.setOnClickListener {
            showHide(timeTextView)
        }
    }
    override fun getLayout(): Int {
            return R.layout.message_to
    }

    private fun showHide(view: View) {
        view.visibility = if (view.visibility == View.VISIBLE){
            View.GONE
        } else{
            View.VISIBLE
        }
    }
}