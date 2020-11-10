package com.namastey.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.namastey.BR
import com.namastey.R
import com.namastey.adapter.ChatAdapter
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityChatBinding
import com.namastey.fragment.ChatSettingsFragment
import com.namastey.model.MatchesListBean
import com.namastey.uiView.ChatBasicView
import com.namastey.utils.Constants
import com.namastey.utils.GlideLib
import com.namastey.viewModel.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import javax.inject.Inject

class ChatActivity : BaseActivity<ActivityChatBinding>(), ChatBasicView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var activityChatBinding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private var matchesListBean =  MatchesListBean()

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

        getIntentData()
        initUI()
    }

    private fun initUI() {
        chatAdapter = ChatAdapter(this)
        rvChat.adapter = chatAdapter

        if (matchesListBean!!.profile_pic != null && matchesListBean.profile_pic != "") {
            GlideLib.loadImage(this, ivProfileUser, matchesListBean!!.profile_pic)
        } else {
            Glide
                .with(this)
                .load(ContextCompat.getDrawable(this, R.drawable.default_placeholder))
                .into(ivProfileUser)
        }

        if (matchesListBean!!.username != "" && matchesListBean!!.username != null) {
            tvUserName.text = matchesListBean!!.username
        }

        hideChatMoreButton(false)
    }

    private fun getIntentData() {
        if (intent.hasExtra("matchesListBean")) {
            matchesListBean = intent.getParcelableExtra<MatchesListBean>("matchesListBean") as MatchesListBean

            Log.e("ChatActivity", "matchesListBean id\t  ${matchesListBean.id}")
            Log.e("ChatActivity", "matchesListBean username\t  ${matchesListBean.username}")
        }
    }

    fun hideChatMoreButton(isHide: Boolean) {
        if (isHide == true) {
            ivChatMore.visibility = View.GONE
        } else {
            ivChatMore.visibility = View.VISIBLE
        }
    }

    fun onClickChatSettings(view: View) {
        addFragment(
            ChatSettingsFragment.getInstance(matchesListBean!!),
            Constants.CHAT_SETTINGS_FRAGMENT
        )
    }

    override fun onResume() {
        super.onResume()
        hideChatMoreButton(false)
    }

    fun onClickChatBack(view: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            hideChatMoreButton(false)
        }

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            finishActivity()
        }
    }

    override fun onSuccessReport(msg: String) {
        TODO("Not yet implemented")
    }

    override fun onSuccessBlockUser(msg: String) {
        TODO("Not yet implemented")
    }

    override fun onSuccessDeleteMatches(msg: String) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        chatViewModel.onDestroy()
        super.onDestroy()
    }
}