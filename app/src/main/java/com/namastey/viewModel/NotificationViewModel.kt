package com.namastey.viewModel

import com.namastey.R
import com.namastey.model.ActivityListBean
import com.namastey.model.AppResponse
import com.namastey.model.FollowRequestBean
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.NotificationView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotificationViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var notificationView: NotificationView = baseView as NotificationView
    private lateinit var job: Job

    fun getFollowRequestList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToFollowRequest()
                    .let { appResponse: AppResponse<ArrayList<FollowRequestBean>> ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK) {
                            notificationView.onSuccessFollowRequest(appResponse.data!!)
                        } else {
                            notificationView.onFailed(appResponse.message, appResponse.error)
                        }
                    }
            } catch (exception: Throwable) {
                setIsLoading(false)
                notificationView.onHandleException(exception)
            }
        }
    }

    fun getActivityList(isFilter: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToPostActivityList(isFilter)
                    .let { appResponse: AppResponse<ArrayList<ActivityListBean>> ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK) {
                            notificationView.onSuccessActivityList(appResponse.data!!)
                        } else {
                            notificationView.onFailed(appResponse.message, appResponse.error)
                        }
                    }
            } catch (exception: Throwable) {
                setIsLoading(false)
                notificationView.onHandleException(exception)
            }
        }
    }

    fun followUser(userId: Long, isFollow: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (notificationView.isInternetAvailable()) {
                    networkService.requestToFollowUserProfile(userId, isFollow).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            notificationView.onSuccessFollow(appResponse.data!!)
                        else
                            notificationView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    setIsLoading(false)
                    notificationView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                notificationView.onHandleException(t)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }

}