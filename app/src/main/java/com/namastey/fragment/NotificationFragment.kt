package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentNotificationBinding
import com.namastey.uiView.NotificationView
import com.namastey.viewModel.NotificationViewModel
import javax.inject.Inject


class NotificationFragment : BaseFragment<FragmentNotificationBinding>(), NotificationView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentAddFriendBinding: FragmentNotificationBinding
    private lateinit var notificationViewModel: NotificationViewModel
    private lateinit var layoutView: View

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
    }

    private fun setupViewModel() {
        notificationViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(NotificationViewModel::class.java)
        notificationViewModel.setViewInterface(this)

        fragmentAddFriendBinding = getViewBinding()
        fragmentAddFriendBinding.viewModel = notificationViewModel
    }


}