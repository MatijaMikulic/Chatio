package com.ferit.matijam.chatio.items

import android.graphics.Typeface
import android.text.Layout
import android.util.Log
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.ferit.matijam.chatio.R
import com.ferit.matijam.chatio.fragments.LatestMessagesFragment
import com.ferit.matijam.chatio.models.ChatMessage
import com.ferit.matijam.chatio.models.User
import com.ferit.matijam.chatio.utils.DatabaseOfflineSupport
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import de.hdodenhof.circleimageview.CircleImageView

class LatestMessageItem(val chatMessage: ChatMessage):Item<GroupieViewHolder>() {

    var chatPartner: User?=null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val chatPartnerId:String?
        if(chatMessage.fromUserId==FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toUserId
        }else{
            chatPartnerId=chatMessage.fromUserId
        }
        val ref= DatabaseOfflineSupport.getDatabase()
            ?.getReference("/users/$chatPartnerId")
        ref?.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartner=snapshot.getValue(User::class.java)
                val userProfileImage=viewHolder.itemView.findViewById<CircleImageView>(R.id.user_item_image_latestmessages)
                Glide.with(viewHolder.root.context).load(chatPartner?.imageUrl).into(userProfileImage)

                val userUsernameTextView=viewHolder.itemView.findViewById<TextView>(R.id.user_item_username_latestmessages)
                userUsernameTextView.text=chatPartner?.username

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("LatestMessageItem",error.toString())
            }
        })
        val latestMessageTextView=viewHolder.itemView.findViewById<TextView>(R.id.user_item_last_message_latestmessages)
        val timeTextView=viewHolder.itemView.findViewById<TextView>(R.id.time_latestmessage)
        latestMessageTextView.text=chatMessage.text
        timeTextView.text=chatMessage.localTime?.substring(startIndex =13)


    }


    override fun getLayout(): Int {
        return R.layout.user_item_latest_messages
    }


}