package com.namastey.viewModel

import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.OTPView

class OTPViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private val otpView = baseView as OTPView

    fun closeSignFragment() {
        otpView.onCloseOtp()
    }

    fun onConfirmClick() {
        otpView.onConfirm()
    }
}