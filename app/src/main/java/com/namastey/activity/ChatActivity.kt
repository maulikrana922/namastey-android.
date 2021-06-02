package com.namastey.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.ChatAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityChatBinding
import com.namastey.fragment.ChatSettingsFragment
import com.namastey.listeners.OnChatMessageClick
import com.namastey.model.ChatMessage
import com.namastey.model.MatchesListBean
import com.namastey.uiView.ChatBasicView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject


class ChatActivity : BaseActivity<ActivityChatBinding>(), ChatBasicView,
    ChatSettingsFragment.onDataPassToActivity, OnChatMessageClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var activityChatBinding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private var matchesListBean = MatchesListBean()
    private var chatMsgList = ArrayList<ChatMessage>()

    //    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
//    private var myChatRef: DatabaseReference = database.reference
    private var storage = Firebase.storage
    private var storageRef = storage.reference
    private var unreadCount = 0
//    private lateinit var lastVisible: DocumentSnapshot
//    private var pageLimit = 5L
//    private var isScrolling = false
//    private var isLastItemReached = false

    private val db = Firebase.firestore
    private lateinit var docReference: DocumentReference
    private lateinit var listenerRegistration: ListenerRegistration

    private val TAG = "ChatActivity"
    private var recorder: MediaRecorder? = null
    private var voiceFileName: String? = ""
    private var isFromProfile = false
    private var isFromMessage = false
    private var chatNotification = false
    private var whoCanSendMessage: Int = -1
    private var isFollowMe = false
    private var characterCount: Int = 0

    companion object {
        var isChatActivityOpen = false
        var userId: Long = 0L
    }

    override fun getViewModel() = chatViewModel

    override fun getLayoutId() = R.layout.activity_chat

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        chatViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)
        activityChatBinding = bindViewData()
        activityChatBinding.viewModel = chatViewModel

        isChatActivityOpen = true
        initData()
    }

    private fun initData() {
        if (intent.hasExtra("matchesListBean")) {
            matchesListBean =
                intent.getParcelableExtra<MatchesListBean>("matchesListBean") as MatchesListBean

//            Log.e("ChatActivity", "matchesListBean id\t  ${matchesListBean.id}")
//            Log.e("ChatActivity", "matchesListBean username\t  ${matchesListBean.username}")
            val isFromAdmin = intent.getBooleanExtra("isFromAdmin", false)
            Log.e("ChatActivity", "isFromAdmin: $isFromAdmin")
            if (intent.hasExtra("isFromAdmin") && intent.getBooleanExtra("isFromAdmin", false)) {
                if (matchesListBean.profile_pic != "") {
                    GlideLib.loadImage(this, ivProfileUser, matchesListBean.profile_pic)
                } else {
                    Glide
                        .with(this)
                        .load(ContextCompat.getDrawable(this, R.drawable.default_placeholder))
                        .into(ivProfileUser)
                }
                if (matchesListBean.username != "") {
                    tvUserName.text = matchesListBean.username
                }
                chatToolbar.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorWhite
                    )
                )
                llChatBox.visibility = View.GONE
                hideChatMoreButton(true)
                ivChatMore.visibility = View.GONE
                tvUserName.setOnClickListener(null)
                ivProfileUser.setOnClickListener(null)
                chatViewModel.getAdminMsgList()
                /* chatAdapter =
                                   ChatAdapter(this@ChatActivity, sessionManager.getUserId(), chatMsgList)
                               rvChat.adapter = chatAdapter*/
            } else {
                if (intent.hasExtra("isFromProfile") && intent.hasExtra("whoCanSendMessage")) {
                    whoCanSendMessage = intent.getIntExtra("whoCanSendMessage", 2)
                    isFromProfile = intent.getBooleanExtra("isFromProfile", false)
                    isFollowMe = intent.getBooleanExtra("isFollowMe", false)
                    Log.e("ChatActivity", "isFromProfile: \t $isFromProfile")
//                if (matchesListBean.is_match == 1)
//                    chatToolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorWhite))
//                else
//                    viewBuyNow.visibility = View.VISIBLE
                }
                if (intent.hasExtra("isFromMessage")) {
                    isFromMessage = intent.getBooleanExtra("isFromMessage", false)
                    when (matchesListBean.is_follow_me) {
                        1 -> isFollowMe = true
                        else -> isFollowMe = false
                    }
                }
                if (intent.hasExtra("chatNotification"))
                    chatNotification = intent.getBooleanExtra("chatNotification", false)

                if (matchesListBean.is_match == 1 || whoCanSendMessage == 0 || isFromMessage)
                    chatToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorWhite))
                else {
                    if (isFollowMe)
                        chatToolbar.setBackgroundColor(
                            ContextCompat.getColor(
                                this,
                                R.color.colorWhite
                            )
                        )
                    else
                        viewBuyNow.visibility = View.VISIBLE
                }

                voiceFileName = "${externalCacheDir?.absolutePath}/voicerecord.mp3"

