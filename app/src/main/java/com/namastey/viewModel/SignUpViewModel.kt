package com.namastey.viewModel

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

    fun socialLogin(email: String, username: String, provider: String, providerId: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestSocialLogin(
                    email,
                    username,
                    provider,
                    providerId
                )
                    .let { response ->
                        setIsLoading(false)
                        if (response.status == Constants.OK) {
                            signUpView.onSuccessResponse(response.data!!)
                        } else {
                            signUpView.onFailed(response.message, response.error)
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