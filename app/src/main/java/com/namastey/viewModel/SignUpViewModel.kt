package com.namastey.viewModel

import android.text.TextUtils
import com.google.gson.JsonObject
import com.namastey.R
import com.namastey.model.AppResponse
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.roomDB.entity.User
import com.namastey.uiView.BaseView
import com.namastey.uiView.SignUpView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SignUpViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var signUpView: SignUpView = baseView as SignUpView
    private lateinit var job: Job

    fun onSkipLogin() {
        signUpView.skipLogin()
    }

    fun socialLogin(
        email: String,
        username: String,
        provider: String,
        providerId: String,
        user_uniqueId: String,
        firebaseToken: String
    ) {
        setIsLoading(true)
        val jsonObject = JsonObject()
        jsonObject.addProperty(Constants.DEVICE_TYPE, Constants.ANDROID)
        jsonObject.addProperty(Constants.EMAIL, email)
        jsonObject.addProperty(Constants.USERNAME, username)
        jsonObject.addProperty(Constants.PROVIDER, provider)
        jsonObject.addProperty(Constants.PROVIDER_ID, providerId)
        jsonObject.addProperty(Constants.DEVICE_TOKEN, firebaseToken)
        if (user_uniqueId.isNotEmpty()) {
            jsonObject.addProperty(Constants.IS_GUEST, 1)
            jsonObject.addProperty(Constants.USER_UNIQUEID, user_uniqueId)
        }

        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestSocialLogin(
                    jsonObject
                )
                    .let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK) {
                            signUpView.onSuccessResponse(appResponse.data!!)
                        } else {
                            signUpView.onFailed(
                                appResponse.message,
                                appResponse.error,
                                appResponse.status
                            )
                        }
                    }
            } catch (t: Throwable) {
                setIsLoading(false)
                signUpView.onHandleException(t)
            }
        }
    }

    fun isValidPhone(phone: String): Boolean {
        var msgId = 0
        when {
            TextUtils.isEmpty(phone) -> msgId = R.string.msg_empty_mobile
        }
        if (msgId > 0) {
            signUpView.showMsg(msgId)
        }

        return msgId == 0
    }

    fun sendOTP(jsonObject: JsonObject) {

        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (signUpView.isInternetAvailable()) {
                    networkService.requestSendOTP(jsonObject)
                        .let { appResponse: AppResponse<User> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                signUpView.onSuccessResponse(appResponse.data!!)
                            } else {
                                signUpView.onFailed(
                                    appResponse.message, appResponse.error,
                                    appResponse.status
                                )
                            }
                        }
                } else {
                    setIsLoading(false)
                    signUpView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                signUpView.onHandleException(exception)
            }
        }

    }

    fun onClickContinue(){
        signUpView.onClickContinue()
    }
    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}