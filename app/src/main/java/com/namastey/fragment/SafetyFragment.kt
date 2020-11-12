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
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.who_can_send_you_direct_msg))
            (activity as AccountSettingsActivity).addFragment(
                SafetySubFragment.getInstance(
                    1
                ), Constants.SAFETY_SUB_FRAGMENT
            )
        }

        tvWhoCanSeeYourFollowersEveryone.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.who_can_see_your_followers))
            (activity as AccountSettingsActivity).addFragment(
                SafetySubFragment.getInstance(
                    2
                ), Constants.SAFETY_SUB_FRAGMENT
            )
        }

        tvWhoCanCommentsOnYourVideosEveryone.setOnClickListener {
            (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.who_can_comments_on_your_video))
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
    }


    private fun setFromSessionManager() {
        switchAllowYourVideoToDownload.isChecked =
            sessionManager.getIntegerValue(Constants.KEY_IS_DOWNLOAD_VIDEO) == 1
    }


    override fun onResume() {
        super.onResume()
        (activity as AccountSettingsActivity).changeHeaderText(getString(R.string.safety))
    }

    override fun onSuccessResponse(safetyBean: SafetyBean) {
        Log.e("SafetyFragment", "onSuccessResponse safetyBean:\t ${safetyBean.is_download}")
        sessionManager.setIntegerValue(
            safetyBean.is_download, Constants.KEY_IS_DOWNLOAD_VIDEO
        )
    }

    override fun onDestroy() {
        safetyViewModel.onDestroy()
        super.onDestroy()
    }
}