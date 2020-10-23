package com.namastey.viewModel

import com.google.gson.JsonObject
import com.namastey.R
import com.namastey.model.AppResponse
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.SettingsView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SettingsViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var settingsView: SettingsView = baseView as SettingsView
    private lateinit var job: Job

    fun editProfile(jsonObject: JsonObject){
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (settingsView.isInternetAvailable()) {
                    networkService.requestCreateProfile(jsonObject)
                        .let { appResponse: AppResponse<Any> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                settingsView.onSuccess(appResponse.message)
                            } else {
                                settingsView.onFailed(appResponse.message, appResponse.error)
                            }
                        }
                } else {
                    setIsLoading(false)
                    settingsView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                settingsView.onHandleException(exception)
            }
        }
    }


    fun getSearchUser(searchStr: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToSearchUser(searchStr).let { appResponse ->
                    setIsLoading(false)
//                    if (appResponse.status == Constants.OK)
//                        filterView.onSuccessSearchList(appResponse.data!!)
//                    else
//                        filterView.onFailed(appResponse.message, appResponse.error)
                }

            } catch (t: Throwable) {
                setIsLoading(false)
                settingsView.onHandleException(t)
            }
        }

    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}