package com.namastey.viewModel

import com.google.gson.JsonObject
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
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

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}