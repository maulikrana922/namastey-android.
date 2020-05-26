package com.namastey.viewModel

import android.os.Handler
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

    fun nextScreen(isLogin: Boolean) {
        Handler().postDelayed({
            if (isLogin) {
                splashNavigatorView.openLoginActivity()
            } else {
                splashNavigatorView.openLoginActivity()
            }
        }, 800)
    }
}