package com.namastey.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivityMatchesScreenBinding
import com.namastey.uiView.MatchesScreenBasicView
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.MatchesScreenViewModel
import kotlinx.android.synthetic.main.activity_matches_screen.*
import javax.inject.Inject

class MatchesScreenActivity : BaseActivity<ActivityMatchesScreenBinding>(), MatchesScreenBasicView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var matchesScreenViewModel: MatchesScreenViewModel
    private lateinit var activityMatchesScreenBinding: ActivityMatchesScreenBinding
    private var userName1 = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        matchesScreenViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(MatchesScreenViewModel::class.java)
        activityMatchesScreenBinding = bindViewData()
        activityMatchesScreenBinding.viewModel = matchesScreenViewModel

        initUi()
        //setupAnimation()
    }

    override fun getViewModel() = matchesScreenViewModel

    override fun getLayoutId() = R.layout.activity_matches_screen

    override fun getBindingVariable() = BR.viewModel

    private fun initUi() {
        getIntentData()
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            userName1 = intent.extras!!.getString("username", "")
            val userProfile1 = intent.extras!!.getString("profile_url", "")

            val userName2 = sessionManager.getStringValue(Constants.KEY_MAIN_USER_NAME)
            val userProfile2 = sessionManager.getStringValue(Constants.KEY_PROFILE_URL)

            if (userProfile1 != null && userProfile1.isNotEmpty()) {
                Glide
                    .with(this)
                    .load(userProfile1)
                    .into(ivProfile1)
            } else {
                Glide
                    .with(this)
                    .load(getDrawable(R.drawable.default_placeholder))
                    .into(ivProfile1)
            }

            if (userProfile2 != null && userProfile2.isNotEmpty()) {
                Glide
                    .with(this)
                    .load(userProfile2)
                    .into(ivProfile2)
            } else {
                Glide
                    .with(this)
                    .load(getDrawable(R.drawable.default_placeholder))
                    .into(ivProfile2)
            }


/*
            tvItsMatch.text =
                getString(R.string.str_its_match).plus(userName2).plus(" ").plus(
                    getString(R.string.and)).plus(" ").plus(userName1).plus(" ")
                        .plus(getString(R.string.str_liked_each_other))
*/
            tvItsMatch.text =
                getString(R.string.you).plus(" ").plus(
                    getString(R.string.and)
                ).plus(" ").plus(userName1).plus(" \n have")
                    .plus(getString(R.string.str_liked_each_other))
        }

    }

    fun onClickShare(view: View) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        sendIntent.putExtra(
            Intent.EXTRA_TEXT, String.format(getString(R.string.msg_matches), userName1)
        )
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }

    fun onClickOpenChat(view: View) {
        val intent = Intent(this@MatchesScreenActivity, MatchesActivity::class.java)
        intent.putExtra("userName", userName1)
//        intent.putExtra("onClickMatches", true)
        openActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickBack(view: View) {
        onBackPressed()
    }
}