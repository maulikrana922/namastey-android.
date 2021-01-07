package com.namastey.viewModel

import com.namastey.R
import com.namastey.model.AppResponse
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.ManageAccountView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ManageAccountViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var manageAccountView: ManageAccountView = baseView as ManageAccountView
    private lateinit var job: Job

    fun removeAccount() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (manageAccountView.isInternetAvailable()) {
                    networkService.requestToDeleteAccount()
                        .let { appResponse: AppResponse<Any> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                manageAccountView.onSuccess(appResponse.message)
                            } else {
                                manageAccountView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                            }
                        }
                } else {
                    setIsLoading(false)
                    manageAccountView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                manageAccountView.onHandleException(exception)
            }
        }

    }

    fun logOut() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (manageAccountView.isInternetAvailable()) {
                    networkService.requestToLogout()
                        .let { appResponse: AppResponse<Any> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                manageAccountView.onLogoutSuccess(appResponse.message)
                            } else {
                                manageAccountView.onLogoutFailed(appResponse.message, appResponse.error)
                            }
                        }
                } else {
                    setIsLoading(false)
                    manageAccountView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                manageAccountView.onHandleException(exception)
            }
        }

    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}