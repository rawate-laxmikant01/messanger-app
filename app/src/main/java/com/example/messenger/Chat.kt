package com.example.messenger

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.messenger.Adapter.ChatAdapter
import com.example.messenger.Fcm.FcmNotificationsSender
import com.example.messenger.Model.ChatModel
import com.example.messenger.databinding.ActivityChatBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class Chat : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    val db = Firebase.firestore

    //   lateinit var status:String
    lateinit var receverroom: String
    lateinit var senderroom: String

    //  lateinit var token: String
    lateinit var chatingId: String
    lateinit var uuid: String

    lateinit var ID: String
    private var imgurl: String? = null

    private val pickImage = 100
    private var imageUri: Uri? = null
    lateinit var timestamp: Timestamp
    lateinit var currentDateAndTime: String
    var idDocument: String = UUID.randomUUID().toString()

    val CAMARA_REQUEST_CODE = 1

    var storageRef = FirebaseStorage.getInstance().getReference("chat")

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle(intent.getStringExtra("name"))
        // binding.chatName.text = intent.getStringExtra("name")
        chatingId = intent.getStringExtra("id").toString()
        binding.imageSendView.visibility = View.GONE
        binding.linearLayout.visibility = View.VISIBLE


        var typemsg: EditText = binding.typeMessage

        uuid = FirebaseAuth.getInstance().currentUser!!.uid
        //date and time
        val simpleDateFormat = SimpleDateFormat("hh:mm aa")
        //   val sdf = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
        timestamp = Timestamp(System.currentTimeMillis())
        currentDateAndTime = simpleDateFormat.format(Date())

        var chatRecycler: RecyclerView = binding.chatRecycler
        val chatlist = ArrayList<ChatModel>()
        chatRecycler.layoutManager = LinearLayoutManager(this)
        senderroom = uuid + chatingId
        receverroom = chatingId + uuid

        //-------------------------------------------------------
        lateinit var userstatus: String

        db.collection("users").document(chatingId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    //   Toast.makeText(this, "" + snapshot["status"], Toast.LENGTH_SHORT).show()
                    userstatus = snapshot["status"].toString()
                    binding.userOnlineStatus.setText(userstatus)
                    //     Toast.makeText(this, ""+snapshot.data.toString(), Toast.LENGTH_SHORT).show()
                } else {
                    // Log.d(TAG, "Current data: null")
                }
            }
        //--------------------------------------------------------------------
        binding.chatRecycler.smoothScrollToPosition(ScrollView.FOCUS_DOWN)
        binding.scroolview.fullScroll(ScrollView.FOCUS_DOWN)
        chatRecycler.smoothScrollToPosition(chatlist.count())

        db.collection("chat").document(receverroom)
            .collection("message")
            .orderBy("dateorder")
            .addSnapshotListener { data, error ->
                if (error != null) {
                    Toast.makeText(this, "" + error.message, Toast.LENGTH_SHORT).show()
                } else {
                    if (data != null) {
                        chatlist.clear()
                        for (document in data) {
                            val chat: ChatModel = document.toObject(ChatModel::class.java)
                            chatlist.add(chat!!)
                        }
                        chatRecycler.adapter = ChatAdapter(chatlist, this@Chat)
                        //binding.scroolview.fullScroll(ScrollView.FOCUS_DOWN)


                    }
                }
            }


        binding.sendMessageBtn.setOnClickListener(View.OnClickListener {
            //  Toast.makeText(this, ""+msg, Toast.LENGTH_SHORT).show()
            var msg = typemsg.text
            if (msg.isEmpty()) {
                Toast.makeText(this, "Empty chat", Toast.LENGTH_SHORT).show()
            } else {
                val chatModel =
                    ChatModel(
                        msg.toString(),
                        currentDateAndTime,
                        chatingId,
                        timestamp.time.toString(),
                        0,
                        idDocument
                    )
                typemsg.setText("")

                db.collection("chat")
                    .document(senderroom)
                    .collection("message")
                    .document(idDocument)
                    .set(chatModel)


                db.collection("chat")
                    .document(receverroom)
                    .collection("message")
                    .document(idDocument)
                    .set(chatModel)

                notification(msg.toString())

            }
        })

        binding.cancleImage.setOnClickListener(View.OnClickListener {
            binding.imageSendView.visibility = View.GONE
            binding.linearLayout.visibility = View.VISIBLE
        })

        binding.addAttachment.setOnClickListener(View.OnClickListener {
            val popupMenu: PopupMenu = PopupMenu(this, binding.addAttachment)
            popupMenu.menuInflater.inflate(R.menu.add_attachment_popup, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.cameraId ->
                        Toast.makeText(this@Chat, "You Clicked : " + item.title, Toast.LENGTH_SHORT)
                            .show()
                    //capturePhoto()

                    R.id.gallaryId ->
                        // Toast.makeText(this@Chat, "You Clicked : " + item.title, Toast.LENGTH_SHORT).show()
                        chooseImg()
                }
                true
            })
            popupMenu.show()
        })
    }

    fun capturePhoto() {

//        val callCameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (callCameraIntent.resolveActivity(packageManager) != null) {
//            startActivityForResult(callCameraIntent, CAMARA_REQUEST_CODE)
//        }
        val camara = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photo = File(Environment.getExternalStorageDirectory(), "Pic.jpg")
        intent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            Uri.fromFile(photo)
        )
        imageUri = Uri.fromFile(photo)
        if (camara.resolveActivity(packageManager) != null) {
            startActivityForResult(camara, CAMARA_REQUEST_CODE)
        }

    }

    override fun onResume() {
        super.onResume()
        db.collection("users").document(uuid).update("status", "online")
    }

    override fun onPause() {
        super.onPause()
        db.collection("users").document(uuid).update("status", "offline")
    }

    override fun onDestroy() {
        super.onDestroy()
        db.collection("users").document(uuid).update("status", "offline")
    }

    private fun notification(msgTitle: String) {
        val fcmNotificationsSender = FcmNotificationsSender(
            intent.getStringExtra("token").toString(),
            intent.getStringExtra("name").toString(),
            msgTitle,
            this@Chat
        )
        fcmNotificationsSender.SendNotifications()
    }

    private fun chooseImg() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data

            binding.gallayImage.setImageURI(imageUri)
            binding.imageSendView.visibility = View.VISIBLE
            binding.linearLayout.visibility = View.INVISIBLE
            binding.chatProgress.visibility = View.VISIBLE
            uploadpicture()
        }
    }

    private fun uploadpicture() {
        val randomID = UUID.randomUUID().toString()
        val riversRef: StorageReference = storageRef.child(randomID)
        imageUri?.let {
            riversRef.putFile(it) // Register observers to listen for when the download is done or if it fails
                .addOnFailureListener(OnFailureListener { exception -> // Handle unsuccessful uploads
                    Toast.makeText(
                        this@Chat,
                        "Unable to Add product Photo" + exception.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }).addOnSuccessListener(OnSuccessListener<Any?> {
                    Toast.makeText(this@Chat, "Image Added to storage", Toast.LENGTH_SHORT).show()
                    riversRef.getDownloadUrl()
                        .addOnSuccessListener(OnSuccessListener<Any> { downloadPhotoUrl ->
                            imgurl = downloadPhotoUrl.toString()
                            //   Toast.makeText(this, "" + imgurl, Toast.LENGTH_SHORT).show()
                            binding.chatProgress.visibility = View.GONE
                            binding.sendImagebtn.setOnClickListener(View.OnClickListener {

                                var idImageDoc: String = UUID.randomUUID().toString()


                                binding.imageSendView.visibility = View.GONE
                                binding.linearLayout.visibility = View.VISIBLE

                                val chatModel =
                                    ChatModel(
                                        imgurl,
                                        currentDateAndTime,
                                        chatingId,
                                        timestamp.time.toString(),
                                        1,
                                        idImageDoc
                                    )

//                                Toast.makeText(this, "" + currentDateAndTime, Toast.LENGTH_SHORT)
//                                    .show()
                                //  Toast.makeText(this, "" + imgurl, Toast.LENGTH_SHORT).show()

                                db.collection("chat")
                                    .document(senderroom)
                                    .collection("message")
                                    .document(idImageDoc)
                                    .set(chatModel).addOnCompleteListener(OnCompleteListener {
                                        notification("Image attaach")
                                    })

                                db.collection("chat")
                                    .document(receverroom)
                                    .collection("message")
                                    .document(idImageDoc)
                                    .set(chatModel)

                                Toast.makeText(this, "Image sending", Toast.LENGTH_SHORT).show()
                            })

                            ID = randomID
                        })
                }
                )
        }
    }
}


