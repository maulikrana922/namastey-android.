package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.ChatActivity
import com.namastey.adapter.MatchedProfileAdapter
import com.namastey.adapter.MessagesAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentMatchesProfileBinding
import com.namastey.listeners.OnMatchesItemClick
import com.namastey.model.MatchesListBean
import com.namastey.uiView.MatchesProfileView
import com.namastey.utils.GlideLib
import com.namastey.viewModel.MatchesProfileViewModel
import kotlinx.android.synthetic.main.row_matches_profile_first.*
import kotlinx.android.synthetic.main.view_matches_horizontal_list.*
import kotlinx.android.synthetic.main.view_matches_messages_list.*
import javax.inject.Inject


class MatchesProfileFragment : BaseFragment<FragmentMatchesProfileBinding>(), MatchesProfileView,
    OnMatchesItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentAddFriendBinding: FragmentMatchesProfileBinding
    private lateinit var matchesProfileViewModel: MatchesProfileViewModel
    private lateinit var layoutView: View
    private lateinit var matchedProfileAdapter: MatchedProfileAdapter
    private lateinit var messagesAdapter: MessagesAdapter
    private var matchesListBean: ArrayList<MatchesListBean> = ArrayList()

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
    override fun onMatchesItemClick(value: Long, position: Int, matchesListBean: MatchesListBean?) {
        if (matchesListBean != null) {
            Log.e("MatchesProfile", "onItemClick: \t matchesListBean: \t ${matchesListBean!!.id}")

            val intent = Intent(requireActivity(), ChatActivity::class.java)
            intent.putExtra("matchesListBean", matchesListBean)
            openActivity(intent)
        }
    }

    override fun onSuccessMatchesList(data: ArrayList<MatchesListBean>) {
//        Log.e("MatchesProfile", "onSuccessMatchesList: \t $data")

        matchesListBean = data
        if (data.size <= 10) {
            tvLikesCount.text = data.size.toString()
        } else {
            tvLikesCount.text = "10+"
        }

        if (data.size == 0) {
            ivBackgroundPicture.setImageResource(R.drawable.default_album)
        } else {
            GlideLib.loadImage(
                requireContext(),
                ivBackgroundPicture,
                data[0].profile_pic
            )
        }

        // tvLikesCount.text = data.size.toString()
        matchedProfileAdapter = MatchedProfileAdapter(data, requireActivity(), this)
        rvMatchesList.adapter = matchedProfileAdapter
        val messageList : List<MatchesListBean> = data.filter { s -> s.is_read == 1 }

        if (messageList.size == 0){
            ivNoMatch.visibility = View.VISIBLE
            tvMessages.visibility = View.GONE
            rvMessagesList.visibility = View.GONE
        }else{
            ivNoMatch.visibility = View.GONE
            tvMessages.visibility = View.VISIBLE
            rvMessagesList.visibility = View.VISIBLE
        }
        messagesAdapter = MessagesAdapter(messageList, requireActivity(), this)
        rvMessagesList.adapter = messagesAdapter
    }

    override fun onDestroy() {
        matchesProfileViewModel.onDestroy()
        super.onDestroy()
    }
}