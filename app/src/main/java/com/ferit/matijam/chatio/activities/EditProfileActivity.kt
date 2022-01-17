package com.ferit.matijam.chatio.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.ferit.matijam.chatio.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.ferit.matijam.chatio.models.User
import com.ferit.matijam.chatio.utils.DatabaseOfflineSupport
import com.ferit.matijam.chatio.utils.DeleteAccountDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseUser


class EditProfileActivity : AppCompatActivity() {

    companion object{
        const val TAG="EditProfileActivity"
    }
    private lateinit var saveImageView:ImageView
    private lateinit var profileImage: CircleImageView
    private lateinit var editUsernameEditText: EditText
    private var user:User?=null
    private var imageUri: Uri?=null
    private var storageProfilePictureReference: StorageReference?=null
    private lateinit var changeImageTextView: TextView
    private var url=""
    private lateinit var closeImageView: ImageView
    private lateinit var deleteButton: Button

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
             imageUri = result.uriContent
            profileImage.setImageURI(imageUri)
        } else {
            val exception = result.error
            Log.d(TAG,exception.toString())
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        supportActionBar?.hide()

        editUsernameEditText = findViewById(R.id.username_edit_text_edit_profile)
        profileImage = findViewById(R.id.user_image_edit_profile)
        saveImageView=findViewById(R.id.save_image_view_edit_profile)
        changeImageTextView=findViewById(R.id.change_image_text_view_edit_profile)
        storageProfilePictureReference=FirebaseStorage.getInstance().getReference("Profile Pictures")
        closeImageView=findViewById(R.id.close_image_view_edit_profile)
        deleteButton=findViewById(R.id.delete_accout_button_edit_profile)

        closeImageView.setOnClickListener {
            val intent=Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        changeImageTextView.setOnClickListener {
            startCrop()
        }
        profileImage.setOnClickListener {
            startCrop()
        }


        saveImageView.setOnClickListener {
            updateUserInfo()
        }

        deleteButton.setOnClickListener {
            authenticate()
        }

        setUserInfo()

    }
    private fun startCrop() {
        // start picker to get image for cropping and then use the image in cropping activity
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
            }
        )
    }

    private fun  authenticate() {
       val user = Firebase.auth.currentUser!!

        val deleteDialog=DeleteAccountDialog(this)
        deleteDialog.startLoading()


        deleteDialog.dismissButton.setOnClickListener {
            deleteDialog.dismiss()
        }

        deleteDialog.confirmButton.setOnClickListener {
            val password=deleteDialog.getPasswordText()
            val credentials = EmailAuthProvider.getCredential(user.email.toString(), password)

            user.reauthenticate(credentials)
                .addOnCompleteListener {
                    deleteDialog.dismiss()
                    Log.d("TAG", "User re-authenticated.")
                    deleteAccount(user)
                }
                .addOnFailureListener{
                    Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun deleteAccount(user: FirebaseUser) {
        val currentUserId=FirebaseAuth.getInstance().currentUser?.uid ?:return
        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    deleteUserInfo(currentUserId)
                    Log.d(TAG, "User account deleted.")
                    val intent=Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                }
            }
    }

    private fun deleteUserInfo(currentUserId: String) {


        val ref= DatabaseOfflineSupport.getDatabase()?.getReference("/users")
        val userMap=HashMap<String,Any?>()
        userMap["email"]=null
        userMap["imageUrl"]="https://firebasestorage.googleapis.com/v0/b/chatio-3e74b.appspot.com/o/default%20images%2Fdefault_profile_image.png?alt=media&token=16af69c3-30f8-4da4-8deb-2f77f0502d97"
        userMap["queryUsername"]=null
        userMap["username"]="Deleted User"

        ref?.child(currentUserId)?.updateChildren(userMap)
    }

    private fun updateUserInfo() {
        when{
            editUsernameEditText.text.toString()=="" -> {
                Toast.makeText(this,"Please enter username", Toast.LENGTH_SHORT).show()
            }
            imageUri==null ->{
                updateUsernameOnly()
            }
            else -> updateUserImageAndInfo()
        }
    }

    private fun updateUserImageAndInfo() {
        val currentUserId=FirebaseAuth.getInstance().currentUser!!.uid
        val fileRef=storageProfilePictureReference!!.child(currentUserId)

        val uploadTask:StorageTask<*>
        uploadTask=fileRef.putFile(imageUri!!)
        uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>>(){ task ->
            if(!task.isSuccessful)
            {
                task.exception?.let{
                    throw it
                }
            }

            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
            if (task.isSuccessful){
                val downloadUri=task.result
                url=downloadUri.toString()

                val ref= DatabaseOfflineSupport.getDatabase()?.getReference("/users")
                val userMap=HashMap<String,Any>()
                userMap["imageUrl"]=url
                userMap["username"]=editUsernameEditText.text.toString()
                userMap["username"]=editUsernameEditText.text.toString().lowercase()

                ref?.child(currentUserId)?.updateChildren(userMap)
                val intent= Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }

    private fun updateUsernameOnly() {
        when{
            editUsernameEditText.text.toString()=="" -> {
                Toast.makeText(this,"Please enter username", Toast.LENGTH_LONG).show()
            }
            else ->{
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                val userRef=DatabaseOfflineSupport.getDatabase()
                    ?.getReference("/users/$currentUserId")
                val userMap=HashMap<String,Any>()
                userMap["username"]=editUsernameEditText.text.toString()
                userMap["queryUsername"]=editUsernameEditText.text.toString().lowercase()

                userRef?.updateChildren(userMap)

                val intent= Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setUserInfo() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val userRef =
            DatabaseOfflineSupport.getDatabase()
                ?.getReference("/users/$currentUserId")

        userRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User::class.java)
                    Glide.with(this@EditProfileActivity)
                        .load(user?.imageUrl)
                        .into(profileImage)
                    editUsernameEditText.setText(user?.username)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", error.message)
            }
        })
    }
}
