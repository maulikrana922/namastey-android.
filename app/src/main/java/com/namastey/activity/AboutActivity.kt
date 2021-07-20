package com.namastey.activity

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import com.namastey.R
import com.namastey.databinding.ActivityAboutBinding
import com.namastey.utils.Constants
import com.namastey.utils.SessionManager
import com.namastey.viewModel.BaseViewModel
import kotlinx.android.synthetic.main.activity_about.*
import javax.inject.Inject

class AboutActivity : BaseActivity<ActivityAboutBinding>() {

    @Inject
    lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        initUI()
    }

    private fun initUI() {
        sessionManager = SessionManager(this)

    }

    override fun getViewModel(): BaseViewModel {
        TODO("Not yet implemented")
    }

    override fun getLayoutId(): Int {
        TODO("Not yet implemented")
    }

    override fun getBindingVariable(): Int {
        TODO("Not yet implemented")
    }

    fun onClickContinue(view: View) {
        if (!TextUtils.isEmpty(edtAbout.text.toString().trim())) {
            sessionManager.setStringValue(
                edtAbout.text.toString().trim(),
                Constants.KEY_TAGLINE
            )
        }
        openActivity(this@AboutActivity, SocialLinkActivity())
    }

    override fun onBackPressed() {
        finishActivity()
    }

    fun onClickBack(view: View) {
        onBackPressed()
    }

    fun onClickSkip(view: View) {
        openActivity(this@AboutActivity, SocialLinkActivity())
    }
}