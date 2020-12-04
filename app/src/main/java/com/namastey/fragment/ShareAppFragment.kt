package com.namastey.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentFindFriendBinding
import com.namastey.databinding.FragmentShareAppBinding
import com.namastey.model.DashboardBean
import com.namastey.uiView.FindFriendView
import com.namastey.uiView.FollowingView
import com.namastey.utils.SessionManager
import com.namastey.viewModel.FindFriendViewModel
import com.namastey.viewModel.ShareAppViewModel
import javax.inject.Inject


class ShareAppFragment : BaseFragment<FragmentShareAppBinding>(), FindFriendView, FollowingView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var fragmentShareAppBinding: FragmentShareAppBinding
    private lateinit var shareAppViewModel: ShareAppViewModel
    private lateinit var layoutView: View

    override fun getViewModel() = shareAppViewModel

    override fun getLayoutId() = R.layout.fragment_share_app

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            FindFriendFragment().apply {

            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutView = view

        // Inflate the layout for this fragment
        shareAppViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ShareAppViewModel::class.java)
        shareAppViewModel.setViewInterface(this)

        fragmentShareAppBinding = getViewBinding()
        fragmentShareAppBinding.viewModel = shareAppViewModel
    }

    override fun onSuccessSuggestedList(suggestedList: ArrayList<DashboardBean>) {
    }

    override fun onSuccessSearchList(suggestedList: ArrayList<DashboardBean>) {
    }

    override fun onSuccess(list: ArrayList<DashboardBean>) {

    }


}