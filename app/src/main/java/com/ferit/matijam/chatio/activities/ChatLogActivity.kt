package com.ferit.matijam.chatio.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.ferit.matijam.chatio.R
import com.ferit.matijam.chatio.fragments.NewMessageFragment
import com.ferit.matijam.chatio.items.ImageMessageFromItem
import com.ferit.matijam.chatio.items.ImageMessageToItem
import com.ferit.matijam.chatio.items.MessageFromItem
import com.ferit.matijam.chatio.items.MessageToItem
import com.ferit.matijam.chatio.models.ChatMessage
import com.ferit.matijam.chatio.models.User
import com.ferit.matijam.chatio.utils.DatabaseOfflineSupport
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.util.*

class ChatLogActivity : AppCompatActivity() {
    companion object
    {
        const val TAG="ChatLog"
    }
    private lateinit var deselectImageView: ImageView
    private lateinit var openGalleryImageView: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var sendMessageImageView: ImageView
    private lateinit var editMessageEditText:EditText
    private var toUser:User?=null
    private val chatAdapter=GroupAdapter<GroupieViewHolder>()
    private var imageUri:Uri?=null

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
        } else {
            val exception = result.error
            Log.d(TAG,exception.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        toUser=intent.getParcelableExtra<User>(NewMessageFragment.USER_KEY)
        supportActionBar?.title=toUser?.username

        deselectImageView=findViewById(R.id.deselect_image_image_view_chat_log)
        recyclerView=findViewById(R.id.recycler_view_chat_log)
        sendMessageImageView=findViewById(R.id.send_image_view_chat_log)
        editMessageEditText=findViewById(R.id.edit_text_chat_log)
        openGalleryImageView=findViewById(R.id.open_gallery_image_view_chat_log)


        recyclerView.apply{
            layoutManager=LinearLayoutManager(this@ChatLogActivity)
            adapter=chatAdapter
        }
        openGalleryImageView.setOnClickListener {
            startCrop()
            deselectImageView.visibility=View.VISIBLE

        }
        deselectImageView.setOnClickListener {
            imageUri=null
        }

        listenForTextMessages()

        sendMessageImageView.setOnClickListener {

            if(imageUri!=null)
            {
                sendImageMessage()
            }
            if(editMessageEditText.text.toString()!="")
            {
                sendTextMessage()
                editMessageEditText.text.clear()
            }
        }

    }

    private fun startCrop() {
        // start picker to get image for cropping and then use the image in cropping activity
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
            }
        )
    }

    private fun sendImageMessage() {
        val currentUserId=FirebaseAuth.getInstance().currentUser?.uid
        val toUserId=toUser?.uid

        if(currentUserId==null)return
        val storageChatPicturesReference= FirebaseStorage.getInstance().getReference("Chat Pictures")
        val filename= UUID.randomUUID().toString()
        val fileRef=storageChatPicturesReference.child(filename)

        var uploadTask:StorageTask<*>
        uploadTask=fileRef.putFile(imageUri!!)

        uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{
            if(!it.isSuccessful)
            {
                it.exception?.let{
                    throw it
                }
            }
            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener( OnCompleteListener<Uri> { task ->
            if(task.isSuccessful)
            {
                val downloadUri=task.result
                val imageUrl=downloadUri.toString()

                val databaseRef = DatabaseOfflineSupport.getDatabase()
                    ?.getReference("/user_messages/$currentUserId/$toUserId")?.push()

                val databaseToRef =DatabaseOfflineSupport.getDatabase()
                    ?.getReference("/user_messages/$toUserId/$currentUserId")?.push()

                val currentTimeMillis=System.currentTimeMillis()/1000
                val chatMessage=ChatMessage(databaseRef?.key!!,"",imageUrl,currentUserId,toUserId,currentTimeMillis)

                databaseRef?.setValue(chatMessage)
                    ?.addOnCompleteListener {
                    Log.d(TAG,"Saved picture")
                    imageUri=null
                    deselectImageView.visibility=View.GONE
                    recyclerView.scrollToPosition(chatAdapter.itemCount-1)
                }
                databaseToRef?.setValue(chatMessage)
                    ?.addOnCanceledListener {
                        Log.d(TAG,"Saved picture")
                    }
                val chatTextMessage=ChatMessage(databaseRef?.key!!,"Sent image",imageUrl,currentUserId,toUserId, currentTimeMillis)

                val latestMessageRef=DatabaseOfflineSupport.getDatabase()
                    ?.getReference("/latest_messages/$currentUserId/$toUserId")
                latestMessageRef?.setValue(chatTextMessage)

                val latestMessageToRef=DatabaseOfflineSupport.getDatabase()
                    ?.getReference("/latest_messages/$toUserId/$currentUserId")
                latestMessageToRef?.setValue(chatTextMessage)
            }
        })


    }

    private fun listenForTextMessages() {
        val toUserId=toUser?.uid
        val currentUserId=FirebaseAuth.getInstance().currentUser?.uid

        val ref=DatabaseOfflineSupport.getDatabase()
            ?.getReference("/user_messages/$currentUserId/$toUserId")
        ref?.keepSynced(true)
        ref?.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage=snapshot.getValue(ChatMessage::class.java)

                if(chatMessage!=null){
                    if(chatMessage.fromUserId==FirebaseAuth.getInstance().currentUser?.uid)
                    {
                        val currentUser=HomeActivity.currentUser?:return
                        if(chatMessage.imageUrl=="")
                        {
                        chatAdapter.add(MessageFromItem(chatMessage.text,currentUser))
                        }else if(chatMessage.text==""){
                            chatAdapter.add(ImageMessageFromItem(chatMessage.imageUrl,currentUser,this@ChatLogActivity))

                        }else{
                            chatAdapter.add(MessageToItem(chatMessage.text,toUser!!))
                            chatAdapter.add(ImageMessageFromItem(chatMessage.imageUrl,toUser!!,this@ChatLogActivity))
                        }
                    }
                    else{
                        if(chatMessage.imageUrl=="")
                        {
                            chatAdapter.add(MessageToItem(chatMessage.text,toUser!!))
                        }else if(chatMessage.text==""){
                            chatAdapter.add(ImageMessageToItem(chatMessage.imageUrl,toUser!!,this@ChatLogActivity))
                        }else{
                            chatAdapter.add(MessageToItem(chatMessage.text,toUser!!))
                            chatAdapter.add(ImageMessageToItem(chatMessage.imageUrl,toUser!!,this@ChatLogActivity))
                        }
                    }
                }
                recyclerView.scrollToPosition(chatAdapter.itemCount -1)
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
    private fun sendTextMessage()
    {
        val currentUserId=FirebaseAuth.getInstance().currentUser?.uid
        val toUserId=toUser?.uid

        if(currentUserId==null)return

        val text=editMessageEditText.text.toString()

        val ref=DatabaseOfflineSupport.getDatabase()
            ?.getReference("/user_messages/$currentUserId/$toUserId")?.push()

        val toRef=DatabaseOfflineSupport.getDatabase()
            ?.getReference("/user_messages/$toUserId/$currentUserId")?.push()

        val chatTextMessage=ChatMessage(ref?.key!!,text,"",currentUserId,toUserId,System.currentTimeMillis()/1000)
        ref.setValue(chatTextMessage)
            .addOnCompleteListener {
                Log.d(TAG,"Saved message")
                editMessageEditText.text.clear()
                recyclerView.scrollToPosition(chatAdapter.itemCount-1)
            }
        toRef?.setValue(chatTextMessage)
            ?.addOnCompleteListener {
                Log.d(TAG,"Saved message")
            }

        val latestMessageRef=DatabaseOfflineSupport.getDatabase()
            ?.getReference("/latest_messages/$currentUserId/$toUserId")
        latestMessageRef?.setValue(chatTextMessage)

        val latestMessageToRef=DatabaseOfflineSupport.getDatabase()
            ?.getReference("/latest_messages/$toUserId/$currentUserId")
        latestMessageToRef?.setValue(chatTextMessage)
    }
}

