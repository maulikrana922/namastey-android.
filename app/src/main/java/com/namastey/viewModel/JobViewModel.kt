package com.namastey.viewModel

import com.google.gson.JsonObject
import com.namastey.R
import com.namastey.model.AppResponse
import com.namastey.model.JobBean
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.JobView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class JobViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var jobView: JobView = baseView as JobView
    private lateinit var job: Job

    fun addJob(jsonObject: JsonObject) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (jobView.isInternetAvailable()) {
                    networkService.requestAddUpdateJob(jsonObject)
                        .let { appResponse: AppResponse<JobBean> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                jobView.onSuccessResponse(appResponse.data!!)
                            } else {
                                jobView.onFailed(appResponse.message, appResponse.error)
                            }
                        }
                } else {
                    setIsLoading(false)
                    jobView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                jobView.onHandleException(exception)
            }
        }
    }

    fun getJobList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (jobView.isInternetAvailable()) {
                    networkService.requestToGetJobList().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            jobView.onSuccessJobList(appResponse.data!!)
                        else
                            jobView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    setIsLoading(false)
                    jobView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)

                jobView.onHandleException(t)
            }
        }
    }

    fun removeJobAPI(id: Long) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (jobView.isInternetAvailable()) {
                    networkService.requestToRemoveJob(id)
                        .let { appResponse: AppResponse<Any> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                jobView.onSuccess(appResponse.message)
                            } else {
                                jobView.onFailed(appResponse.message, appResponse.error)
                            }
                        }
                } else {
                    setIsLoading(false)
                    jobView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                jobView.onHandleException(exception)
            }
        }

    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}