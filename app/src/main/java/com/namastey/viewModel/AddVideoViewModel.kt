package com.namastey.viewModel

import com.google.gson.JsonObject
import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.AddVideoView
import com.namastey.uiView.BaseView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddVideoViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var addVideoView: AddVideoView = baseView as AddVideoView
    private lateinit var job: Job

    fun createUser(jsonObject: JsonObject) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (addVideoView.isInternetAvailable()) {
                    networkService.requestSignupProfile(jsonObject).let { appResponse ->
                        if (appResponse.status == Constants.OK)
                            addVideoView.onSuccessCreateOrUpdate(appResponse.data!!)
                        else
                            addVideoView.onFailed(
                                appResponse.message,
                                appResponse.error,
                                appResponse.status
                            )
                    }
                } else {
                    setIsLoading(false)
                    addVideoView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                addVideoView.onHandleException(t)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}