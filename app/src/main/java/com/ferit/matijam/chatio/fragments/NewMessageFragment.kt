package com.ferit.matijam.chatio.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ferit.matijam.chatio.R
import com.ferit.matijam.chatio.activities.ChatLogActivity
import com.ferit.matijam.chatio.items.UserNewItem
import com.ferit.matijam.chatio.models.User
import com.ferit.matijam.chatio.utils.DatabaseOfflineSupport
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder


class NewMessageFragment : Fragment() {

    companion object{
        const val TAG="NewMessageFragment"
        const val USER_KEY="USER_KEY"
    }

    private lateinit var  recyclerView: RecyclerView
    private lateinit var searchEditText:EditText
    private var userAdapter=GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_message, container, false)

        recyclerView=view.findViewById(R.id.recycler_view_new_message)
        searchEditText=view.findViewById(R.id.search_user_edit_text_new_message)
        recyclerView.apply {
            layoutManager=LinearLayoutManager(context)
            adapter=userAdapter
        }
        userAdapter.setOnItemClickListener { item, view ->
            val userItemNew=item as UserNewItem
            val intent= Intent(view.context,ChatLogActivity::class.java)
            intent.putExtra(USER_KEY, userItemNew.user)
            startActivity(intent)
            activity?.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        searchEditText.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(searchEditText.text.toString()!="")
                {
                    searchUsers(s.toString().lowercase())
                }
                else{
                    userAdapter.clear()
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        return view
    }
    private fun searchUsers(input: String) {
        val currentUserId=FirebaseAuth.getInstance().currentUser!!.uid
        val query= DatabaseOfflineSupport.getDatabase()
            ?.getReference("/users")?.orderByChild("queryUsername")?.startAt(input)?.endAt(input+"\uf0ff")
        query?.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userAdapter.clear()
                snapshot.children.forEach {
                    val user=it.getValue(User::class.java)
                    if(user!=null)
                    {
                        if(user.uid!=currentUserId) {
                            userAdapter.add(UserNewItem(user))
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, error.message)
            }
        })
    }
}

