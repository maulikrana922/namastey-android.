package com.namastey.viewModel

import com.google.gson.JsonObject
import com.namastey.R
import com.namastey.model.AppResponse
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.AccountSettingsView
import com.namastey.uiView.BaseView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AccountSettingsViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var accountSettingsView: AccountSettingsView = baseView as AccountSettingsView
    private lateinit var job: Job

    fun editProfile(jsonObject: JsonObject) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (accountSettingsView.isInternetAvailable()) {
                    networkService.requestCreateProfile(jsonObject)
                            .let { appResponse: AppResponse<Any> ->
                                setIsLoading(false)
                                if (appResponse.status == Constants.OK) {
                                    accountSettingsView.onSuccess(appResponse.message)
                                } else {
                                    accountSettingsView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                                }
                            }
                } else {
                    setIsLoading(false)
                    accountSettingsView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                accountSettingsView.onHandleException(exception)
            }
        }
    }


    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}