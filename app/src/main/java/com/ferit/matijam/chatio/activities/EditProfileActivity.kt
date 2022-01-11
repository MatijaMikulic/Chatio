package com.ferit.matijam.chatio.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.ferit.matijam.chatio.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.ferit.matijam.chatio.models.User
import com.ferit.matijam.chatio.utils.DatabaseOfflineSupport
import com.ferit.matijam.chatio.utils.LoadingDialog
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView

class EditProfileActivity : AppCompatActivity() {


    private lateinit var saveImageView:ImageView
    private lateinit var profileImage: CircleImageView
    private lateinit var editUsernameEditText: EditText
    private var user:User?=null
    private var imageUri: Uri?=null
    private var storageProfilePictureReference: StorageReference?=null
    private lateinit var changeImageTextView: TextView
    private var url=""
    private lateinit var closeImageView: ImageView

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
             imageUri = result.uriContent
            profileImage.setImageURI(imageUri)
        } else {
            val exception = result.error
            Log.d("EditProfileActivity",exception.toString())
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

        closeImageView.setOnClickListener {
            val intent=Intent(this,HomeActivity::class.java)
            startActivity(intent)
            finish()
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
        val currenUserId=FirebaseAuth.getInstance().currentUser!!.uid
        val fileRef=storageProfilePictureReference!!.child(currenUserId)

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

                ref?.child(currenUserId)?.updateChildren(userMap)
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
                Log.d("TAG", error.getMessage())
            }
        })
    }
}
