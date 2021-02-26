package com.namastey.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.DashboardView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job

class DashboardViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var dashboardView: DashboardView = baseView as DashboardView
    private lateinit var job: Job

    private val _downloading: MutableLiveData<Boolean> = MutableLiveData()
    val downloading: LiveData<Boolean> = _downloading

    fun setDownloading(downloading: Boolean) {
        _downloading.value = downloading
    }

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
                            dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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

    fun getFeedList(subCatId: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToGetFeed(subCatId).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK) {
                            dashboardView.onSuccessFeed(appResponse.data!!)
                        } else {
                            dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                        }
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
                            dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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
                            dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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
                            dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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
                                appResponse.data?.let {
                                    dashboardView.onSuccessProfileLike(
                                        appResponse.data!!
                                    )
                                }
                            else
                                dashboardView.onFailedMaxLike(
                                    appResponse.message,
                                    appResponse.error
                                )
                            // dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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
                            dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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
                            dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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

    fun blockUser(userId: Long, isBlock: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToBlockUser(userId, isBlock).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccessBlockUser(appResponse.message)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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

    fun savePost(postId: Long) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToSavePost(postId).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccessSavePost(appResponse.message)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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

    fun postView(postId: Long) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToPostView(postId).let { appResponse ->

                    }
                } else {
                    dashboardView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                dashboardView.onHandleException(t)
            }
        }

    }

    fun getMentionList(search: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToPostMentionList(search).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccessMention(appResponse.data!!)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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

    fun postShare(postId: Int, isShare: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToSharePost(postId, isShare).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccessPostShare(appResponse.message)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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

    fun getMembershipPriceList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToMembershipPriceList().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccessMembershipList(appResponse.data!!)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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

    fun getNewFeedList(page: Int, subCatId: Int, lat: Double, lng: Double) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToGetNewFeed(page, subCatId, lat, lng)
                        .let { appResponse ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                dashboardView.onSuccessFeed(appResponse.data!!)
                            } else {
                                dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                            }
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

    fun getPurchaseStatus() {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToGetPurchaseStatus().let { appResponse ->
                        //                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccessPurchaseStatus(appResponse.data!!)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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

    fun boostUse() {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (dashboardView.isInternetAvailable()) {
                    networkService.requestToBoostUse().let { appResponse ->
                        if (appResponse.status == Constants.OK)
                            dashboardView.onSuccessBoostUse(appResponse.data!!)
                        else
                            dashboardView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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

}