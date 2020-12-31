package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.ChatActivity
import com.namastey.activity.LikeProfileActivity
import com.namastey.adapter.MatchedProfileAdapter
import com.namastey.adapter.MessagesAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentMatchesProfileBinding
import com.namastey.listeners.OnMatchesItemClick
import com.namastey.model.ChatMessage
import com.namastey.model.LikedUserCountBean
import com.namastey.model.MatchesListBean
import com.namastey.uiView.MatchesProfileView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.MatchesProfileViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.row_matches_profile_first.*
import kotlinx.android.synthetic.main.view_matches_horizontal_list.*
import kotlinx.android.synthetic.main.view_matches_messages_list.*
import javax.inject.Inject


class MatchesProfileFragment : BaseFragment<FragmentMatchesProfileBinding>(), MatchesProfileView,
    OnMatchesItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var fragmentAddFriendBinding: FragmentMatchesProfileBinding
    private lateinit var matchesProfileViewModel: MatchesProfileViewModel
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


    override fun getViewModel() = matchesProfileViewModel

    override fun getLayoutId() = R.layout.fragment_matches_profile

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() = MatchesProfileFragment().apply {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view
        setupViewModel()

        initUI()
    }

    private fun setupViewModel() {
        matchesProfileViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(MatchesProfileViewModel::class.java)
        matchesProfileViewModel.setViewInterface(this)

        fragmentAddFriendBinding = getViewBinding()
        fragmentAddFriendBinding.viewModel = matchesProfileViewModel
    }

    private fun initUI() {
        /* messagesAdapter = MessagesAdapter(matchesListBean, requireActivity(), this)
         rvMessagesList.adapter = messagesAdapter*/
        /*   messagesAdapter = MessagesAdapter(requireActivity(), this)
         rvMessagesList.adapter = messagesAdapter*/
        matchesProfileViewModel.getLikeUserCount()

        rlProfileMain.setOnClickListener {
            val intent = Intent(requireActivity(), LikeProfileActivity::class.java)
            intent.putExtra("likeUserCount", likeUserCount)
            intent.putExtra("lastUserProfile", lastUserProfile)
            openActivity(intent)
        }

        messagesAdapter = MessagesAdapter(messageList, requireActivity(), this,sessionManager)
        rvMessagesList.adapter = messagesAdapter
    }

    override fun onResume() {
        super.onResume()
        matchesProfileViewModel.getMatchesList()
        matchesProfileViewModel.getChatMessageList()
    }

    override fun onPause() {
        super.onPause()
        if (::listenerRegistration.isInitialized)
            listenerRegistration.remove()
    }
    override fun onMatchesItemClick(position: Int, matchesBean: MatchesListBean, fromMessage: Boolean) {
        val intent = Intent(requireActivity(), ChatActivity::class.java)
        intent.putExtra("isFromMessage", fromMessage)
        intent.putExtra("matchesListBean", matchesBean)
        openActivity(intent)
    }

    override fun onSuccessMatchesList(data: ArrayList<MatchesListBean>) {
        matchesListBean.clear()
        matchesListBean = data
        matchedProfileAdapter = MatchedProfileAdapter(matchesListBean, requireActivity(), this)
        rvMatchesList.adapter = matchedProfileAdapter

    }

    override fun onSuccessMessageList(chatMessageList: ArrayList<MatchesListBean>) {
        messageList.clear()
        messagesAdapter.notifyDataSetChanged()
        messageList.addAll(chatMessageList)

        if (messageList.isEmpty()) {
            ivNoMatch.visibility = View.VISIBLE
            tvMessages.visibility = View.GONE
            rvMessagesList.visibility = View.GONE
        } else {
            ivNoMatch.visibility = View.GONE
            tvMessages.visibility = View.VISIBLE
            rvMessagesList.visibility = View.VISIBLE
        }

//        myChatRef = database.getReference(Constants.FirebaseConstant.CHATS)

        removeListeners()

        for (i in 0 until messageList.size){
            getMessageFromFirebase(messageList[i].id, i)
        }
    }

    private fun removeListeners() {
//        if (::messageListListener.isInitialized && ::getMessageListQuery.isInitialized){
//            getMessageListQuery.removeEventListener(messageListListener)

            if (::listenerRegistration.isInitialized)
                listenerRegistration.remove()
//        }
    }

    private fun getMessageFromFirebase(messageUserId: Long, position: Int){

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
    override fun onSuccessLikeUserCount(likedUserCountBean: LikedUserCountBean) {
        this.likeUserCount = likedUserCountBean.like_count
        this.lastUserProfile = likedUserCountBean.profile_pic

        tvLikesCount.text = likeUserCount.toString()

        GlideLib.loadImage(
            requireContext(),
            ivBackgroundPicture,
            lastUserProfile
        )
    }

    override fun onDestroy() {
        matchesProfileViewModel.onDestroy()
        super.onDestroy()
    }
}