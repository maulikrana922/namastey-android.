package com.namastey.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.e
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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
import kotlinx.android.synthetic.main.activity_followers.*
import kotlinx.android.synthetic.main.activity_following.*
import kotlinx.android.synthetic.main.activity_following.headertitle
import kotlinx.android.synthetic.main.activity_following.llEmpty
import kotlinx.android.synthetic.main.activity_following.rvSearchUser
import kotlinx.android.synthetic.main.activity_following.searchFollowFollowing
import kotlinx.android.synthetic.main.activity_following.tvEmptyFollow
import kotlinx.android.synthetic.main.activity_following.tvEmptyFollowMsg
import kotlinx.android.synthetic.main.activity_following_followers.*
import java.util.ArrayList
import javax.inject.Inject

class FollowingActivity : BaseActivity<ActivityFollowingBinding>(), FollowingView,
    OnFollowItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var activityFollowingbinding: ActivityFollowingBinding
    private lateinit var followingViewModel: FollowingViewModel
    private var followingList: ArrayList<DashboardBean> = ArrayList()
    private lateinit var followingAdapter: FollowingAdapter
    private var position = -1
    private var userId: Long = -1
    private var isMyProfile = false
    private var userName = ""
    private var profileBean = ProfileBean()


    override fun getViewModel() = followingViewModel

    override fun getLayoutId() = R.layout.activity_following

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        followingViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(FollowingViewModel::class.java)
        activityFollowingbinding = bindViewData()
        activityFollowingbinding.viewModel = followingViewModel
        initData()

    }

    override fun onBackPressed(){
        intent = Intent(this@FollowingActivity,ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }


    fun onClickProfileBack(view: View) {
        onBackPressed()
    }


    private fun initData() {

        profileBean = intent.getParcelableExtra<ProfileBean>(Constants.PROFILE_BEAN) as ProfileBean

        title = intent.getStringExtra("title")
        if (title.equals("Follower")) {
            headertitle.setText("Followers")
        } else {
            headertitle.setText("Following")
        }

        if (intent.hasExtra("isMyProfile"))
            isMyProfile = intent.getBooleanExtra("isMyProfile", false)
        if (!isMyProfile) {
            ivFollowFind.visibility = View.GONE
        }
        userId = profileBean.user_id
        userName = profileBean.username
        followingViewModel.getFollowingList(userId)
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

        if (::followingAdapter.isInitialized)
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
            tvEmptyFollow.text = getString(R.string.nofollowing)
            if (isMyProfile) {
                tvEmptyFollowMsg.text = String.format(
                    getString(R.string.msg_empty_followers_temp)
                )
            } else {
                tvEmptyFollowMsg.text = String.format(
                    getString(R.string.msg_empty_followers),
                    userName
                )
            }

            llEmpty.visibility = View.VISIBLE
            rvSearchUser.visibility = View.GONE
        }
    }

    override fun onSuccessStartChat(msg: String) {

    }

    override fun onUserItemClick(userId: Long) {
        val intent = Intent(this, ProfileViewActivity::class.java)
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
            if (isMyProfile) {
                tvEmptyFollowMsg.text = String.format(
                    getString(R.string.msg_empty_followers_temp)
                )
            } else {
                tvEmptyFollowMsg.text = String.format(
                    getString(R.string.msg_empty_followers),
                    userName
                )
            }
            llEmpty.visibility = View.VISIBLE
            rvSearchUser.visibility = View.GONE
        } else {
            llEmpty.visibility = View.GONE
            rvSearchUser.visibility = View.VISIBLE
            rvSearchUser.addItemDecoration(
                DividerItemDecoration(
                    applicationContext,
                    LinearLayoutManager.VERTICAL
                )
            )
        }
        followingAdapter = FollowingAdapter(
            followingList,
            this,
            true,
            this,
            sessionManager.getUserId(),
            isMyProfile
        )
        rvSearchUser.adapter = followingAdapter
    }
}