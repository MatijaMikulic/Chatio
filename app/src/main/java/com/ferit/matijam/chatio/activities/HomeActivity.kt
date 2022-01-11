package com.ferit.matijam.chatio.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.ferit.matijam.chatio.fragments.LatestMessagesFragment
import com.ferit.matijam.chatio.fragments.NewMessageFragment
import com.ferit.matijam.chatio.fragments.ProfileFragment
import com.ferit.matijam.chatio.R
import com.ferit.matijam.chatio.models.User
import com.ferit.matijam.chatio.utils.DatabaseOfflineSupport
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {

    private lateinit var navigationView:BottomNavigationView

    companion object{
        var currentUser: User?=null
        const val TAG="HomeActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)



        retrieveCurrentUser()

        navigationView = findViewById(R.id.navigation_view_home)

        moveToFragment(LatestMessagesFragment())
        supportActionBar?.title="Latest Messages"

        navigationView.setOnItemSelectedListener{
            when(it.itemId)
            {
                R.id.nav_latest_messages -> {
                    supportActionBar?.title="Latest Messages"
                    moveToFragment(LatestMessagesFragment())
                    return@setOnItemSelectedListener true
                }
            }
            when(it.itemId)
            {
                R.id.nav_new_message -> {
                    supportActionBar?.title="New Message"
                    moveToFragment(NewMessageFragment())
                    return@setOnItemSelectedListener true
                }
            }
            when(it.itemId)
            {
                R.id.nav_profile -> {
                    supportActionBar?.title="Profile"
                    moveToFragment(ProfileFragment())
                    return@setOnItemSelectedListener true
                }
            }
            false
        }


    }

    private fun moveToFragment(fragment: Fragment) {

        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container_home, fragment)
        fragmentTrans.commit()

    }

    private fun retrieveCurrentUser()
    {
        val currentUserId=FirebaseAuth.getInstance().currentUser?.uid
        val userRef= DatabaseOfflineSupport.getDatabase()
            ?.getReference("/users/$currentUserId")
        userRef?.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser=snapshot.getValue(User::class.java)
                Log.d(TAG,"Current user: ${currentUser?.username}")
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}