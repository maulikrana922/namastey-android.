package com.namastey.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.namastey.BR
import com.namastey.R
import com.namastey.activity.ChatActivity
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.FragmentChatSettingsBinding
import com.namastey.model.ChatMessage
import com.namastey.model.MatchesListBean
import com.namastey.model.SuperMessageBean
import com.namastey.uiView.ChatBasicView
import com.namastey.utils.Constants
import com.namastey.utils.CustomAlertDialog
import com.namastey.utils.CustomCommonAlertDialog
import com.namastey.utils.SessionManager
import com.namastey.viewModel.ChatViewModel
import kotlinx.android.synthetic.main.dialog_bottom_report.*
import kotlinx.android.synthetic.main.dialog_common_alert.*
import kotlinx.android.synthetic.main.fragment_chat_settings.*
import java.util.*
import javax.inject.Inject

class ChatSettingsFragment : BaseFragment<FragmentChatSettingsBinding>(), ChatBasicView {
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var fragmentChatSettingsBinding: FragmentChatSettingsBinding
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var layoutView: View
    private var matchesListBean: MatchesListBean = MatchesListBean()
    private lateinit var bottomSheetDialogReport: BottomSheetDialog
    private lateinit var onDataPassActivity: onDataPassToActivity

    private var senderId = 0L

    //    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
//    private var myChatRef: DatabaseReference = database.reference
    private val db = Firebase.firestore

    override fun getViewModel() = chatViewModel

    override fun getLayoutId() = R.layout.fragment_chat_settings

    override fun getBindingVariable() = BR.viewModel

    companion object {
        fun getInstance(matchesListBean: MatchesListBean, senderId: Long) =
            ChatSettingsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("matchesListBean", matchesListBean)
                    putLong("senderId", senderId)
                }
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
//        Log.e("ChatSettings", "matchesListBean: \t ${matchesListBean!!.id}")
        matchesListBean = arguments!!.getParcelable<MatchesListBean>("matchesListBean")!!
        if (matchesListBean.is_match == 1)
            tvMatchDelete.visibility = View.VISIBLE
        else
            tvDeleteChat.visibility = View.VISIBLE
        senderId = arguments!!.getLong("senderId")
        Log.e("ChatSettingFragment", "senderId: \t $senderId")

        setMuteNotification()
        switchMuteNotification.setOnCheckedChangeListener { buttonView, isChecked ->
            when {
                isChecked -> {
                    chatViewModel.muteParticularUserNotification(senderId, 1)
                }
                else -> {
                    chatViewModel.muteParticularUserNotification(senderId, 0)
                }
            }
        }

        tvMatchDelete.setOnClickListener {
            dialogMatchDeleteUser()
        }
        tvDeleteChat.setOnClickListener {
            deleteChatDialog()
        }

        tvReport.setOnClickListener {
            dialogReportUser()
        }

        tvBlock.setOnClickListener {
            if (matchesListBean.is_block == 1)
                dialogUnblockUser()
            else
                dialogBlockUser()
        }
        onDataPassActivity = activity as onDataPassToActivity
        if (matchesListBean.is_block == 1)
            tvBlock.text = getString(R.string.unblock)
        else
            tvBlock.text = getString(R.string.block)
    }

    private fun setMuteNotification() {
        if (matchesListBean.is_notification == 1) {
            switchMuteNotification.isChecked = true

        } else {
            switchMuteNotification.isChecked = false
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as ChatActivity).hideChatMoreButton(true)
    }

    private fun dialogUnblockUser() {
        object : CustomCommonAlertDialog(
            requireActivity(),
            matchesListBean.username,
            resources.getString(R.string.msg_unblock_user),
            matchesListBean.profile_pic,
            requireActivity().resources.getString(R.string.yes),
            resources.getString(R.string.cancel)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        chatViewModel.blockUser(matchesListBean.id, 0)
                    }
                }
            }
        }.show()
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

    private fun deleteChatDialog() {
        object : CustomCommonAlertDialog(
            requireActivity(),
            matchesListBean.username,
            getString(R.string.msg_delete_chat),
            matchesListBean.profile_pic,
            getString(R.string.delete_chat),
            resources.getString(R.string.no_thanks)
        ) {
            override fun onBtnClick(id: Int) {
                when (id) {
                    btnAlertOk.id -> {
                        chatViewModel.deleteChat(matchesListBean.id)
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

        if (matchesListBean.is_block == 1)
            tvBlock.text = getString(R.string.block)
        else
            tvBlock.text = getString(R.string.unblock)

        object : CustomAlertDialog(
            requireActivity(),
            msg, getString(R.string.ok), ""
        ) {
            override fun onBtnClick(id: Int) {
                dismiss()
                if (matchesListBean.is_block == 1) {
                    matchesListBean.is_block == 0
                    onDataPassActivity.chatSettingData(0)
                } else {
                    matchesListBean.is_block == 1
                    onDataPassActivity.chatSettingData(1)
                }
            }
        }.show()
    }

    override fun onSuccessDeleteMatches(msg: String) {
        Log.e("ChatSetting", "onSuccessDeleteMatches: \t msg:  $msg")

//        myChatRef = database.getReference(Constants.FirebaseConstant.CHATS)

        val chatId = if (sessionManager.getUserId() < matchesListBean.id)
            sessionManager.getUserId().toString().plus("_").plus(matchesListBean.id)
        else
            matchesListBean.id.toString().plus("_").plus(sessionManager.getUserId())

//        myChatRef.child(chatId).removeValue()
        db.collection(Constants.FirebaseConstant.MESSAGES).document(chatId)
            .delete()
            .addOnSuccessListener { Log.d("Success", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("Failure", "Error deleting document", e) }

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

    override fun onSuccessAdminMessage(data: ArrayList<ChatMessage>) {
        TODO("Not yet implemented")
    }

    override fun onSuccessMuteNotification(message: String) {
        Log.e("ChatSettingFragment", "onSuccessMuteNotification: \t $message")

        if (matchesListBean.is_notification == 1) {
            matchesListBean.is_notification = 0
        } else {
            matchesListBean.is_notification = 1
        }
    }

    override fun onSuccessSuperMessage(msg: SuperMessageBean) {

    }


    interface onDataPassToActivity {
        fun chatSettingData(isBlock: Int)
    }

    override fun onDestroy() {
        chatViewModel.onDestroy()
        super.onDestroy()
    }
}