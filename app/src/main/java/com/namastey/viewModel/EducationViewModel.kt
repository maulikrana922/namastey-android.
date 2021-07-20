package com.namastey.viewModel

import com.google.gson.JsonObject
import com.namastey.R
import com.namastey.model.AppResponse
import com.namastey.model.EducationBean
import com.namastey.model.JobBean
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.EducationView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EducationViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var educationView: EducationView = baseView as EducationView
    private lateinit var job: Job

    fun addEducation(course: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (educationView.isInternetAvailable()) {
                    networkService.requestAddEducation(course)
                        .let { appResponse: AppResponse<EducationBean> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                educationView.onSuccessResponse(appResponse.data!!)
                            } else {
                                educationView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                            }
                        }
                } else {
                    setIsLoading(false)
                    educationView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                educationView.onHandleException(exception)
            }
        }
    }

    fun updateEducation(id:Long,collegeName: String, year: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (educationView.isInternetAvailable()) {
                    networkService.requestUpdateEducation(id.toString(), collegeName, year)
                        .let { appResponse: AppResponse<EducationBean> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                educationView.onSuccessResponse(appResponse.data!!)
                            } else {
                                educationView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                            }
                        }
                } else {
                    setIsLoading(false)
                    educationView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                educationView.onHandleException(exception)
            }
        }
    }

    fun getEducationList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (educationView.isInternetAvailable()) {
                    networkService.requestToGetEducationList().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            educationView.onSuccessEducationList(appResponse.data!!)
                        else
                            educationView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                    }
                } else {
                    setIsLoading(false)
                    educationView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                educationView.onHandleException(t)
            }
        }
    }

    fun removeEducationAPI(id: Long) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (educationView.isInternetAvailable()) {
                    networkService.requestToRemoveEducation(id)
                        .let { appResponse: AppResponse<Any> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                educationView.onSuccess(appResponse.message)
                            } else {
                                educationView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                            }
                        }
                } else {
                    setIsLoading(false)
                    educationView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                educationView.onHandleException(exception)
            }
        }
    }

    fun addJob(jsonObject: JsonObject) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (educationView.isInternetAvailable()) {
                    networkService.requestAddUpdateJob(jsonObject)
                        .let { appResponse: AppResponse<JobBean> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                educationView.onSuccessResponseJob(appResponse.data!!)
                            } else {
                                educationView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                            }
                        }
                } else {
                    setIsLoading(false)
                    educationView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                educationView.onHandleException(exception)
            }
        }
    }


    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}