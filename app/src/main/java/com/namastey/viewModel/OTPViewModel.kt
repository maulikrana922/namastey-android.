package com.namastey.viewModel

import android.text.TextUtils
import com.namastey.R
import com.namastey.model.AppResponse
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.roomDB.entity.User
import com.namastey.uiView.BaseView
import com.namastey.uiView.OTPView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OTPViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private val otpView = baseView as OTPView
    private lateinit var job: Job

    fun closeSignFragment() {
        otpView.onCloseOtp()
    }

    fun onConfirmClick() {
        otpView.onConfirm()
    }

    fun verifyOTP(phone: String, email: String, otp: String) {
        if (isValidOTP(otp)) {
            setIsLoading(true)
            job = GlobalScope.launch(Dispatchers.Main) {
                try {
                    if (otpView.isInternetAvailable()) {
                        networkService.requestVerifyOTP(phone,email,otp).let { appResponse: AppResponse<User> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                otpView.onSuccessResponse(appResponse.data!!)
                            } else {
                                otpView.onFailed(appResponse.message, appResponse.error)
                            }
                        }
                    } else {
                        setIsLoading(false)
                        otpView.showMsg(R.string.no_internet)
                    }
                } catch (exception: Throwable) {
                    setIsLoading(false)
                    otpView.onHandleException(exception)
                }
            }
        }
    }

    private fun isValidOTP(otp: String): Boolean {
        var msgId = 0
        when {
            TextUtils.isEmpty(otp) -> msgId = R.string.msg_empty_otp
        }
        if (msgId > 0) {
            otpView.showMsg(msgId)
        }

        return msgId == 0
    }

    fun onDestroy() {
        if (::job.isInitialized) {
            job.cancel()
        }
    }
}