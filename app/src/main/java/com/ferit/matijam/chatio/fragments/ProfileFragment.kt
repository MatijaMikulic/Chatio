package com.ferit.matijam.chatio.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.bumptech.glide.Glide
import com.ferit.matijam.chatio.R
import com.ferit.matijam.chatio.activities.EditProfileActivity
import com.ferit.matijam.chatio.activities.LoginActivity
import com.ferit.matijam.chatio.models.User
import com.ferit.matijam.chatio.utils.DatabaseOfflineSupport
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    companion object{
        const val TAG="ProfileFragment"
    }

    private lateinit var logoutTextView:TextView
    private lateinit var editProfileButton: Button
    private lateinit var profileImage:CircleImageView
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        logoutTextView=view.findViewById(R.id.logout_text_view_profile)
        editProfileButton=view.findViewById(R.id.edit_button_profile)
        profileImage=view.findViewById(R.id.user_image_profile)
        usernameTextView=view.findViewById(R.id.username_text_view_profile)
        emailTextView=view.findViewById(R.id.email_text_view_profile)


        logoutTextView.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent= Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            activity?.finish()
            activity?.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        }

        editProfileButton.setOnClickListener {
            val intent=Intent(context,EditProfileActivity::class.java)
            startActivity(intent)
            activity?.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        setUserInfo()

        return view
    }

    private fun setUserInfo() {
        val currentUserId=FirebaseAuth.getInstance().currentUser?.uid
        val userRef= DatabaseOfflineSupport.getDatabase()
            ?.getReference("/users/$currentUserId")

        userRef?.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val user=snapshot.getValue(User::class.java)
                    Glide.with(context!!)
                        .load(user?.imageUrl)
                        .into(profileImage)
                    usernameTextView.text=user?.username
                    emailTextView.text=user?.email
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, error.message)
            }
        })
    }
}