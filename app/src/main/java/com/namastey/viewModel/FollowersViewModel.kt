package com.namastey.viewModel

import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.FollowersView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FollowersViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var followersView: FollowersView = baseView as FollowersView
    private lateinit var job: Job

    fun getFollowersList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (followersView.isInternetAvailable()) {
                    networkService.requestToGetFollowersList().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            followersView.onSuccess(appResponse.data!!)
                        else
                            followersView.onFailed(
                                appResponse.message,
                                appResponse.error
                            )
                    }
                } else {
                    setIsLoading(false)
                    followersView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                followersView.onHandleException(t)
            }
        }


    }

    fun onDestroy() {
        if (::job.isInitialized) {
            job.cancel()
        }
    }

}