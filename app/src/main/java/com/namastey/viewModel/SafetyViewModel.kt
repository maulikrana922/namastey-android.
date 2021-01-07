package com.namastey.viewModel

import com.namastey.model.AppResponse
import com.namastey.model.SafetyBean
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.SafetyView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SafetyViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var safetyView: SafetyView = baseView as SafetyView
    private lateinit var job: Job

    fun idDownloadVideo(isDownload: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToDownloadVideo(isDownload)
                    .let { appResponse: AppResponse<SafetyBean> ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK) {
                            safetyView.onSuccessIsSuccessResponse(appResponse.data!!)
                        } else {
                            safetyView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                        }
                    }
            } catch (exception: Throwable) {
                setIsLoading(false)
                safetyView.onHandleException(exception)
            }
        }
    }


    fun isShareProfileSafety(isShare: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToShareProfileSafety(isShare)
                    .let { appResponse: AppResponse<SafetyBean> ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK) {
                            safetyView.onSuccessShareProfileSafetyResponse(appResponse.data!!)
                        } else {
                            safetyView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                        }
                    }
            } catch (exception: Throwable) {
                setIsLoading(false)
                safetyView.onHandleException(exception)
            }
        }
    }


    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}