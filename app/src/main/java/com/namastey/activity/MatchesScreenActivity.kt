package com.namastey.activity

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.GlideApp
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        matchesScreenViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(MatchesScreenViewModel::class.java)
        activityMatchesScreenBinding = bindViewData()
        activityMatchesScreenBinding.viewModel = matchesScreenViewModel

        initUi()
    }

    override fun getViewModel() = matchesScreenViewModel

    override fun getLayoutId() = R.layout.activity_matches_screen

    override fun getBindingVariable() = BR.viewModel

    private fun initUi() {
        mainBackToNamastey.setOnClickListener {
            onBackPressed()
        }

        getIntentData()
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            val userName1 = intent.extras!!.getString("username", "")
            val userProfile1 = intent.extras!!.getString("profile_url", "")

            val userName2 = sessionManager.getStringValue(Constants.KEY_CASUAL_NAME)
            val userProfile2 = sessionManager.getStringValue(Constants.KEY_PROFILE_URL)

            if (userProfile1 != null && userProfile1.isNotEmpty()) {
                GlideApp
                    .with(this)
                    .load(userProfile1)
                    .into(ivProfile1)
            } else {
                GlideApp
                    .with(this)
                    .load(getDrawable(R.drawable.default_placeholder))
                    .into(ivProfile1)
            }

            if (userProfile2 != null && userProfile2.isNotEmpty()) {
                GlideApp
                    .with(this)
                    .load(userProfile2)
                    .into(ivProfile2)
            } else {
                GlideApp
                    .with(this)
                    .load(getDrawable(R.drawable.default_placeholder))
                    .into(ivProfile2)
            }


            tvItsMatch.text =
                "Its a match! \n" + userName2 + " and " + userName1 + "liked each other."

        }

    }
}