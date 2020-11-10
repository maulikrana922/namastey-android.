package com.namastey.viewModel

import com.namastey.R
import com.namastey.model.AppResponse
import com.namastey.model.SafetyBean
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.SafetySubView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SafetySubViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var safetySubView: SafetySubView = baseView as SafetySubView
    private lateinit var job: Job

    fun seeYourFollowers(isFollowers: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (safetySubView.isInternetAvailable()) {
                    networkService.requestToSeeYourFollowers(isFollowers)
                        .let { appResponse: AppResponse<SafetyBean> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                safetySubView.onSuccessResponse(appResponse.data!!)
                            } else {
                                safetySubView.onFailed(appResponse.message, appResponse.error)
                            }
                        }
                } else {
                    setIsLoading(false)
                    safetySubView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                safetySubView.onHandleException(exception)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}