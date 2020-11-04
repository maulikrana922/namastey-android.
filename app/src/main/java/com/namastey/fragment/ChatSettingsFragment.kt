package com.namastey.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentChatSettingsBinding
import com.namastey.uiView.ChatBasicView
import com.namastey.utils.CustomCommonAlertDialog
import com.namastey.viewModel.ChatViewModel
import kotlinx.android.synthetic.main.dialog_common_alert.*
import kotlinx.android.synthetic.main.fragment_chat_settings.*
import javax.inject.Inject

class ChatSettingsFragment : BaseFragment<FragmentChatSettingsBinding>(), ChatBasicView {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory


    private lateinit var fragmentChatSettingsBinding: FragmentChatSettingsBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var layoutView: View

    override fun getViewModel() = chatViewModel

    override fun getLayoutId() = R.layout.fragment_chat_settings

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance() =
            ChatSettingsFragment().apply {
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
        chatViewModel = ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)
        chatViewModel.setViewInterface(this)
        fragmentChatSettingsBinding = getViewBinding()
        fragmentChatSettingsBinding.viewModel = chatViewModel
    }

    private fun initData() {
        tvMatchDelete.setOnClickListener {
            dialogMatchDeleteUser()
        }

        tvReport.setOnClickListener {
            dialogReportUser()
        }

        tvBlock.setOnClickListener {
            dialogBlockUser()
        }
    }

    private fun dialogBlockUser() {
        object : CustomCommonAlertDialog(
            requireActivity(),
            "",
            getString(R.string.msg_block_user),
            "",
            getString(R.string.block_user),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        dismiss()
                    }
                }
            }
        }.show()

    }

    private fun dialogReportUser() {
        object : CustomCommonAlertDialog(
            requireActivity(),
            "",
            getString(R.string.msg_report_user),
            "",
            getString(R.string.report_user),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        dismiss()
                    }
                }
            }
        }.show()

    }

    private fun dialogMatchDeleteUser() {
        object : CustomCommonAlertDialog(
            requireActivity(),
            "",
            getString(R.string.msg_delete_user),
            "",
            getString(R.string.delete_user),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        dismiss()
                    }
                }
            }
        }.show()

    }

    override fun onDestroy() {
        chatViewModel.onDestroy()
        super.onDestroy()
    }

}