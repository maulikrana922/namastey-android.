package com.namastey.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.MatchedProfileAdapter
import com.namastey.adapter.MessagesAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityMatchesBinding
import com.namastey.listeners.OnMatchesItemClick
import com.namastey.model.ChatMessage
import com.namastey.model.LikedUserCountBean
import com.namastey.model.MatchesListBean
import com.namastey.uiView.MatchesProfileView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.NotAvailableFeatureDialog
import com.namastey.utils.SessionManager
import com.namastey.viewModel.MatchesProfileViewModel
import kotlinx.android.synthetic.main.activity_matches.*
import kotlinx.android.synthetic.main.row_matches_profile_first.*
import kotlinx.android.synthetic.main.view_matches_horizontal_list.*
import kotlinx.android.synthetic.main.view_matches_messages_list.*
import java.util.*
import javax.inject.Inject

class MatchesActivity : BaseActivity<ActivityMatchesBinding>(), MatchesProfileView,
    OnMatchesItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    //private lateinit var matchesBasicViewModel: MatchesBasicViewModel
    private lateinit var matchesProfileViewModel: MatchesProfileViewModel
    private lateinit var activityMatchesProfileBinding: ActivityMatchesBinding

    private lateinit var tabOne: TextView
    private lateinit var tabTwo: TextView

    private var onClickMatches = false
    private var isFromDashboard = false
    private var userName = ""

    private lateinit var layoutView: View
    private lateinit var matchedProfileAdapter: MatchedProfileAdapter
    private lateinit var messagesAdapter: MessagesAdapter
    private var matchesListBean: ArrayList<MatchesListBean> = ArrayList()
    private var messageList: ArrayList<MatchesListBean> = ArrayList()

    //    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
//    private var myChatRef: DatabaseReference = database.reference
    private var likeUserCount = 0
    private var lastUserProfile = ""

    //    private lateinit var messageListListener: ValueEventListener
//    private lateinit var getMessageListQuery: Query
    private val db = Firebase.firestore
    private lateinit var docReference: DocumentReference
    private lateinit var listenerRegistration: ListenerRegistration


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        matchesProfileViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(MatchesProfileViewModel::class.java)
        activityMatchesProfileBinding = bindViewData()
        activityMatchesProfileBinding.viewModel = matchesProfileViewModel

        initData()
        initUI()
    }


    override fun onSuccessMatchesList(data: ArrayList<MatchesListBean>) {
        matchesListBean.clear()
        matchesListBean = data
        matchedProfileAdapter = MatchedProfileAdapter(matchesListBean, this, this)
        rvMatchesList.adapter = matchedProfileAdapter

    }

    override fun onSuccessMessageList(chatMessageList: ArrayList<MatchesListBean>) {
        messageList.clear()
        messagesAdapter.notifyDataSetChanged()
        messageList.addAll(chatMessageList)

        if (messageList.isEmpty()) {
            llNoMatch.visibility = View.VISIBLE
            tvMessages.visibility = View.GONE
            rvMessagesList.visibility = View.GONE
        } else {
            llNoMatch.visibility = View.GONE
            tvMessages.visibility = View.VISIBLE
            rvMessagesList.visibility = View.VISIBLE
        }

//        myChatRef = database.getReference(Constants.FirebaseConstant.CHATS)

        removeListeners()

        for (i in 0 until messageList.size) {
            getMessageFromFirebase(messageList[i].id, i)
        }
    }

    override fun onSuccessLikeUserCount(likedUserCountBean: LikedUserCountBean) {
        this.likeUserCount = likedUserCountBean.like_count
        this.lastUserProfile = likedUserCountBean.profile_pic

        tvLikesCount.text = likeUserCount.toString()

        GlideLib.loadImage(
            this,
            ivBackgroundPicture,
            lastUserProfile
        )
    }

    fun onClickNotification(view: View) {
        val intent = Intent(this@MatchesActivity, NotificationActivity::class.java)
        openActivityForResult(intent, 105)
//        openActivity(this@MatchesActivity, NotificationActivity())
    }

    private fun initData() {
//        setupViewPager()
//        tabMatchesProfile.setupWithViewPager(viewPagerMatchesProfile)
//        setupTabIcons()
        if (intent.hasExtra("isFromDashboard")) {
            isFromDashboard = intent.getBooleanExtra("isFromDashboard", false)
//            if (isFromDashboard)
//
//            else

        } else if (intent.hasExtra("onClickMatches")) {
            onClickMatches = intent.getBooleanExtra("onClickMatches", false)
//            userName = intent.getStringExtra("userName")!!
//            if (onClickMatches)
//                tabMatchesProfile.getTabAt(0)?.select()
//            else
//                tabMatchesProfile.getTabAt(1)?.select()
        } else if (intent.hasExtra("chatNotification")) {
            val intentChat = Intent(this@MatchesActivity, ChatActivity::class.java)
            intentChat.putExtra("isFromMessage", true)
            intentChat.putExtra("chatNotification", true)
            Log.d("Chat notification :", "8 pass")
            val matchesListBean = intent.getParcelableExtra<MatchesListBean>("matchesListBean")
            intentChat.putExtra("matchesListBean", matchesListBean)
            openActivity(intentChat)
        }

        if (intent.hasExtra("onFollowRequest")) {
            val onFollowRequest = intent.getBooleanExtra("onFollowRequest", false)
            if (onFollowRequest) {
                val intent = Intent(this@MatchesActivity, NotificationActivity::class.java)
                intent.putExtra("onFollowRequest", true)
                openActivity(intent)
            }
        }
    }

    override fun getViewModel() = matchesProfileViewModel

    override fun getLayoutId() = R.layout.activity_matches

    override fun getBindingVariable() = BR.viewModel

