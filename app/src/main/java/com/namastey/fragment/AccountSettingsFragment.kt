package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentAccountSettingsBinding
import com.namastey.uiView.AccountSettingsView
import com.namastey.utils.Constants
import com.namastey.viewModel.AccountSettingsViewModel
import kotlinx.android.synthetic.main.fragment_account_settings.*
import javax.inject.Inject

class AccountSettingsFragment : BaseFragment<FragmentAccountSettingsBinding>(),
    AccountSettingsView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var fragmentAccountBinding: FragmentAccountSettingsBinding
    private lateinit var accountSettingsViewModel: AccountSettingsViewModel
    private lateinit var layoutView: View


    override fun getViewModel() = accountSettingsViewModel

    override fun getLayoutId() = R.layout.fragment_account_settings

    override fun getBindingVariable() = BR.viewModel


    companion object {
        fun getInstance() =
            AccountSettingsFragment().apply {
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

        initData()
    }

    private fun setupViewModel() {
        accountSettingsViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(AccountSettingsViewModel::class.java)
        fragmentAccountBinding = getViewBinding()
        fragmentAccountBinding.viewModel = accountSettingsViewModel
    }

    private fun initData() {

        tvNotifications.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.notifications))

            (activity as AccountSettingsActivity).addFragment(
                AccountSettingsNotificationFragment.getInstance(),
                Constants.ACCOUNT_SETTINGS_NOTIFICATIONS_FRAGMENT
            )
        }

        tvBlockedList.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.block_list))
            (activity as AccountSettingsActivity).addFragment(
                BlockListFragment.getInstance(),
                Constants.BLOCK_LIST_FRAGMENT
            )
        }

        tvManageAccount.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.manage_account))
            (activity as AccountSettingsActivity).addFragment(
                ManageAccountFragment.getInstance(),
                Constants.MANAGE_ACCOUNT_FRAGMENT
            )
        }

        tvContentLanguages.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.content_languages))
            (activity as AccountSettingsActivity).addFragment(
                ContentLanguageFragment.getInstance(),
                Constants.CONTENT_LANGUAGE_FRAGMENT
            )
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.account_settings))
    }

    override fun onDestroy() {
        accountSettingsViewModel.onDestroy()
        super.onDestroy()
    }
}