package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentAccountSettingsNotificationBinding
import com.namastey.uiView.AccountSettingsNotificationView
import com.namastey.viewModel.AccountSettingsNotificationViewModel
import javax.inject.Inject


class AccountSettingsNotificationFragment :
    BaseFragment<FragmentAccountSettingsNotificationBinding>(),
    AccountSettingsNotificationView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentAccountSettingsBinding: FragmentAccountSettingsNotificationBinding
    private lateinit var accountSettingsNotificationViewModel: AccountSettingsNotificationViewModel
    private lateinit var layoutView: View


    override fun getViewModel() = accountSettingsNotificationViewModel

    override fun getLayoutId() = R.layout.fragment_account_settings_notification

    override fun getBindingVariable() = BR.viewModel


    companion object {
        fun getInstance() =
            AccountSettingsNotificationFragment().apply {
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
    }

    private fun setupViewModel() {
        accountSettingsNotificationViewModel =
            ViewModelProviders.of(this, viewModelFactory)
                .get(AccountSettingsNotificationViewModel::class.java)
        fragmentAccountSettingsBinding = getViewBinding()
        fragmentAccountSettingsBinding.viewModel = accountSettingsNotificationViewModel
    }

    override fun onResume() {
        super.onResume()
        (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.notifications))
    }

    override fun onDestroy() {
        accountSettingsNotificationViewModel.onDestroy()
        super.onDestroy()
    }

}