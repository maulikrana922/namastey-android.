package com.namastey.viewModel

import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.FindFriendView
import com.namastey.uiView.FollowingView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ShareAppViewModel  constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var followingView: FollowingView = baseView as FollowingView
    private var findFriendView: FindFriendView = baseView as FindFriendView

    private lateinit var job: Job


    fun getFollowingList(userId: Long) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (followingView.isInternetAvailable()) {
                    networkService.requestToGetFollowingList(userId).let { appResponse ->
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

    fun getSearchUser(searchStr: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToSearchUser(searchStr).let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        findFriendView.onSuccessSearchList(appResponse.data!!)
                    else
                        findFriendView.onFailed(appResponse.message, appResponse.error)
                }

            } catch (t: Throwable) {
                setIsLoading(false)
                findFriendView.onHandleException(t)
            }
        }

    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}