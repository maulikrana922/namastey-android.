package com.namastey.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.namastey.BR
import com.namastey.R
import com.namastey.dagger.module.ViewModelFactory
import com.namastey.databinding.ActivitySplashBinding
import com.namastey.uiView.SplashNavigatorView
import com.namastey.utils.AppPreference
import com.namastey.viewModel.SplashViewModel
import javax.inject.Inject

class SplashActivity : BaseActivity<ActivitySplashBinding>(), SplashNavigatorView {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var splashViewModel: SplashViewModel
    private lateinit var activitySplashBinding: ActivitySplashBinding

    override fun openLoginActivity() {
        Log.d("Login","user not login")
        startActivity(Intent(this, DemoActivity::class.java))
        finish()
    }

    override fun openMainActivity() {
        Log.d("Login","user login")
        startActivity(Intent(this, DemoActivity::class.java))
        finish()
    }

    override fun getViewModel() = splashViewModel

    override fun getLayoutId() = R.layout.activity_splash

    override fun getBindingVariable() = BR.viewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getActivityComponent().inject(this)

        splashViewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SplashViewModel::class.java)
        activitySplashBinding = bindViewData()
        activitySplashBinding.viewModel = splashViewModel

        splashViewModel.nextScreen(AppPreference.getInt(this,AppPreference.USER_ID))

    }
}
