package com.namastey.viewModel

import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.DashboardView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DashboardViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var dashboardView: DashboardView = baseView as DashboardView
    private lateinit var job: Job

    fun getCategoryList() {
//        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToGetCategoryList().let { appResponse ->
                        //                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccessCategory(appResponse.data!!)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
//                    setIsLoading(false)
                    dashboardView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                dashboardView.onHandleException(t)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized) {
            job.cancel()
        }
    }

    fun getFeedList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToGetFeed().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccessFeed(appResponse.data!!)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    setIsLoading(false)
                    dashboardView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                dashboardView.onHandleException(t)
            }
        }
    }

    fun getCommentList(postId: Long) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToGetCommentList(postId).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccessGetComment(appResponse.data!!)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    setIsLoading(false)
                    dashboardView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                dashboardView.onHandleException(t)
            }
        }
    }

    fun addComment(postId: Long, edtComment: String) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToAddComment(postId, edtComment).let { appResponse ->
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccessAddComment(appResponse.data!!)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    dashboardView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                dashboardView.onHandleException(t)
            }
        }
    }

    fun deleteComment(commentId: Long) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToDeleteComment(commentId).let { appResponse ->
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccess(appResponse.message)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    dashboardView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                dashboardView.onHandleException(t)
            }
        }
    }

    fun likeUserProfile(likedUserId: Long, isLike: Int) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToLikeUserProfile(likedUserId, isLike)
                        .let { appResponse ->
                            if (appResponse.status == Constants.OK)
                                appResponse.data?.let { dashboardView.onSuccessProfileLike(it) }
                            else
                                dashboardView.onFailed(appResponse.message, appResponse.error)
                        }
                } else {
                    dashboardView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                dashboardView.onHandleException(t)
            }
        }
    }

    fun followUser(userId: Long, isFollow: Int) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToFollowUser(userId, isFollow).let { appResponse ->
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccessFollow(appResponse.message)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    dashboardView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                dashboardView.onHandleException(t)
            }
        }
    }

    fun reportUser(reportUserId: Long, reason: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToReportUser(reportUserId, reason).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccessReport(appResponse.message)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    setIsLoading(false)
                    dashboardView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                dashboardView.onHandleException(t)
            }
        }
    }

    fun blockUser(userId: Long) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToBlockUser(userId).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccessBlockUser(appResponse.message)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    setIsLoading(false)
                    dashboardView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                dashboardView.onHandleException(t)
            }
        }
    }
}