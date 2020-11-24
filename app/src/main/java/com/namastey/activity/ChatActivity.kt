package com.namastey.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.firebase.database.*
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
import com.namastey.viewModel.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat.*
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

            myChatRef = database.getReference(Constants.FirebaseTable.CHATS)
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
        if (isHide == true) {
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
            sendMessage(edtMessage.text.toString())
        }else{
            Toast.makeText(this@ChatActivity, "Please enter message",Toast.LENGTH_SHORT).show()
        }
    }
    private fun sendMessage(message: String){

        val chatMessage = ChatMessage(message,sessionManager.getUserId(),matchesListBean.id)
        myChatRef.push().setValue(chatMessage).addOnSuccessListener {
            edtMessage.setText("")
        }
    }
}