package com.namastey.viewModel

import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.FollowingView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FollowingViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var followingView: FollowingView = baseView as FollowingView
    private lateinit var job: Job

    fun getFollowingList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (followingView.isInternetAvailable()) {
                    networkService.requestToGetFollowingList().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            followingView.onSuccess(appResponse.data!!)
                        else
                            followingView.onFailed(
                                appResponse.message,
                                appResponse.error
                            )
                    }
                } else {
                    setIsLoading(false)
                    followingView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                followingView.onHandleException(t)
            }
        }


    }

    fun getFollowersList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (followingView.isInternetAvailable()) {
                    networkService.requestToGetFollowersList().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            followingView.onSuccess(appResponse.data!!)
                        else
                            followingView.onFailed(
                                appResponse.message,
                                appResponse.error
                            )
                    }
                } else {
                    setIsLoading(false)
                    followingView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                followingView.onHandleException(t)
            }
        }
    }

    fun followUser(userId: Long, isFollow: Int) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (followingView.isInternetAvailable()) {
                    networkService.requestToFollowUser(userId, isFollow).let { appResponse ->
                        if (appResponse.status == Constants.OK)
                            followingView.onSuccess(appResponse.message)
                        else
                            followingView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    followingView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                followingView.onHandleException(t)
            }
        }
    }

    fun removeFollowUser(followersUserId: Long, isFollow: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (followingView.isInternetAvailable()) {
                    networkService.requestToRemoveFollowUser(followersUserId, isFollow).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            followingView.onSuccess(appResponse.message)
                        else
                            followingView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    setIsLoading(false)
                    followingView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                followingView.onHandleException(t)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized) {
            job.cancel()
        }
    }
}