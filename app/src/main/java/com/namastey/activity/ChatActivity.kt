package com.namastey.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.ChatAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityChatBinding
import com.namastey.fragment.ChatSettingsFragment
import com.namastey.uiView.ChatBasicView
import com.namastey.utils.Constants
import com.namastey.viewModel.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import javax.inject.Inject

class ChatActivity : BaseActivity<ActivityChatBinding>(), ChatBasicView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var activityChatBinding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter

    override fun getViewModel() = chatViewModel

    override fun getLayoutId() = R.layout.activity_chat

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        chatViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ChatViewModel::class.java)
        activityChatBinding = bindViewData()
        activityChatBinding.viewModel = chatViewModel

        initUI()
    }

    private fun initUI() {

        chatAdapter = ChatAdapter(this)
        rvChat.adapter = chatAdapter
    }


    fun onClickChatSettings(view: View) {
        addFragment(
            ChatSettingsFragment(),
            Constants.CHAT_SETTINGS_FRAGMENT
        )
    }


    fun onClickChatBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finishActivity()
        }
    }

    override fun onDestroy() {
        chatViewModel.onDestroy()
        super.onDestroy()
    }
}