package com.namastey.viewModel

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
                networkService.requestToSeeYourFollowers(isFollowers)
                    .let { appResponse: AppResponse<SafetyBean> ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK) {
                            safetySubView.onSuccessYourFollowerResponse(appResponse.data!!)
                        } else {
                            safetySubView.onFailed(appResponse.message, appResponse.error)
                        }
                    }
            } catch (exception: Throwable) {
                setIsLoading(false)
                safetySubView.onHandleException(exception)
            }
        }
    }

     fun whoCanCommentYourVideo(who_can_comment: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToWhoCanCommentYourVideo(who_can_comment)
                    .let { appResponse: AppResponse<SafetyBean> ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK) {
                            safetySubView.onSuccessWhoCanCommentYourVideoResponse(appResponse.data!!)
                        } else {
                            safetySubView.onFailed(appResponse.message, appResponse.error)
                        }
                    }
            } catch (exception: Throwable) {
                setIsLoading(false)
                safetySubView.onHandleException(exception)
            }
        }
    }

     fun whoCanSendYouDirectMessage(whoCanSendDirectMessage: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToWhoCanSendYouDirectMessageVideo(whoCanSendDirectMessage)
                    .let { appResponse: AppResponse<SafetyBean> ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK) {
                            safetySubView.onSuccessWhoCanSendYouDirectMessageResponse(appResponse.data!!)
                        } else {
                            safetySubView.onFailed(appResponse.message, appResponse.error)
                        }
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