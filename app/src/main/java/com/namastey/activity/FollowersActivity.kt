package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.FollowingAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityFollowingBinding
import com.namastey.listeners.OnFollowItemClick
import com.namastey.model.DashboardBean
import com.namastey.model.ProfileBean
import com.namastey.uiView.FollowingView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.FollowingViewModel
import kotlinx.android.synthetic.main.activity_following.*
import java.util.*
import javax.inject.Inject

class FollowersActivity : BaseActivity<ActivityFollowingBinding>(),
    FollowingView,
    OnFollowItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityFollowersBinding: ActivityFollowingBinding
    private lateinit var followersViewModel: FollowingViewModel
    private var followersList: ArrayList<DashboardBean> = ArrayList()
    private lateinit var followingAdapter: FollowingAdapter
    private var position = -1
    private var userId: Long = -1
    private var isMyProfile = false
    private var userName = ""
    private var profileBean = ProfileBean()
    private var title: String? = null
    private lateinit var activityName: String


    override fun getViewModel() = followersViewModel

    override fun getLayoutId() = R.layout.activity_following

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getActivityComponent().inject(this)

        followersViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            FollowingViewModel::class.java
        )
        followersViewModel.setViewInterface(this)

        activityFollowersBinding = bindViewData()
        activityFollowersBinding.viewModel = followersViewModel
        initData()
    }

    override fun onBackPressed() {
        finishActivity()
    }


    fun onClickProfileBack(view: View) {
        onBackPressed()
    }

    private fun initData() {

        profileBean = intent.getParcelableExtra<ProfileBean>(Constants.PROFILE_BEAN) as ProfileBean

        activityName = intent.getStringExtra("title").toString()

        title = intent.getStringExtra("title")
        if (title.equals("Follower")) {
            headertitle.setText("Followers")
        } else {
            headertitle.setText("Following")

        }

        if (intent.hasExtra("isMyProfile"))
            isMyProfile = intent.getBooleanExtra("isMyProfile", false)

        userId = profileBean.user_id
        userName = profileBean.username
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

        if (::followingAdapter.isInitialized)
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
        val intent = Intent(this, ProfileViewActivity::class.java)
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
            setEmptyMsg()
            llEmpty.visibility = View.VISIBLE
            rvSearchUser.visibility = View.GONE
        }
    }

    override fun onSuccessStartChat(msg: String) {

    }

    override fun onSuccess(list: ArrayList<DashboardBean>) {
        followersList = list
        if (followersList.size == 0) {
            tvEmptyFollow.text = getString(R.string.nofollowers)
            setEmptyMsg()
            llEmpty.visibility = View.VISIBLE
            rvSearchUser.visibility = View.GONE
        } else {
            llEmpty.visibility = View.GONE
            rvSearchUser.visibility = View.VISIBLE
            /* rvSearchUser.addItemDecoration(
                 DividerItemDecoration(
                     applicationContext,
                     LinearLayoutManager.VERTICAL
                 )
             )*/
        }
        followingAdapter = FollowingAdapter(
            followersList,
            this,
            false,
            this,
            sessionManager.getUserId(),
            isMyProfile,
            activityName
        )
        rvSearchUser.adapter = followingAdapter


    }

    fun setEmptyMsg() {
        if (isMyProfile) {
            tvEmptyFollowMsg.text = String.format(
                getString(R.string.msg_empty_followers),
                getString(R.string.you)
            )
            tvEmptyFollow.text = String.format(
                getString(R.string.msg_empty_followers_title),
                getString(R.string.you)
            )
        } else {
            tvEmptyFollowMsg.text = String.format(
                getString(R.string.msg_empty_followers),
                userName
            )
            tvEmptyFollow.text =
                String.format(getString(R.string.msg_empty_followers_title), userName)
        }
    }

}