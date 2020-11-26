package com.namastey.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
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
import javax.inject.Inject


class ChatActivity : BaseActivity<ActivityChatBinding>(), ChatBasicView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var activityChatBinding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private var matchesListBean =  MatchesListBean()
    private var chatMsgList = ArrayList<ChatMessage>()
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myChatRef: DatabaseReference = database.reference
    private var storage = Firebase.storage
    private var storageRef = storage.reference
    private var pictureFile: File? = null

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
            matchesListBean = intent.getParcelableExtra<MatchesListBean>("matchesListBean") as MatchesListBean

//            Log.e("ChatActivity", "matchesListBean id\t  ${matchesListBean.id}")
//            Log.e("ChatActivity", "matchesListBean username\t  ${matchesListBean.username}")

            myChatRef = database.getReference(Constants.FirebaseConstant.CHATS)
            myChatRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    chatMsgList.clear()

                    for (snapshot in dataSnapshot.children) {
                        val chatMessage: ChatMessage = snapshot.getValue(ChatMessage::class.java)!!
                        Log.d("Firebase :", "Value is: ${chatMessage.message}")

                        if (chatMessage.receiver == sessionManager.getUserId() && chatMessage.sender == matchesListBean.id ||
                                chatMessage.receiver == matchesListBean.id && chatMessage.sender == sessionManager.getUserId()){
                            chatMsgList.add(chatMessage)
                        }
                    }
                    chatAdapter = ChatAdapter(this@ChatActivity,sessionManager.getUserId(),chatMsgList)
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
    }

    override fun onDestroy() {
        chatViewModel.onDestroy()
        super.onDestroy()
    }

    fun onClickSendMessage(view: View) {
        if (edtMessage.text.trim().isNotEmpty()){
            sendMessage(edtMessage.text.toString(),"")
        }
    }
    private fun sendMessage(message: String,imageUrl: String){

        val chatMessage = ChatMessage(message,sessionManager.getUserId(),matchesListBean.id,imageUrl)
        myChatRef.push().setValue(chatMessage).addOnSuccessListener {
            edtMessage.setText("")
        }
    }

    /**
     * Open camera and select image
     */
    fun onClickCamera(view: View) {
        if (isPermissionGrantedForCamera())
            capturePhoto()
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
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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

                uploadFile(photoUri)
            }
        }
    }

    private fun uploadFile(photoUri: Uri){
        chatViewModel.setIsLoading(true)

        val imagesRef = storageRef.child(Constants.FirebaseConstant.IMAGES.plus("/").plus(System.currentTimeMillis()))

        imagesRef.putFile(photoUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    val imageUrl = it.toString()
                    chatViewModel.setIsLoading(false)
                    sendMessage(Constants.FirebaseConstant.IMAGE_UPLOAD,imageUrl)
                }
            }

            .addOnFailureListener { e ->
                print(e.message)
                chatViewModel.setIsLoading(false)
                Log.d("Firebase : ", "Image uplaod issue")
            }

//        uploadTask.addOnProgressListener { taskSnapshot ->
//            val progress =
//                100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
//
//            println("Upload is $progress% done")
//            val currentprogress = progress.toInt()
//            progressBar.progress = currentprogress
//        }
    }
}