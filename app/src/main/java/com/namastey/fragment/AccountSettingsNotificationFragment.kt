package com.namastey.fragment

import android.os.Bundle
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
import com.namastey.utils.SessionManager
import com.namastey.viewModel.AccountSettingsNotificationViewModel
import kotlinx.android.synthetic.main.fragment_account_settings_notification.*
import javax.inject.Inject


class AccountSettingsNotificationFragment :
    BaseFragment<FragmentAccountSettingsNotificationBinding>(),
    AccountSettingsNotificationView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
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

        setSelected()
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
        var jsonObject = JsonObject()

        switchMatches.setOnCheckedChangeListener { buttonView, isChecked ->
            jsonObject = JsonObject()
            when {
                isChecked -> {
                    jsonObject.addProperty(Constants.IS_MATCHES, 1)
                    accountSettingsNotificationViewModel.onNotificationOnOff(jsonObject)
                }
                else -> {
                    jsonObject.addProperty(Constants.IS_MATCHES, 0)
                    accountSettingsNotificationViewModel.onNotificationOnOff(jsonObject)
                }
            }
        }

        switchComments.setOnCheckedChangeListener { buttonView, isChecked ->
            jsonObject = JsonObject()
            when {
                isChecked -> {
                    jsonObject.addProperty(Constants.IS_COMMENT, 1)
                    accountSettingsNotificationViewModel.onNotificationOnOff(jsonObject)
                }
                else -> {
                    jsonObject.addProperty(Constants.IS_COMMENT, 0)
                    accountSettingsNotificationViewModel.onNotificationOnOff(jsonObject)
                }
            }
        }

        switchNewFollowers.setOnCheckedChangeListener { buttonView, isChecked ->
            jsonObject = JsonObject()
            when {
                isChecked -> {
                    jsonObject.addProperty(Constants.IS_FOLLOW, 1)
                    accountSettingsNotificationViewModel.onNotificationOnOff(jsonObject)
                }
                else -> {
                    jsonObject.addProperty(Constants.IS_FOLLOW, 0)
                    accountSettingsNotificationViewModel.onNotificationOnOff(jsonObject)
                }

            }
        }

        switchMentions.setOnCheckedChangeListener { buttonView, isChecked ->
            jsonObject = JsonObject()
            when {
                isChecked -> {
                    jsonObject.addProperty(Constants.IS_MENTIONS, 1)
                    accountSettingsNotificationViewModel.onNotificationOnOff(jsonObject)
                }
                else -> {
                    jsonObject.addProperty(Constants.IS_MENTIONS, 0)
                    accountSettingsNotificationViewModel.onNotificationOnOff(jsonObject)
                }
            }
        }

        switchVideosSuggestions.setOnCheckedChangeListener { buttonView, isChecked ->
            jsonObject = JsonObject()
            when {
                isChecked -> {
                    jsonObject.addProperty(Constants.IS_SUGGEST, 1)
                    accountSettingsNotificationViewModel.onNotificationOnOff(jsonObject)
                }
                else -> {
                    jsonObject.addProperty(Constants.IS_SUGGEST, 0)
                    accountSettingsNotificationViewModel.onNotificationOnOff(jsonObject)
                }

            }
        }
    }

    private fun setSelected() {
        switchMentions.isChecked = sessionManager.getIntegerValue(Constants.KEY_IS_MENTIONS) == 1

        switchMatches.isChecked = sessionManager.getIntegerValue(Constants.KEY_IS_MATCHES) == 1

        switchComments.isChecked = sessionManager.getIntegerValue(Constants.KEY_IS_COMMENTS) == 1

        switchNewFollowers.isChecked = sessionManager.getIntegerValue(Constants.KEY_IS_NEW_FOLLOWERS) == 1

        switchVideosSuggestions.isChecked = sessionManager.getIntegerValue(Constants.KEY_IS_VIDEO_SUGGESTIONS) == 1

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

//        if (notificationOnOffBean.is_mentions == 1) {
            sessionManager.setIntegerValue(
                notificationOnOffBean.is_mentions,
                Constants.KEY_IS_MENTIONS
            )
//        } else {
//            sessionManager.setIntegerValue(
//                0,
//                Constants.KEY_IS_MENTIONS
//            )
//        }

//        if (notificationOnOffBean.is_matches == 1) {
            sessionManager.setIntegerValue(
                notificationOnOffBean.is_matches, Constants.KEY_IS_MATCHES
            )
//        } else {
//            sessionManager.setIntegerValue(
//                0,
//                Constants.KEY_IS_MATCHES
//            )
//        }

//        if (notificationOnOffBean.is_follow == 1) {
            sessionManager.setIntegerValue(
                notificationOnOffBean.is_follow,
                Constants.KEY_IS_NEW_FOLLOWERS
            )
//        } else {
//            sessionManager.setIntegerValue(
//                0,
//                Constants.KEY_IS_NEW_FOLLOWERS
//            )
//        }

//        if (notificationOnOffBean.is_comment == 1) {
            sessionManager.setIntegerValue(
                notificationOnOffBean.is_comment,
                Constants.KEY_IS_COMMENTS
            )
//        } else {
//            sessionManager.setIntegerValue(
//                0,
//                Constants.KEY_IS_COMMENTS
//            )
//        }

//        if (notificationOnOffBean.is_suggest == 1) {
            sessionManager.setIntegerValue(
                notificationOnOffBean.is_suggest,
                Constants.KEY_IS_VIDEO_SUGGESTIONS
            )
//        } else {
//            sessionManager.setIntegerValue(
//                0,
//                Constants.KEY_IS_VIDEO_SUGGESTIONS
//            )
//        }
    }

}