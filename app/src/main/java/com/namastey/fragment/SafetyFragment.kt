package com.namastey.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.AccountSettingsActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentSafetyBinding
import com.namastey.model.SafetyBean
import com.namastey.uiView.SafetyView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.SafetyViewModel
import kotlinx.android.synthetic.main.fragment_safety.*
import javax.inject.Inject

class SafetyFragment : BaseFragment<FragmentSafetyBinding>(), SafetyView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var fragmentSafetyBinding: FragmentSafetyBinding
    private lateinit var safetyViewModel: SafetyViewModel
    private lateinit var layoutView: View

    override fun getViewModel() = safetyViewModel

    override fun getLayoutId() = R.layout.fragment_safety

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            SafetyFragment().apply {
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

        setFromSessionManager()
    }

    private fun setupViewModel() {
        safetyViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SafetyViewModel::class.java)
        safetyViewModel.setViewInterface(this)
        fragmentSafetyBinding = getViewBinding()
        fragmentSafetyBinding.viewModel = safetyViewModel
    }

    private fun initData() {

        tvWhoCanSendYouDirectMsgEveryone.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.allow_messages_from))
            (activity as AccountSettingsActivity).addFragment(
                SafetySubFragment.getInstance(
                    1
                ), Constants.SAFETY_SUB_FRAGMENT
            )
        }

        tvWhoCanSeeYourFollowersEveryone.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.follower_visibility))
            (activity as AccountSettingsActivity).addFragment(
                SafetySubFragment.getInstance(
                    2
                ), Constants.SAFETY_SUB_FRAGMENT
            )
        }

        tvWhoCanCommentsOnYourVideosEveryone.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.allow_comments_from))
            (activity as AccountSettingsActivity).addFragment(
                SafetySubFragment.getInstance(
                    3
                ), Constants.SAFETY_SUB_FRAGMENT
            )
        }

        switchAllowYourVideoToDownload.setOnCheckedChangeListener { buttonView, isChecked ->
            when {
                isChecked -> {
                    safetyViewModel.idDownloadVideo(1)
                }
                else -> {
                    safetyViewModel.idDownloadVideo(0)
                }
            }
        }

        switchAllowYourToShareYourProfile.setOnCheckedChangeListener { buttonView, isChecked ->
            when {
                isChecked -> {
                    safetyViewModel.isShareProfileSafety(1)
                }
                else -> {
                    safetyViewModel.isShareProfileSafety(0)
                }
            }
        }
    }


    private fun setFromSessionManager() {
        switchAllowYourVideoToDownload.isChecked =
            sessionManager.getIntegerValue(Constants.KEY_IS_DOWNLOAD_VIDEO) == 1

        switchAllowYourToShareYourProfile.isChecked =
            sessionManager.getIntegerValue(Constants.KEY_IS_SHARE_PROFILE_SAFETY) == 1

        if (sessionManager.getIntegerValue(Constants.KEY_CAN_SEND_YOU_DIRECT_MESSAGE) == 0) {
            tvWhoCanSendYouDirectMsgEveryone.text = getString(R.string.everyone)
        } else if (sessionManager.getIntegerValue(Constants.KEY_CAN_SEND_YOU_DIRECT_MESSAGE) == 1) {
            tvWhoCanSendYouDirectMsgEveryone.text = getString(R.string.followers)
        } else  if (sessionManager.getIntegerValue(Constants.KEY_CAN_SEND_YOU_DIRECT_MESSAGE) == 2) {
            tvWhoCanSendYouDirectMsgEveryone.text = getString(R.string.no_one)
        } else {
            tvWhoCanSendYouDirectMsgEveryone.text = getString(R.string.everyone)
        }

        if (sessionManager.getIntegerValue(Constants.KEY_IS_YOUR_FOLLOWERS) == 0) {
            tvWhoCanSeeYourFollowersEveryone.text = getString(R.string.everyone)
        } else if (sessionManager.getIntegerValue(Constants.KEY_IS_YOUR_FOLLOWERS) == 1) {
            tvWhoCanSeeYourFollowersEveryone.text = getString(R.string.followers)
        } else  if (sessionManager.getIntegerValue(Constants.KEY_IS_YOUR_FOLLOWERS) == 2) {
            tvWhoCanSeeYourFollowersEveryone.text = getString(R.string.no_one)
        } else {
            tvWhoCanSeeYourFollowersEveryone.text = getString(R.string.everyone)
        }

        if (sessionManager.getIntegerValue(Constants.KEY_CAN_COMMENT_YOUR_VIDEO) == 0) {
            tvWhoCanCommentsOnYourVideosEveryone.text = getString(R.string.everyone)
        } else if (sessionManager.getIntegerValue(Constants.KEY_CAN_COMMENT_YOUR_VIDEO) == 1) {
            tvWhoCanCommentsOnYourVideosEveryone.text = getString(R.string.followers)
        } else  if (sessionManager.getIntegerValue(Constants.KEY_CAN_COMMENT_YOUR_VIDEO) == 2) {
            tvWhoCanCommentsOnYourVideosEveryone.text = getString(R.string.no_one)
        } else {
            tvWhoCanCommentsOnYourVideosEveryone.text = getString(R.string.everyone)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.safety))
    }

    override fun onSuccessIsSuccessResponse(safetyBean: SafetyBean) {
        Log.e("SafetyFragment", "onSuccessResponse safetyBean:\t ${safetyBean.is_download}")
        sessionManager.setIntegerValue(
            safetyBean.is_download, Constants.KEY_IS_DOWNLOAD_VIDEO
        )
    }

    override fun onSuccessShareProfileSafetyResponse(safetyBean: SafetyBean) {
        Log.e("SafetyFragment", "onSuccessResponse safetyBean:\t ${safetyBean.is_share}")
        sessionManager.setIntegerValue(
            safetyBean.is_share, Constants.KEY_IS_SHARE_PROFILE_SAFETY
        )
    }

    override fun onDestroy() {
        safetyViewModel.onDestroy()
        super.onDestroy()
    }
}