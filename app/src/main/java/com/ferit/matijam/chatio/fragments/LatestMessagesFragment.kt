package com.ferit.matijam.chatio.fragments

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ferit.matijam.chatio.R
import com.ferit.matijam.chatio.activities.ChatLogActivity
import com.ferit.matijam.chatio.items.LatestMessageItem
import com.ferit.matijam.chatio.models.ChatMessage
import com.ferit.matijam.chatio.utils.DatabaseOfflineSupport
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.w3c.dom.Text

class LatestMessagesFragment : Fragment() {

    companion object{
        const val TAG="LatestMessagesFragment"
    }

    private lateinit var latestMessagesAdapter:GroupAdapter<GroupieViewHolder>
    val latestMessagesMap= LinkedHashMap<String,ChatMessage>()
    private lateinit var recyclerView:RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_latest_messages, container, false)

        latestMessagesAdapter=GroupAdapter<GroupieViewHolder>()
        recyclerView=view.findViewById(R.id.recycler_view_latest_messages)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = latestMessagesAdapter
        }
        //send to chat log activity
        latestMessagesAdapter.setOnItemClickListener { item, view ->
            val row=item as LatestMessageItem

            val user=row.chatPartner
            val intent= Intent(context, ChatLogActivity::class.java)
            intent.putExtra(NewMessageFragment.USER_KEY,user)
            startActivity(intent)
            activity?.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        listenForLatestMessages()

        return view
    }

    private fun listenForLatestMessages() {
        val currentUserId=FirebaseAuth.getInstance().currentUser!!.uid
        val latestMessageRef= DatabaseOfflineSupport.getDatabase()
            ?.getReference("/latest_messages/$currentUserId")
        latestMessageRef?.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage=snapshot.getValue(ChatMessage::class.java)?:return
                latestMessagesMap[snapshot.key!!]=chatMessage
                refreshRecyclerView()

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage=snapshot.getValue(ChatMessage::class.java)?:return
                latestMessagesMap.remove(snapshot.key!!)
                latestMessagesMap[snapshot.key!!]=chatMessage
                refreshRecyclerView()
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,error.toString())
            }
        })
    }

    private fun refreshRecyclerView() {
        latestMessagesAdapter.clear()
        val newList=latestMessagesMap.toList().sortedBy { (key,value)->value.timestamp }
        newList.reversed().forEach {
            latestMessagesAdapter.add(LatestMessageItem(it.second)) //it.second = chatMessage
        }
    }
}