//class FirestorePagingAdapter {



//    mAdapter =
//    object : FirestorePagingAdapter<Post, PostViewHolder>(options) {
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
//            val view = layoutInflater.inflate(R.layout.item_post, parent, false)
//            return PostViewHolder(view)
//        }
//
//        override fun onBindViewHolder(viewHolder: PostViewHolder, position: Int, post: Post) {
//            // Bind to ViewHolder
//            viewHolder.bind(post)
//        }
//
//        override fun onError(e: Exception) {
//            super.onError(e)
//            Log.e("MainActivity", e.message)
//        }
//
//        override fun onLoadingStateChanged(state: LoadingState) {
//            when (state) {
//                LoadingState.LOADING_INITIAL -> {
//                    swipeRefreshLayout.isRefreshing = true
//                }
//
//                LoadingState.LOADING_MORE -> {
//                    swipeRefreshLayout.isRefreshing = true
//                }
//
//                LoadingState.LOADED -> {
//                    swipeRefreshLayout.isRefreshing = false
//                }
//
//                LoadingState.ERROR -> {
//                    Toast.makeText(
//                        applicationContext,
//                        "Error Occurred!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    swipeRefreshLayout.isRefreshing = false
//                }
//
//                LoadingState.FINISHED -> {
//                    swipeRefreshLayout.isRefreshing = false
//                }
//            }
//        }
//    }
//
//}