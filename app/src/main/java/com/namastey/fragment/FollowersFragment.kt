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
import com.namastey.model.ProfileBean
import com.namastey.uiView.FollowingView
import com.namastey.viewModel.FollowingViewModel
import kotlinx.android.synthetic.main.fragment_following.*
import javax.inject.Inject

class FollowersFragment : BaseFragment<FragmentFollowingBinding>(), FollowingView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentFollowersBinding: FragmentFollowingBinding
    private lateinit var followersViewModel: FollowingViewModel
    private lateinit var layoutView: View
    private var followingList: ArrayList<ProfileBean> = ArrayList()
    private lateinit var followingAdapter: FollowingAdapter

    override fun onSuccess(list: ArrayList<ProfileBean>) {
        if (list.size == 0) {
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
        followingAdapter = FollowingAdapter(list, activity!!)
        rvFollowing.adapter = followingAdapter
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
        followersViewModel.getFollowersList()
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


}