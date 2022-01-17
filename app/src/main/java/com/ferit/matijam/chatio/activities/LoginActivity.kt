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
import com.google.firebase.auth.FirebaseAuth
import com.ferit.matijam.chatio.utils.LoadingDialog


class LoginActivity : AppCompatActivity() {

    private lateinit var toRegisterTextView: TextView
    private lateinit var loginButton:Button
    private lateinit var emailEditText:EditText
    private lateinit var passwordEditText: EditText

    companion object{
        const val TAG="LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        toRegisterTextView=findViewById(R.id.to_register_text_view_login)
        loginButton=findViewById(R.id.login_button_login)
        emailEditText=findViewById(R.id.email_edit_text_login)
        passwordEditText=findViewById(R.id.password_edit_text_login)

        loginButton.setOnClickListener {

            performLogin()
        }


        toRegisterTextView.setOnClickListener {
            val intent= Intent(this, RegisterActivity::class.java)
            startActivity(intent)

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

    }

    private fun performLogin() {
        val email=emailEditText.text.toString()
        val password=passwordEditText.text.toString()
        val isCompleted=checkIfAllFieldsAreFilled(email,password)
        if(isCompleted==false)
        {
            return
        }
        val loadingDialog= LoadingDialog(this, "Loading profile...")
        loadingDialog.startLoading()

        val auth:FirebaseAuth= FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    loadingDialog.dismiss()
                    Log.d(TAG, "Successfully logged in!")

                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                loadingDialog.dismiss()
                Log.d(TAG,"Failed: ${it.message}")
                Toast.makeText(this,"${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun checkIfAllFieldsAreFilled(email: String, password: String):Boolean {
        var flag=true
        when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(
                    this,
                    "Email is required",
                    Toast.LENGTH_SHORT
                ).show()
                flag=false
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(
                    this,
                    "Password is required",
                    Toast.LENGTH_SHORT

                ).show()
                flag=false
            }
        }

        return flag
    }

    override fun onStart() {
        super.onStart()

        if(FirebaseAuth.getInstance().currentUser != null) // if user is already logged in
        {
            val intent= Intent(this@LoginActivity, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

}