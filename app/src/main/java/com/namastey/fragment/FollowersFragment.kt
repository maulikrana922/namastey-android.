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

class FollowersFragment : BaseFragment<FragmentFollowingBinding>(), FollowingView,
    OnFollowItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var fragmentFollowersBinding: FragmentFollowingBinding
    private lateinit var followersViewModel: FollowingViewModel
    private lateinit var layoutView: View
    private var followersList: ArrayList<DashboardBean> = ArrayList()
    private lateinit var followingAdapter: FollowingAdapter
    private var position = -1
    private var userId: Long = -1
    private var isMyProfile = false


    override fun getViewModel() = followersViewModel

    override fun getLayoutId() = R.layout.fragment_following

    override fun getBindingVariable() = BR.viewModel


    companion object {
        fun getInstance(userId: Long, isMyProfile: Boolean) =
            FollowersFragment().apply {
                arguments = Bundle().apply {
                    putLong(Constants.USER_ID, userId)
                    putBoolean("isMyProfile", isMyProfile)
                }
            }
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
        followersViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            FollowingViewModel::class.java
        )
        followersViewModel.setViewInterface(this)

        fragmentFollowersBinding = getViewBinding()
        fragmentFollowersBinding.viewModel = followersViewModel
    }

    private fun initUI() {
        userId = arguments!!.getLong(Constants.USER_ID)
        isMyProfile = arguments!!.getBoolean("isMyProfile")

        followersViewModel.getFollowersList(userId)
        searchFollowers()
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
                   // rvSearchUser.visibility = View.VISIBLE
                    filter(newText.toString().trim())
                    // followingAdapter.filter!!.filter(newText.toString().trim())
                } else {
                    filter("")
                    followersViewModel.getFollowersList(userId)
                    //rvSearchUser.visibility = View.GONE
                }
                return true
            }
        })

        searchFollowFollowing.setOnCloseListener {
            filter("")
            followersViewModel.getFollowersList(userId)

            false
        }
    }

    private fun filter(text: String) {
        Log.e("FollowersFragment", "filter: text: $text")
        //new array list that will hold the filtered data
        val filteredName: ArrayList<DashboardBean> = ArrayList()

        for (followers in followersList) {
            if (followers.username.toLowerCase().contains(text.toLowerCase())) {
                filteredName.add(followers)
            }
        }

        followingAdapter.filterList(filteredName)
    }


    override fun onDestroy() {
        followersViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onItemRemoveFollowersClick(
        userId: Long,
        isFollow: Int,
        position: Int
    ) {
        this.position = position
        if (isMyProfile)
            followersViewModel.removeFollowUser(userId, isFollow)
        else
            followersViewModel.followUser(userId, isFollow)
    }

    override fun onUserItemClick(userId: Long) {
        val intent = Intent(requireActivity(), ProfileViewActivity::class.java)
        intent.putExtra(Constants.USER_ID, userId)
        openActivity(intent)
    }

    override fun onSuccess(msg: String) {

        if (isMyProfile) {
            followersList.removeAt(position)
            followingAdapter.notifyItemRemoved(position)
            followingAdapter.notifyItemRangeChanged(position, followingAdapter.itemCount)
        } else {
            if (followersList[position].is_follow == 1)
                followersList[position].is_follow = 0
            else
                followersList[position].is_follow = 1

            followingAdapter.notifyItemChanged(position)
        }

        if (followersList.size == 0) {
            tvEmptyFollow.text = getString(R.string.followers)
            tvEmptyFollowMsg.text = getString(R.string.msg_empty_following)
            llEmpty.visibility = View.VISIBLE
            rvFollowing.visibility = View.GONE
        }
    }

    override fun onSuccessStartChat(msg: String) {
        TODO("Not yet implemented")
    }

    override fun onSuccess(list: ArrayList<DashboardBean>) {
        followersList = list
        if (followersList.size == 0) {
            tvEmptyFollow.text = getString(R.string.followers)
            tvEmptyFollowMsg.text = getString(R.string.msg_empty_following)
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
            followersList,
            requireActivity(),
            false,
            this,
            sessionManager.getUserId(),
            isMyProfile
        )
        rvFollowing.adapter = followingAdapter


    }

}