package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.database.*
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.ChatActivity
import com.namastey.adapter.MatchedProfileAdapter
import com.namastey.adapter.MessagesAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentMatchesProfileBinding
import com.namastey.listeners.OnMatchesItemClick
import com.namastey.model.ChatMessage
import com.namastey.model.MatchesListBean
import com.namastey.uiView.MatchesProfileView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.utils.SessionManager
import com.namastey.utils.Utils
import com.namastey.viewModel.MatchesProfileViewModel
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
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var myChatRef: DatabaseReference = database.reference
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
    }

    override fun onResume() {
        super.onResume()
        matchesProfileViewModel.getMatchesList()
    }

    override fun onMatchesItemClick(position: Int, matchesBean: MatchesListBean?) {
        if (matchesBean != null) {
//            Log.e("MatchesProfile", "onItemClick: \t matchesListBean: \t ${matchesListBean!!.id}")
            val intent = Intent(requireActivity(), ChatActivity::class.java)
            intent.putExtra("isFromProfile", false)
            intent.putExtra("matchesListBean", matchesBean)
            openActivity(intent)

//            if (matchesBean.is_read == 0){
//                val chatId = if (sessionManager.getUserId() < matchesBean.id)
//                    sessionManager.getUserId().toString().plus(matchesBean.id)
//                else
//                    matchesBean.id.toString().plus(sessionManager.getUserId())
//
//                matchesListBean[position].is_read = 1
//                matchedProfileAdapter.notifyItemChanged(position)
//
//                messageList.add(0,matchesBean)
//                messagesAdapter.notifyItemInserted(0)
//
//            }
        }
    }

    override fun onSuccessMatchesList(data: ArrayList<MatchesListBean>) {
//        Log.e("MatchesProfile", "onSuccessMatchesList: \t $data")

        matchesListBean.clear()
        matchesListBean = data
        if (matchesListBean.size <= 10) {
            tvLikesCount.text = matchesListBean.size.toString()
        } else {
            tvLikesCount.text = "10+"
        }

        if (matchesListBean.size == 0) {
            ivBackgroundPicture.setImageResource(R.drawable.default_album)
        } else {
            GlideLib.loadImage(
                requireContext(),
                ivBackgroundPicture,
                matchesListBean[0].profile_pic
            )
        }

        // tvLikesCount.text = matchesListBean.size.toString()
        matchedProfileAdapter = MatchedProfileAdapter(matchesListBean, requireActivity(), this)
        rvMatchesList.adapter = matchedProfileAdapter
        val messageListTemp: List<MatchesListBean> = matchesListBean.filter { s -> s.is_read == 1 }

        this.messageList = messageListTemp as ArrayList<MatchesListBean>
        if (messageList.isEmpty()) {
            ivNoMatch.visibility = View.VISIBLE
            tvMessages.visibility = View.GONE
            rvMessagesList.visibility = View.GONE
        }else{
            ivNoMatch.visibility = View.GONE
            tvMessages.visibility = View.VISIBLE
            rvMessagesList.visibility = View.VISIBLE
        }
        messagesAdapter = MessagesAdapter(this.messageList, requireActivity(), this)
        rvMessagesList.adapter = messagesAdapter

        myChatRef = database.getReference(Constants.FirebaseConstant.CHATS)

        messageList.forEachIndexed { index, matchedBean ->

            val chatId = if (sessionManager.getUserId() < matchedBean.id)
                sessionManager.getUserId().toString().plus(matchedBean.id)
            else
                matchedBean.id.toString().plus(sessionManager.getUserId())

            val lastQuery: Query = myChatRef.child(chatId).orderByKey().limitToLast(1)

            lastQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val chatMessage: ChatMessage = snapshot.getValue(ChatMessage::class.java)!!
                        Log.d("Firebase Fragment :", "Value is: ${chatMessage.message}")

                        messageList[index].timestamp =
                            Utils.convertTimestampToChatFormat(chatMessage.timestamp)
                        messageList[index].message = chatMessage.message
                        messageList[index].url = chatMessage.url
                    }
                    messagesAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Log.w("Firebase :", "Failed to read value.", error.toException())
                }
            })

        }
    }

    override fun onDestroy() {
        matchesProfileViewModel.onDestroy()
        super.onDestroy()
    }
}