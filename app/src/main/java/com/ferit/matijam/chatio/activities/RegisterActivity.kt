package com.ferit.matijam.chatio.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.ferit.matijam.chatio.R
import com.ferit.matijam.chatio.models.User
import com.ferit.matijam.chatio.utils.DatabaseOfflineSupport
import com.ferit.matijam.chatio.utils.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var toLoginTextView: TextView
    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText:EditText
    private lateinit var passwordEditText:EditText
    private lateinit var repeatPasswordEditText:EditText
    private lateinit var registerButton:Button

    companion object{
        const val TAG="RegisterActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.hide()

        toLoginTextView=findViewById(R.id.to_login_text_view_register)
        usernameEditText=findViewById(R.id.username_edit_text_register)
        emailEditText=findViewById(R.id.email_edit_text_register)
        passwordEditText=findViewById(R.id.password_edit_text_register)
        repeatPasswordEditText=findViewById(R.id.repeat_password_edit_text_register)
        registerButton=findViewById(R.id.register_button_register)


        toLoginTextView.setOnClickListener {
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        registerButton.setOnClickListener {
            performRegister()

        }

    }

    private fun performRegister() {

        val username=usernameEditText.text.toString()
        val email=emailEditText.text.toString()
        val password=passwordEditText.text.toString()
        val repeatPassword=repeatPasswordEditText.text.toString()

        val isCompleted=checkIfAllFieldsAreFilled(username,email,password,repeatPassword)
        val isValid=isPasswordValid(password,repeatPassword)

       if(isCompleted==false or isValid==false)
        {
            return
        }
        val loadingDialog=LoadingDialog(this,"Creating user account...")
        loadingDialog.startLoading()

        val auth=FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(it.isSuccessful)
                {
                    loadingDialog.dismiss()
                    Log.d(TAG,"Successfully created user!")
                    saveUserToFirebaseDatabase(username,email)
                }
                else
                {
                    loadingDialog.dismiss()
                    return@addOnCompleteListener
                }
            }
            .addOnFailureListener {
                loadingDialog.dismiss()
                Log.d(TAG,"Failed to create user: ${it.message}")
                Toast.makeText(this,"Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()

            }

    }

    private fun saveUserToFirebaseDatabase(username: String, email: String) {
        val currentUserID=FirebaseAuth.getInstance().currentUser!!.uid

        val ref= DatabaseOfflineSupport.getDatabase()?.getReference("/users/$currentUserID")
        val imageLocation="https://firebasestorage.googleapis.com/v0/b/chatio-3e74b.appspot.com/o/default%20images%2Fdefault_profile_image.png?alt=media&token=16af69c3-30f8-4da4-8deb-2f77f0502d97"
        val queryUsername=username.lowercase()
        val user=User(currentUserID,username,email,imageLocation,queryUsername)

        ref?.setValue(user)
            ?.addOnSuccessListener {
                Log.d(TAG,"Successfully saved user to firebase database!")

                val intent=Intent(this@RegisterActivity, HomeActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            ?.addOnFailureListener {
                Log.d(TAG, "${it.message}")
            }

    }

    private fun isPasswordValid(password: String, repeatPassword: String): Boolean {
        var flag=true
        if(password!=repeatPassword)
        {
            flag=false
            Toast.makeText(this,"Passwords do not match!", Toast.LENGTH_SHORT).show()

        }
        return  flag
    }

    private fun checkIfAllFieldsAreFilled(username: String, email: String, password: String, repeatPassword: String):Boolean{
        var flag=true
        when{
            TextUtils.isEmpty(username) -> {Toast.makeText(this, "Username is required!", Toast.LENGTH_SHORT).show()
                flag=false
            }
            TextUtils.isEmpty(email) -> {
                Toast.makeText(this, "Email is required!", Toast.LENGTH_SHORT).show()
                flag= false
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(this, "Password is required!", Toast.LENGTH_SHORT).show()
                flag= false
            }
            TextUtils.isEmpty(repeatPassword) -> {
                Toast.makeText(this, "Password is required!", Toast.LENGTH_SHORT).show()
                flag= false
            }
            else -> flag=true
        }
        return flag
    }

   override fun onBackPressed() { //animation
        super.onBackPressed()

        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
    }
}
