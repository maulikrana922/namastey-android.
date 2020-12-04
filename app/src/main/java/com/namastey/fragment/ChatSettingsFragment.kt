package com.namastey.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.ChatActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentChatSettingsBinding
import com.namastey.model.MatchesListBean
import com.namastey.uiView.ChatBasicView
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.CustomCommonAlertDialog
import com.namastey.viewModel.ChatViewModel
import kotlinx.android.synthetic.main.dialog_bottom_report.*
import kotlinx.android.synthetic.main.dialog_common_alert.*
import kotlinx.android.synthetic.main.fragment_chat_settings.*
import javax.inject.Inject

class ChatSettingsFragment : BaseFragment<FragmentChatSettingsBinding>(), ChatBasicView {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var fragmentChatSettingsBinding: FragmentChatSettingsBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var layoutView: View
    private var matchesListBean: MatchesListBean? = null
    private lateinit var bottomSheetDialogReport: BottomSheetDialog

    override fun getViewModel() = chatViewModel

    override fun getLayoutId() = R.layout.fragment_chat_settings

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(matchesListBean: MatchesListBean) =
            ChatSettingsFragment().apply {
                this.matchesListBean = matchesListBean
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
        Log.e("ChatSettings", "matchesListBean: \t ${matchesListBean!!.id}")
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

    override fun onResume() {
        super.onResume()
        (activity as ChatActivity).hideChatMoreButton(true)
    }

    private fun dialogBlockUser() {
        object : CustomCommonAlertDialog(
            requireActivity(),
            matchesListBean!!.username,
            getString(R.string.msg_block_user),
            matchesListBean!!.profile_pic,
            getString(R.string.block_user),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        dismiss()
                        chatViewModel.blockUser(matchesListBean!!.id, 1)
                    }
                }
            }
        }.show()
    }

    private fun dialogReportUser() {
        object : CustomCommonAlertDialog(
            requireActivity(),
            matchesListBean!!.username,
            getString(R.string.msg_report_user),
            matchesListBean!!.profile_pic,
            getString(R.string.report_user),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        // chatViewModel.reportUser(matchesListBean!!.id, "")
                        dismiss()
                        openReportBottomSheet()
                    }
                }
            }
        }.show()
    }

    private fun openReportBottomSheet() {
        bottomSheetDialogReport = BottomSheetDialog(requireContext(), R.style.dialogStyle)
        bottomSheetDialogReport.setContentView(
            layoutInflater.inflate(
                R.layout.dialog_bottom_report,
                null
            )
        )
        bottomSheetDialogReport.window?.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialogReport.window?.attributes?.windowAnimations = R.style.DialogAnimation
        bottomSheetDialogReport.setCancelable(true)

        bottomSheetDialogReport.tvReportCancel.setOnClickListener {
            bottomSheetDialogReport.dismiss()
        }

        bottomSheetDialogReport.tvReportSpam.setOnClickListener {
            bottomSheetDialogReport.dismiss()
            chatViewModel.reportUser(matchesListBean!!.id, getString(R.string.its_spam))
        }

        bottomSheetDialogReport.tvReportInappropriate.setOnClickListener {
            bottomSheetDialogReport.dismiss()
            chatViewModel.reportUser(matchesListBean!!.id, getString(R.string.its_inappropriate))
        }

        bottomSheetDialogReport.show()
    }

    private fun dialogMatchDeleteUser() {
        object : CustomCommonAlertDialog(
            requireActivity(),
            matchesListBean!!.username,
            getString(R.string.msg_delete_matches),
            matchesListBean!!.profile_pic,
            getString(R.string.delete_user),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        chatViewModel.deleteMatches(matchesListBean!!.id)
                        dismiss()
                    }
                }
            }
        }.show()

    }

    override fun onSuccessReport(msg: String) {
        Log.e("ChatSetting", "onSuccessReport: \t msg:  $msg")
        object : CustomAlertDialog(
            requireActivity(),
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
            }
        }.show()
        // bottomSheetDialogReport.dismiss()
    }

    override fun onSuccessBlockUser(msg: String) {
        Log.e("ChatSetting", "onSuccessBlockUser: \t msg:  $msg")

        object : CustomAlertDialog(
            requireActivity(),
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
            }
        }.show()
    }

    override fun onSuccessDeleteMatches(msg: String) {
        Log.e("ChatSetting", "onSuccessDeleteMatches: \t msg:  $msg")

        object : CustomAlertDialog(
            requireActivity(),
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
                finishActivity(requireActivity())
            }
        }.show()
    }

    override fun onDestroy() {
        chatViewModel.onDestroy()
        super.onDestroy()
    }
}