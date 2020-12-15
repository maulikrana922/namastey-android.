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
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.ChatAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityChatBinding
import com.namastey.fragment.ChatSettingsFragment
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
import javax.inject.Inject


class ChatActivity : BaseActivity<ActivityChatBinding>(), ChatBasicView,
    ChatSettingsFragment.onDataPassToActivity {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var activityChatBinding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private var matchesListBean = MatchesListBean()
    private var chatMsgList = ArrayList<ChatMessage>()
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myChatRef: DatabaseReference = database.reference
    private var storage = Firebase.storage
    private var storageRef = storage.reference

    private val TAG = "ChatActivity"
    private var recorder: MediaRecorder? = null
    private var voiceFileName: String? = ""
    private var isFromProfile = false
    private var whoCanSendMessage: Int = -1
    private var characterCount:  Int = 0

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

        initData()
    }

    private fun initData() {
        if (intent.hasExtra("matchesListBean")) {
            matchesListBean =
                intent.getParcelableExtra<MatchesListBean>("matchesListBean") as MatchesListBean

//            Log.e("ChatActivity", "matchesListBean id\t  ${matchesListBean.id}")
//            Log.e("ChatActivity", "matchesListBean username\t  ${matchesListBean.username}")

            if (intent.hasExtra("isFromProfile") && intent.hasExtra("whoCanSendMessage")){
                whoCanSendMessage = intent.getIntExtra("whoCanSendMessage",2)
                isFromProfile = intent.getBooleanExtra("isFromProfile", false)
                Log.e("ChatActivity", "isFromProfile: \t $isFromProfile")
//                if (matchesListBean.is_match == 1)
//                    chatToolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorWhite))
//                else
//                    viewBuyNow.visibility = View.VISIBLE
            }

            if (matchesListBean.is_match == 1 || whoCanSendMessage == 0)
                chatToolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorWhite))
            else{
                viewBuyNow.visibility = View.VISIBLE
            }

            voiceFileName = "${externalCacheDir?.absolutePath}/voicerecord.mp3"

//            Call api if matches already not read
            if (matchesListBean.is_read == 0) {
                chatViewModel.readMatches(matchesListBean.id, 1)
            }
            chatViewModel.setIsLoading(true)
            myChatRef = database.getReference(Constants.FirebaseConstant.CHATS)

            val chatId = if (sessionManager.getUserId() < matchesListBean.id)
                sessionManager.getUserId().toString().plus(matchesListBean.id)
            else
                matchesListBean.id.toString().plus(sessionManager.getUserId())

            myChatRef.child(chatId).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    chatMsgList.clear()

                    for (snapshot in dataSnapshot.children) {
                        val chatMessage: ChatMessage = snapshot.getValue(ChatMessage::class.java)!!
//                        Log.d("Firebase :", "Value is: ${chatMessage.message}")

                        if (chatMessage.receiver == sessionManager.getUserId() && chatMessage.sender == matchesListBean.id ||
                            chatMessage.receiver == matchesListBean.id && chatMessage.sender == sessionManager.getUserId()
                        ) {
                            chatMsgList.add(chatMessage)
                        }
                    }
                    chatViewModel.setIsLoading(false)
                    chatAdapter =
                        ChatAdapter(this@ChatActivity, sessionManager.getUserId(), chatMsgList)
                    rvChat.adapter = chatAdapter

                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("Firebase :", "Failed to read value.", error.toException())
                }
            })

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

    fun hideChatMoreButton(isHide: Boolean) {
        if (isHide) {
            ivChatMore.visibility = View.GONE
        } else {
            ivChatMore.visibility = View.VISIBLE
        }
    }

    fun onClickChatSettings(view: View) {
        addFragment(
            ChatSettingsFragment.getInstance(matchesListBean!!),
            Constants.CHAT_SETTINGS_FRAGMENT
        )
    }

    override fun onResume() {
        super.onResume()
        hideChatMoreButton(false)
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

    override fun onSuccess(msg: String) {

    }

    override fun onDestroy() {
        chatViewModel.onDestroy()
        super.onDestroy()
    }

    fun onClickSendMessage(view: View) {
        if (edtMessage.text.trim().isNotEmpty()) {
            characterCount += edtMessage.text.length
            if (matchesListBean.is_match == 1)
                sendMessage(edtMessage.text.toString(), "")
            else if (isFromProfile && whoCanSendMessage == 0 && characterCount <= Constants.MAX_CHARACTER){   // your can send 280 character with public account else purchase plan
                Log.d("ChatActivity : ", "Count  $characterCount")
                sendMessage(edtMessage.text.toString(), "")
            }
        }
    }

    private fun sendMessage(message: String, imageUrl: String) {

        val chatMessage = ChatMessage(
            message,
            sessionManager.getUserId(),
            matchesListBean.id,
            imageUrl,
            System.currentTimeMillis()
        )
        val chatId = if (sessionManager.getUserId() < matchesListBean.id)
            sessionManager.getUserId().toString().plus(matchesListBean.id)
        else
            matchesListBean.id.toString().plus(sessionManager.getUserId())

        if (matchesListBean.is_block == 1){
            showMsg(getString(R.string.msg_block_user_chat))
        }else {
            myChatRef.child(chatId).push().setValue(chatMessage).addOnSuccessListener {
                edtMessage.setText("")
            }
        }
    }

    /**
     * Open camera and select image
     */
    fun onClickCamera(view: View) {
        if (matchesListBean.is_block == 1){
            showMsg(getString(R.string.msg_block_user_chat))
        }else {
            if (matchesListBean.is_match == 1) {
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
//                val bitmap: Bitmap = Utils.scaleBitmapDown(
//                    MediaStore.Images.Media.getBitmap(contentResolver, photoUri),
//                    1200
//                )!!
//                pictureFile = Utils.getCameraFile(this@ChatActivity)

                uploadFile(photoUri, true)
            } else if (requestCode == Constants.REQUEST_CODE_IMAGE) {
                if (data?.clipData != null) {
                    val count = data.clipData?.itemCount
                    for (i in 0 until count!!) {
                        val imageUri: Uri = data.clipData?.getItemAt(i)!!.uri
                        uploadFile(imageUri, true)
                    }

                } else if (data?.data != null) {
                    // if single image is selected
                    val imageUri: Uri = data.data!!
                    uploadFile(imageUri, true)
                }

//                try {
//                    val selectedImage = data!!.data
//                    if (selectedImage != null)
//                        uploadFile(selectedImage)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
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
        if (matchesListBean.is_block == 1){
            showMsg(getString(R.string.msg_block_user_chat))
        }else {
            if (matchesListBean.is_match == 1) {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(intent, Constants.REQUEST_CODE_IMAGE);
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
//        player?.release()
//        player = null
    }

    fun onClickProfile(view: View) {
        val intent = Intent(this@ChatActivity, ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, matchesListBean.id)
        openActivity(intent)
    }

    fun onClickBuyNow(view: View) {
        openActivity(this@ChatActivity,MembershipActivity())
    }

    override fun chatSettingData(isBlock: Int) {
        Log.d("Block user:", isBlock.toString())
        matchesListBean.is_block = isBlock
    }
}