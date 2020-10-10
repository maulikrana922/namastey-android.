package com.namastey.viewModel

import android.os.Handler
import com.namastey.activity.SplashActivity
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.SplashNavigatorView

class SplashViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var splashNavigatorView: SplashNavigatorView = baseView as SplashNavigatorView

    fun nextScreen(splashActivity: SplashActivity, isLogin: Boolean) {
        Handler(splashActivity.mainLooper).postDelayed({
            if (isLogin) {
                splashNavigatorView.openMainActivity()
            } else {
                splashNavigatorView.openLoginActivity()
            }
        }, 800)
    }
}