//            Call api if matches already not read
                if (matchesListBean.is_read == 0) {
                    chatViewModel.readMatches(matchesListBean.id, 1)
                }
                userId = matchesListBean.id
                chatViewModel.setIsLoading(true)
//            myChatRef = database.getReference(Constants.FirebaseConstant.CHATS)

                val chatId = if (sessionManager.getUserId() < matchesListBean.id)
                    sessionManager.getUserId().toString().plus("_").plus(matchesListBean.id)
                else
                    matchesListBean.id.toString().plus("_").plus(sessionManager.getUserId())
                db.clearPersistence()

//            This part for isRead message or not
                val docRef = db.collection(Constants.FirebaseConstant.MESSAGES)
                    .document(chatId)
                    .collection(Constants.FirebaseConstant.LAST_MESSAGE)


                listenerRegistration = docRef.addSnapshotListener { document, error ->
                    if (document != null) {
                        Log.d("Success", "DocumentSnapshot data: ")
                        for (messageDocument in document.documents) {
                            val chatMessage = messageDocument.toObject(ChatMessage::class.java)
                            if (chatMessage != null) {
                                unreadCount = chatMessage.unreadCount
                                if (chatMessage.sender == matchesListBean.id && chatMessage.receiver == sessionManager.getUserId()) {
                                    docRef.document(chatId).update(
                                        "read", 1,
                                        "unreadCount", 0
                                    )
                                }
                            }
                        }

                    } else {
                        Log.d("TAG", "No such document")
                    }
                }

                chatAdapter =
                    ChatAdapter(this@ChatActivity, sessionManager.getUserId(), chatMsgList, this)
                rvChat.adapter = chatAdapter

                db.collection(Constants.FirebaseConstant.MESSAGES)
                    .document(chatId)
                    .collection(Constants.FirebaseConstant.CHATS)
                    .addSnapshotListener { messageSnapshot, exception ->
                        if (messageSnapshot == null || messageSnapshot.isEmpty) {
                            chatViewModel.setIsLoading(false)
                            return@addSnapshotListener
                        }
                        chatMsgList.clear()

                        for (messageDocument in messageSnapshot.documents) {
                            val chatMessage = messageDocument.toObject(ChatMessage::class.java)

                            if (chatMessage != null) {
                                if (chatMessage.receiver == sessionManager.getUserId() && chatMessage.sender == matchesListBean.id ||
                                    chatMessage.receiver == matchesListBean.id && chatMessage.sender == sessionManager.getUserId()
                                ) {
                                    chatMsgList.add(chatMessage)
                                }
                            }
                        }
                        chatViewModel.setIsLoading(false)

                        chatMsgList.sortBy { it.timestamp }
                        chatAdapter.notifyDataSetChanged()
                        rvChat.smoothScrollToPosition(chatAdapter.itemCount)

                    }

                if (matchesListBean.profile_pic != "") {
                    GlideLib.loadImage(this, ivProfileUser, matchesListBean.profile_pic)
                } else {
                    Glide
                        .with(this)
                        .load(ContextCompat.getDrawable(this, R.drawable.default_placeholder))
                        .into(ivProfileUser)
                }

                if (matchesListBean.username != "") {
                    tvUserName.text = matchesListBean.username
                }

                ivMic.setOnTouchListener(object : View.OnTouchListener {
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        when (event?.action) {
                            MotionEvent.ACTION_DOWN -> {
                                if (matchesListBean.is_block == 0) {
                                    if (matchesListBean.is_match == 1) {
                                        isAudioPermissionGranted()
                                        ivMic.setPadding(7, 7, 7, 7)
                                    }
                                }
                                return true
                            }
                            MotionEvent.ACTION_UP -> {
                                Log.d(TAG, "Call stop record....")
                                if (matchesListBean.is_block == 0) {
                                    if (matchesListBean.is_match == 1) {
                                        ivMic.setPadding(0, 0, 0, 0)
                                        stopRecording()
                                    }
                                }
                            }
                        }

                        return v?.onTouchEvent(event) ?: true
                    }
                })
                hideChatMoreButton(false)
            }

        }
    }

    fun hideChatMoreButton(isHide: Boolean) {
        if (isHide) {
            ivChatMore.visibility = View.GONE
        } else {
            ivChatMore.visibility = View.VISIBLE
        }
    }

    fun onClickChatSettings(view: View) {
        addFragment(
            ChatSettingsFragment.getInstance(
                matchesListBean, matchesListBean.id
            ),
            Constants.CHAT_SETTINGS_FRAGMENT
        )
    }

    override fun onResume() {
        super.onResume()
        if (intent.hasExtra("isFromAdmin") && intent.getBooleanExtra("isFromAdmin", false)) {
            hideChatMoreButton(true)
        } else {
            hideChatMoreButton(false)
        }
    }

    fun onClickChatBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            hideChatMoreButton(false)
        }

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finishActivity()
        }
    }

    override fun onSuccessReport(msg: String) {
    }

    override fun onSuccessBlockUser(msg: String) {
    }

    override fun onSuccessDeleteMatches(msg: String) {
        finishActivity()
    }

    override fun onSuccessAdminMessage(data: ArrayList<ChatMessage>) {
        Log.e("ChatActivity", "onSuccessAdminMessage: \t ${data.size}")
        this.chatMsgList = data
        chatAdapter = ChatAdapter(this@ChatActivity, sessionManager.getUserId(), data, this)
        rvChat.adapter = chatAdapter
    }

    override fun onSuccessMuteNotification(message: String) {
        TODO("Not yet implemented")
    }

    override fun onSuccess(msg: String) {

    }

    override fun onDestroy() {
        chatViewModel.onDestroy()
        isChatActivityOpen = false
        userId = 0
        super.onDestroy()
        if (::listenerRegistration.isInitialized)
            listenerRegistration.remove()
    }

    fun onClickSendMessage(view: View) {
        if (edtMessage.text.trim().isNotEmpty()) {
            characterCount += edtMessage.text.length
            if (chatNotification || isFromMessage) {
                sendMessage(edtMessage.text.toString(), "")
            } else if (matchesListBean.is_match == 1 || isFollowMe)
                sendMessage(edtMessage.text.toString(), "")
            else if (isFromProfile && whoCanSendMessage == 0 && characterCount <= Constants.MAX_CHARACTER) {   // your can send 280 character with public account else purchase plan
                Log.d("ChatActivity : ", "Count  $characterCount")
                sendMessage(edtMessage.text.toString(), "")
            }
        }
    }

    private fun sendMessage(message: String, imageUrl: String) {

        chatViewModel.startChat(matchesListBean.id, 1)

        val chatMessage = ChatMessage(
            message,
            sessionManager.getUserId(),
            matchesListBean.id,
            imageUrl,
            System.currentTimeMillis(),
            0,
            0
        )
        val chatId = if (sessionManager.getUserId() < matchesListBean.id)
            sessionManager.getUserId().toString().plus("_").plus(matchesListBean.id)
        else
            matchesListBean.id.toString().plus("_").plus(sessionManager.getUserId())

        if (matchesListBean.is_block == 1) {
            showMsg(getString(R.string.msg_block_user_chat))
        } else {
//            myChatRef.child(chatId).push().setValue(chatMessage).addOnSuccessListener {
//                edtMessage.setText("")
//            }


//            Firestore part
            db.collection(Constants.FirebaseConstant.MESSAGES)
                .document(chatId)
                .collection(Constants.FirebaseConstant.CHATS)
                .add(chatMessage)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
//                    val chatMessage = documentReference.get().result.toObject(ChatMessage::class.java)
//                    chatMsgList.add(chatMessage!!)
//                    chatAdapter.notifyItemInserted(chatAdapter.itemCount)
                    edtMessage.setText("")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

//            Added last message
            chatMessage.unreadCount = ++unreadCount
            db.collection(Constants.FirebaseConstant.MESSAGES)
                .document(chatId)
                .collection(Constants.FirebaseConstant.LAST_MESSAGE)
                .document(chatId).set(chatMessage)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

//            Firestore part
        }
    }

    /**
     * Open camera and select image
     */
    fun onClickCamera(view: View) {
        if (matchesListBean.is_block == 1) {
            showMsg(getString(R.string.msg_block_user_chat))
        } else {
            if (matchesListBean.is_match == 1 || isFollowMe) {
                if (isPermissionGrantedForCamera())
                    capturePhoto()
            }
        }
    }

    private fun capturePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            try {

                val photoUri: Uri = FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName + ".provider",
                    Utils.getCameraFile(this@ChatActivity)
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                startActivityForResult(takePictureIntent, Constants.REQUEST_CODE_CAMERA_IMAGE)

            } catch (ex: Exception) {
                showMsg(ex.localizedMessage)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.REQUEST_CODE_CAMERA_IMAGE) {
                val photoUri = FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName.plus(".provider"),
                    Utils.getCameraFile(this@ChatActivity)
                )
                uploadFile(photoUri, true)
            } else if (requestCode == Constants.REQUEST_CODE_IMAGE) {
                val intent = Intent(this@ChatActivity, ImageSliderActivity::class.java)
                if (data?.clipData != null) {
                    intent.clipData = data.clipData
                } else if (data?.data != null) {
                    // if single image is selected
                    val imageUri: Uri = data.data!!
                    intent.putExtra("imageUri", imageUri)
                }
                openActivityForResult(intent, Constants.REQUEST_CODE_SLIDE_IMAGE)
            } else if (requestCode == Constants.REQUEST_CODE_SLIDE_IMAGE) {
                if (data != null) {
                    if (data.hasExtra("imageUri")) {
                        val imageUri: Uri = data.getParcelableExtra<Uri>("imageUri")!!
                        uploadFile(imageUri, true)
                    } else {
                        val count = data.clipData?.itemCount
                        for (i in 0 until count!!) {
                            val imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                            uploadFile(imageUri, true)
                        }
                    }
                }
            }
        }
    }

    private fun uploadFile(photoUri: Uri, isImageUpload: Boolean) {
        chatViewModel.setIsLoading(true)

        Log.d(TAG, "Upload file started....")
        val fileStorage: StorageReference = if (isImageUpload) {
            storageRef.child(
                Constants.FirebaseConstant.IMAGES.plus("/").plus(System.currentTimeMillis())
            )
        } else {
            storageRef.child(
                Constants.FirebaseConstant.VOICE.plus("/").plus(System.currentTimeMillis())
            )
        }

//        val imagesRef = storageRef.child(Constants.FirebaseConstant.IMAGES.plus("/").plus(System.currentTimeMillis()))

        fileStorage.putFile(photoUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    val imageUrl = it.toString()
                    chatViewModel.setIsLoading(false)
                    Log.d(TAG, "File upload success....")
                    if (isImageUpload)
                        sendMessage(Constants.FirebaseConstant.MSG_TYPE_IMAGE, imageUrl)
                    else
                        sendMessage(Constants.FirebaseConstant.MSG_TYPE_VOICE, imageUrl)
                }
            }

            .addOnFailureListener { e ->
                print(e.message)
                chatViewModel.setIsLoading(false)
                Log.d("Firebase : ", "Image uplaod issue")
            }
    }

    fun onClickSelectImage(view: View) {
        // For latest versions API LEVEL 19+
        if (matchesListBean.is_block == 1) {
            showMsg(getString(R.string.msg_block_user_chat))
        } else {
            if (matchesListBean.is_match == 1 || isFollowMe) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, Constants.REQUEST_CODE_IMAGE)
            }
        }
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(voiceFileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            try {
                Log.d(TAG, "Recording started....")
                prepare()
            } catch (e: IOException) {
                Log.e("LOG_TAG", "prepare() failed")
            }

            start()
        }
    }

    private fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        val voiceUri = Uri.fromFile(File(voiceFileName!!))
        Log.d(TAG, "Recording ended....")
        uploadFile(voiceUri, false)
    }

    private fun isAudioPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission granted
                startRecording()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.RECORD_AUDIO
                    ), Constants.PERMISSION_STORAGE
                )
            }
        } else {
            startRecording()
        }
    }

    override fun onStop() {
        super.onStop()
        recorder?.release()
        recorder = null
        isChatActivityOpen = false
        userId = 0
//        player?.release()
//        player = null
    }

    fun onClickProfile(view: View) {
        val intent = Intent(this@ChatActivity, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, matchesListBean.id)
        openActivity(intent)
    }

    fun onClickBuyNow(view: View) {
        openActivity(this@ChatActivity, MembershipActivity())
    }

    override fun chatSettingData(isBlock: Int) {
        Log.d("Block user:", isBlock.toString())
        matchesListBean.is_block = isBlock
    }

    override fun onChatImageClick(imageUrl: String) {
        val intent = Intent(this@ChatActivity, ImageDisplayActivity::class.java)
        intent.putExtra("imageUrl",imageUrl)
        openActivity(intent)
    }
    override fun onChatMessageClick(message: String, position: Int) {
        when {
            message.contains("Edit your bio") -> {
                openActivity(this@ChatActivity, EditProfileActivity())
            }
            message.contains("Add More Video") -> {
                val intent = Intent(this@ChatActivity, CreateAlbumActivity::class.java)
                intent.putExtra("fromAlbumList", false)
                openActivity(intent)
            }
            message.contains("Edit your profile") -> {
                openActivity(this@ChatActivity, EditProfileActivity())
            }
            message.contains("Add Social links") -> {
                val intent = Intent(this@ChatActivity, EditProfileActivity::class.java)
                intent.putExtra("fromAddSocialLink", true)
                openActivity(intent)
            }
            message.contains("Buy Boost") -> {
                val intent = Intent(this@ChatActivity, ProfileActivity::class.java)
                intent.putExtra("fromBuyBoost", true)
                openActivity(intent)
            }
            message.contains("Try our membership") -> {
                openActivity(this@ChatActivity, MembershipActivity())
            }
            message.contains("To visit profile click on username") -> {
                val value = message.substring(message.indexOf("F30C45") + 7)
//                Log.d(TAG,"Username : " + value)
                val username = value.substring(0,value.indexOf("</font>"))
//                Log.d(TAG,"Username : " + username)
                val intent = Intent(this@ChatActivity, ProfileViewActivity::class.java)
                intent.putExtra(Constants.USERNAME, username)
                openActivity(intent)
            }
        }
    }
}