/*    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(MatchesProfileFragment(), resources.getString(R.string.matches))
        adapter.addFrag(NotificationFragment(), resources.getString(R.string.notification))
        viewPagerMatchesProfile.adapter = adapter
    }

    private fun setupTabIcons() {
        tabOne = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        tabOne.background =
            ContextCompat.getDrawable(this, R.drawable.rounded_bottom_left_red_solid)
        tabOne.text = resources.getString(R.string.matches)
        tabOne.setTextColor(Color.WHITE)
        tabMatchesProfile.getTabAt(0)?.customView = tabOne
        tabTwo = LayoutInflater.from(this).inflate(R.layout.custom_tab, null) as TextView
        tabTwo.text = resources.getString(R.string.notification)
        tabTwo.setTextColor(Color.GRAY)
        tabTwo.background = ContextCompat.getDrawable(this, R.drawable.rounded_top_right_pink_solid)
        tabMatchesProfile.getTabAt(1)?.customView = tabTwo

        tabMatchesProfile.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                changeSelectedTab(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })
    }*/

    /**
     * change background and text color according to select tab
     */
    private fun changeSelectedTab(position: Int) {

        if (position == 0) {
            tabOne.background =
                ContextCompat.getDrawable(this, R.drawable.rounded_bottom_left_red_solid)
            tabOne.setTextColor(Color.WHITE)
            tabTwo.background =
                ContextCompat.getDrawable(this, R.drawable.rounded_top_right_pink_solid)
            tabTwo.setTextColor(Color.GRAY)
        } else {
            tabOne.background =
                ContextCompat.getDrawable(this, R.drawable.rounded_bottom_left_pink_solid)
            tabOne.setTextColor(Color.GRAY)
            tabTwo.background =
                ContextCompat.getDrawable(this, R.drawable.rounded_top_right_red_solid)
            tabTwo.setTextColor(Color.WHITE)
        }
    }

    fun onClickMatchesBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            if (onClickMatches && userName != "") {
                val intent = Intent(this@MatchesActivity, ProfileViewActivity::class.java)
                intent.putExtra(Constants.USERNAME, userName)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                openActivity(intent)
            } else {
                finishActivity()

            }
        }
    }

    override fun onDestroy() {
        matchesProfileViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        if (::listenerRegistration.isInitialized)
            listenerRegistration.remove()
    }


    override fun onResume() {
        super.onResume()
        matchesProfileViewModel.getMatchesList()
        matchesProfileViewModel.getChatMessageList()
    }

    private fun initUI() {
        matchesProfileViewModel.getLikeUserCount()

        rlProfileMain.setOnClickListener {
                      object : NotAvailableFeatureDialog(
                          this,
                          getString(R.string.see_who_likes_not_available),
                          getString(R.string.alert_msg_feature_not_available), R.drawable.ic_heart_like
                      ) {
                          override fun onBtnClick(id: Int) {
                              dismiss()
                          }
                      }.show()

//            if (sessionManager.getIntegerValue(Constants.KEY_IS_PURCHASE) == 1) {
//                val intent = Intent(this, LikeProfileActivity::class.java)
//                intent.putExtra("likeUserCount", likeUserCount)
//                intent.putExtra("lastUserProfile", lastUserProfile)
//                openActivity(intent)
//            } else {
//                val intent = Intent(this, MemberActivity::class.java)
//                intent.putExtra(Constants.ACTIVITY_TYPE, "MatchesActivity")
//                startActivity(intent)
//            }
        }

        messagesAdapter = MessagesAdapter(messageList, this, this, sessionManager)
        rvMessagesList.adapter = messagesAdapter
    }

    override fun onMatchesItemClick(
        position: Int,
        matchesListBean: MatchesListBean,
        fromMessage: Boolean
    ) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("isFromMessage", fromMessage)
        intent.putExtra("matchesListBean", matchesListBean)
        Log.e("MatchesProfileFragment", "matchesListBean.id: ${matchesListBean.id}")
        if (matchesListBean.id == 0L) {
            intent.putExtra("isFromAdmin", true)
        } else {
            intent.putExtra("isFromAdmin", false)
        }
        openActivity(intent)
    }

    private fun getMessageFromFirebase(messageUserId: Long, position: Int) {

        val chatId = if (sessionManager.getUserId() < messageUserId)
            sessionManager.getUserId().toString().plus("_").plus(messageUserId)
        else
            messageUserId.toString().plus("_").plus(sessionManager.getUserId())

//        getMessageListQuery = myChatRef.child(chatId).orderByKey().limitToLast(1)
        db.clearPersistence()
        val docRef = db.collection(Constants.FirebaseConstant.MESSAGES)
            .document(chatId)
            .collection(Constants.FirebaseConstant.LAST_MESSAGE)
            .document(chatId)

        listenerRegistration = docRef.addSnapshotListener { document, error ->
            if (document != null) {
                Log.d("Success", "DocumentSnapshot data: ${document.data}")
                val chatMessage = document.toObject(ChatMessage::class.java)

                if (chatMessage != null) {
                    messageList[position].chatMessage = chatMessage
//                        messageList[position].timestamp =
//                            Utils.convertTimestampToChatFormat(chatMessage.timestamp)
//                        messageList[position].message = chatMessage.message
//                        messageList[position].url = chatMessage.url
                }
//                    messageList.sortByDescending { it.timestamp }
                messagesAdapter.notifyItemChanged(position)
            } else {
                Log.d("TAG", "No such document")
            }
        }
//            .addOnFailureListener { exception ->
//                Log.d("TAG", "get failed with ", exception)
//            }


//        messageListListener = getMessageListQuery.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (snapshot in dataSnapshot.children) {
//                    val chatMessage: ChatMessage = snapshot.getValue(ChatMessage::class.java)!!
//                    Log.d("Firebase Fragment :", "Value is: ${chatMessage.message}")
//
//                    messageList[position].timestamp =
//                        Utils.convertTimestampToChatFormat(chatMessage.timestamp)
//                    messageList[position].message = chatMessage.message
//                    messageList[position].url = chatMessage.url
//                }
//                messagesAdapter.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                // Failed to read value
//                Log.w("Firebase :", "Failed to read value.", error.toException())
//            }
//        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 106) {
            // Do nothing
        } else if (requestCode == 105) {
            finishActivity()
        }
    }

    private fun removeListeners() {
//        if (::messageListListener.isInitialized && ::getMessageListQuery.isInitialized){
//            getMessageListQuery.removeEventListener(messageListListener)

        if (::listenerRegistration.isInitialized)
            listenerRegistration.remove()
//        }
    }
}