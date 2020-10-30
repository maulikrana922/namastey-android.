package com.namastey.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.MatchesActivity
import com.namastey.adapter.NotificationAdapter
import com.namastey.dagger.module.GlideApp
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentNotificationBinding
import com.namastey.model.FollowRequestBean
import com.namastey.uiView.NotificationView
import com.namastey.utils.Constants
import com.namastey.viewModel.NotificationViewModel
import kotlinx.android.synthetic.main.fragment_notification.*
import javax.inject.Inject


class NotificationFragment : BaseFragment<FragmentNotificationBinding>(), NotificationView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentAddFriendBinding: FragmentNotificationBinding
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var layoutView: View
    private lateinit var notificationAdapter: NotificationAdapter


    override fun getViewModel() = notificationViewModel

    override fun getLayoutId() = R.layout.fragment_notification

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

    private fun setupViewModel() {
        notificationViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(NotificationViewModel::class.java)
        notificationViewModel.setViewInterface(this)

        fragmentAddFriendBinding = getViewBinding()
        fragmentAddFriendBinding.viewModel = notificationViewModel
    }

    private fun initUI() {

        notificationViewModel.getFollowRequestList()

        notificationAdapter = NotificationAdapter(requireActivity())
        rvNotification.adapter = notificationAdapter

        tvFollowRequest.setOnClickListener {
            val followRequestFragment = FollowRequestFragment.getInstance()
            followRequestFragment.setTargetFragment(this, Constants.REQUEST_CODE)
            (activity as MatchesActivity).addFragment(
                followRequestFragment,
                Constants.FOLLOW_REQUEST_FRAGMENT
            )
        }

    }

    override fun onSuccessFollowRequest(data: ArrayList<FollowRequestBean>) {
        Log.e("NotificationFragment", "onSuccessFollowRequest: \t data: ${data.size}")
        if (data.get(0).profile_url != null && data.get(0).profile_url.isNotEmpty()) {
            // GlideLib.loadImage(requireContext(), ivFollowRequestFirst, data.get(0).profile_url)
            Log.e("NotificationFragment", "profile: \t data: ${data.get(0).profile_url}")
            GlideApp
                .with(requireContext())
                .load(data.get(0).profile_url)
                .into(ivFollowRequestFirst)
        } else {
            GlideApp
                .with(requireContext())
                .load(ContextCompat.getDrawable(requireContext(), R.drawable.default_placeholder))
                .into(ivFollowRequestFirst)
        }

        if (data.get(1).profile_url != null && data.get(1).profile_url.isNotEmpty()) {
            Log.e("NotificationFragment", "profile: \t data: ${data[2].profile_url}")
            GlideApp
                .with(requireContext())
                .load(data.get(1).profile_url)
                .into(ivFollowRequestSecond)
        } else {
            GlideApp
                .with(requireContext())
                .load(ContextCompat.getDrawable(requireContext(), R.drawable.default_placeholder))
                .into(ivFollowRequestSecond)
        }

        if (data.get(2).profile_url != null && data.get(2).profile_url.isNotEmpty()) {
            Log.e("NotificationFragment", "profile: \t data: ${data.get(2).profile_url}")
            GlideApp
                .with(requireContext())
                .load(data.get(2).profile_url)
                .into(ivFollowRequestThird)
        } else {
            GlideApp
                .with(requireContext())
                .load(ContextCompat.getDrawable(requireContext(), R.drawable.default_placeholder))
                .into(ivFollowRequestThird)
        }

    }

    override fun onDestroy() {
        notificationViewModel.onDestroy()
        super.onDestroy()
    }

}