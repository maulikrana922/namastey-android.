package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.FollowingAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentFollowingBinding
import com.namastey.listeners.OnFollowItemClick
import com.namastey.model.DashboardBean
import com.namastey.uiView.FollowingView
import com.namastey.utils.Constants
import com.namastey.viewModel.FollowingViewModel
import kotlinx.android.synthetic.main.fragment_following.*
import javax.inject.Inject

class FollowersFragment : BaseFragment<FragmentFollowingBinding>(), FollowingView,OnFollowItemClick {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentFollowersBinding: FragmentFollowingBinding
    private lateinit var followersViewModel: FollowingViewModel
    private lateinit var layoutView: View
    private var followersList: ArrayList<DashboardBean> = ArrayList()
    private lateinit var followingAdapter: FollowingAdapter
    private var position = -1
    private var userId: Long = -1

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
        followingAdapter = FollowingAdapter(followersList, requireActivity(),false,this)
        rvFollowing.adapter = followingAdapter
    }

    companion object {
        fun getInstance(userId: Long) =
            FollowersFragment().apply {
                arguments = Bundle().apply {
                    putLong(Constants.USER_ID, userId)
                }
            }
    }
    override fun getViewModel() = followersViewModel

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
        followersViewModel.getFollowersList(userId)
    }

    private fun setupViewModel() {
        followersViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            FollowingViewModel::class.java
        )
        followersViewModel.setViewInterface(this)

        fragmentFollowersBinding = getViewBinding()
        fragmentFollowersBinding.viewModel = followersViewModel
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
        followersViewModel.removeFollowUser(userId,isFollow)
    }

    override fun onSuccess(msg: String) {
//        super.onSuccess(msg)
        followersList.removeAt(position)
        followingAdapter.notifyItemRemoved(position)
        followingAdapter.notifyItemRangeChanged(position, followingAdapter.itemCount)

        if (followersList.size == 0) {
            tvEmptyFollow.text = getString(R.string.followers)
            tvEmptyFollowMsg.text = getString(R.string.msg_empty_following)
            llEmpty.visibility = View.VISIBLE
            rvFollowing.visibility = View.GONE
        }
    }
}