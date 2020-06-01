package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentFollowersBinding
import com.namastey.uiView.FollowersView
import com.namastey.viewModel.FollowersViewModel
import javax.inject.Inject

class FollowersFragment : BaseFragment<FragmentFollowersBinding>(), FollowersView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentFollowersBinding: FragmentFollowersBinding
    private lateinit var followersViewModel: FollowersViewModel
    private lateinit var layoutView: View


    override fun getViewModel() = followersViewModel

    override fun getLayoutId() = R.layout.fragment_followers

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

    }

    private fun setupViewModel() {
        followersViewModel = ViewModelProviders.of(this, viewModelFactory).get(
            FollowersViewModel::class.java
        )
        followersViewModel.setViewInterface(this)

        fragmentFollowersBinding = getViewBinding()
        fragmentFollowersBinding.viewModel = followersViewModel
    }

    override fun onDestroy() {
//        followersViewModel.onDestroy()
        super.onDestroy()
    }


}