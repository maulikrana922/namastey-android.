package com.namastey.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.ProfileViewActivity
import com.namastey.adapter.FollowingAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentFollowingBinding
import com.namastey.listeners.OnFollowItemClick
import com.namastey.model.DashboardBean
import com.namastey.uiView.FollowingView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.FollowingViewModel
import kotlinx.android.synthetic.main.fragment_following.*
import java.util.*
import javax.inject.Inject

class FollowingFragment : BaseFragment<FragmentFollowingBinding>(), FollowingView,
    OnFollowItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var fragmentFollowingBinding: FragmentFollowingBinding
    private lateinit var followingViewModel: FollowingViewModel
    private lateinit var layoutView: View
    private var followingList: ArrayList<DashboardBean> = ArrayList()
    private lateinit var followingAdapter: FollowingAdapter
    private var position = -1
    private var userId: Long = -1
    private var isMyProfile = false
    private var userName = ""


    companion object {
        fun getInstance(userId: Long, isMyProfile: Boolean, userName: String) =
            FollowingFragment().apply {
                arguments = Bundle().apply {
                    putLong(Constants.USER_ID, userId)
                    putBoolean("isMyProfile", isMyProfile)
                    putString("userName", userName)
                }
            }
    }

    override fun getViewModel() = followingViewModel

    override fun getLayoutId() = R.layout.fragment_following

    override fun getBindingVariable() = BR.viewModel

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

    private fun initUI() {
        userId = arguments!!.getLong(Constants.USER_ID)
        isMyProfile = arguments!!.getBoolean("isMyProfile")
        userName = arguments!!.getString("userName", "")
        followingViewModel.getFollowingList(userId)
        searchFollowers()
    }

    private fun setupViewModel() {
        followingViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            FollowingViewModel::class.java
        )
        followingViewModel.setViewInterface(this)

        fragmentFollowingBinding = getViewBinding()
        fragmentFollowingBinding.viewModel = followingViewModel
    }

    private fun searchFollowers() {
        searchFollowFollowing.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.e("FollowersFragment", "onQueryTextSubmit: $query")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    Log.e("FollowersFragment", "onQueryTextChange: $newText")
                    rvSearchUser.visibility = View.VISIBLE
                    filter(newText.toString().trim())
                    // followingAdapter.filter!!.filter(newText.toString().trim())
                } else {
                    filter("")
                    followingViewModel.getFollowingList(userId)
                    //rvSearchUser.visibility = View.GONE
                }
                return true
            }
        })

        searchFollowFollowing.setOnCloseListener {
            filter("")
            followingViewModel.getFollowingList(userId)

            false
        }
    }

    private fun filter(text: String) {
        Log.e("FollowersFragment", "filter: text: $text")
        //new array list that will hold the filtered data
        val filteredName: ArrayList<DashboardBean> = ArrayList()

        for (followers in followingList) {
            if (followers.username.toLowerCase().contains(text.toLowerCase())) {
                filteredName.add(followers)
            }
        }

        followingAdapter.filterList(filteredName)
    }


    override fun onDestroy() {
        followingViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onSuccess(msg: String) {

        if (isMyProfile) {
            followingList.removeAt(position)
            followingAdapter.notifyItemRemoved(position)
            followingAdapter.notifyItemRangeChanged(position, followingAdapter.itemCount)
        } else {
            // If other user profile view and follow/unfollow then update position
            if (followingList[position].is_follow == 1)
                followingList[position].is_follow = 0
            else
                followingList[position].is_follow = 1

            followingAdapter.notifyItemChanged(position)
        }

        if (followingList.size == 0) {
            tvEmptyFollow.text = getString(R.string.following)
            if (isMyProfile){
                tvEmptyFollowMsg.text = String.format(
                    getString(R.string.msg_empty_followers_temp))
            }else{
                tvEmptyFollowMsg.text = String.format(
                    getString(R.string.msg_empty_followers),
                    userName
                )
            }

            llEmpty.visibility = View.VISIBLE
            rvFollowing.visibility = View.GONE
        }
    }

    override fun onSuccessStartChat(msg: String) {
        TODO("Not yet implemented")
    }

    override fun onUserItemClick(userId: Long) {
        val intent = Intent(requireActivity(), ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }

    override fun onItemRemoveFollowersClick(
        userId: Long,
        isFollow: Int,
        position: Int
    ) {
        this.position = position
        followingViewModel.followUser(userId, isFollow)
    }

    override fun onSuccess(list: ArrayList<DashboardBean>) {
        followingList = list
        if (followingList.size == 0) {
            tvEmptyFollow.text = getString(R.string.following)
            if (isMyProfile){
                tvEmptyFollowMsg.text = String.format(
                    getString(R.string.msg_empty_followers_temp))
            }else{
                tvEmptyFollowMsg.text = String.format(
                    getString(R.string.msg_empty_followers),
                    userName
                )
            }

            llEmpty.visibility = View.VISIBLE
            rvFollowing.visibility = View.GONE
        } else {
            llEmpty.visibility = View.GONE
            rvFollowing.visibility = View.VISIBLE
            rvFollowing.addItemDecoration(
                DividerItemDecoration(
                    context,
                    LinearLayoutManager.VERTICAL
                )
            )
        }
        followingAdapter = FollowingAdapter(
            followingList,
            requireActivity(),
            true,
            this,
            sessionManager.getUserId(),
            isMyProfile
        )
        rvFollowing.adapter = followingAdapter
    }

}