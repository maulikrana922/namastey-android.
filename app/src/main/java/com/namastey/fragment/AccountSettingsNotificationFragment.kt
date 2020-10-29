package com.namastey.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentAccountSettingsNotificationBinding
import com.namastey.model.NotificationOnOffBean
import com.namastey.uiView.AccountSettingsNotificationView
import com.namastey.utils.Constants
import com.namastey.viewModel.AccountSettingsNotificationViewModel
import kotlinx.android.synthetic.main.fragment_account_settings_notification.*
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

        initData()
    }

    private fun setupViewModel() {
        accountSettingsNotificationViewModel =
            ViewModelProviders.of(this, viewModelFactory)
                .get(AccountSettingsNotificationViewModel::class.java)
        accountSettingsNotificationViewModel.setViewInterface(this)
        fragmentAccountSettingsBinding = getViewBinding()
        fragmentAccountSettingsBinding.viewModel = accountSettingsNotificationViewModel
    }

    private fun initData() {
        switchMatches.setOnCheckedChangeListener { buttonView, isChecked ->
            when {
                isChecked -> {
                    accountSettingsNotificationViewModel.onNotificationOnOff(onNotificationOnOff())
                }
                else -> {
                    accountSettingsNotificationViewModel.onNotificationOnOff(onNotificationOnOff())
                }

            }
        }

        switchComments.setOnCheckedChangeListener { buttonView, isChecked ->
            when {
                isChecked -> {
                    accountSettingsNotificationViewModel.onNotificationOnOff(onNotificationOnOff())
                }
                else -> {
                    accountSettingsNotificationViewModel.onNotificationOnOff(onNotificationOnOff())
                }

            }
        }

        switchNewFollowers.setOnCheckedChangeListener { buttonView, isChecked ->
            when {
                isChecked -> {
                    accountSettingsNotificationViewModel.onNotificationOnOff(onNotificationOnOff())
                }
                else -> {
                    accountSettingsNotificationViewModel.onNotificationOnOff(onNotificationOnOff())
                }

            }
        }

        switchMentions.setOnCheckedChangeListener { buttonView, isChecked ->
            when {
                isChecked -> {
                    accountSettingsNotificationViewModel.onNotificationOnOff(onNotificationOnOff())
                }
                else -> {
                    accountSettingsNotificationViewModel.onNotificationOnOff(onNotificationOnOff())
                }

            }
        }

        switchVideosSuggestions.setOnCheckedChangeListener { buttonView, isChecked ->
            when {
                isChecked -> {
                    accountSettingsNotificationViewModel.onNotificationOnOff(onNotificationOnOff())
                }
                else -> {
                    accountSettingsNotificationViewModel.onNotificationOnOff(onNotificationOnOff())
                }

            }
        }
    }

    private fun onNotificationOnOff(): JsonObject {
        val jsonObject = JsonObject()

        if (switchMatches.isChecked) {
            jsonObject.addProperty(Constants.IS_MATCHES, 1)
        } else {
            jsonObject.addProperty(Constants.IS_MATCHES, 0)
        }

        if (switchComments.isChecked) {
            jsonObject.addProperty(Constants.IS_COMMENT, 1)
        } else {
            jsonObject.addProperty(Constants.IS_COMMENT, 0)
        }

        if (switchNewFollowers.isChecked) {
            jsonObject.addProperty(Constants.IS_FOLLOW, 1)
        } else {
            jsonObject.addProperty(Constants.IS_FOLLOW, 0)
        }

        if (switchMentions.isChecked) {
            jsonObject.addProperty(Constants.IS_MENTIONS, 1)
        } else {
            jsonObject.addProperty(Constants.IS_MENTIONS, 0)
        }

        if (switchVideosSuggestions.isChecked) {

            jsonObject.addProperty(Constants.IS_SUGGEST, 1)
        } else {

            jsonObject.addProperty(Constants.IS_SUGGEST, 0)
        }

        return jsonObject
    }

    override fun onResume() {
        super.onResume()
        (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.notifications))
    }

    override fun onDestroy() {
        accountSettingsNotificationViewModel.onDestroy()
        super.onDestroy()
    }

    override fun onSuccessResponse(notificationOnOffBean: NotificationOnOffBean) {
        Log.e("SettingNotification", "onSuccessResponse: \t ${notificationOnOffBean.id}")
        Log.e("SettingNotification", "onSuccessResponse: \t ${notificationOnOffBean.is_matches}")
    }

}