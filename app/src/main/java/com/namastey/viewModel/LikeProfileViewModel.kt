package com.namastey.viewModel

import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.ProfileLikeView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LikeProfileViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var profileLikeView: ProfileLikeView = baseView as ProfileLikeView
    private lateinit var job: Job

    fun getLikedUserPost() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileLikeView.isInternetAvailable()) {
                    networkService.requestToGetLikedUserPost().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            profileLikeView.onSuccess(appResponse.data!!)
                        else
                            profileLikeView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    setIsLoading(false)
                    profileLikeView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                profileLikeView.onHandleException(t)
            }
        }
    }

    fun getLikeUserPost() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileLikeView.isInternetAvailable()) {
                    networkService.requestToGetLikeUserPost().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            profileLikeView.onSuccess(appResponse.data!!)
                        else
                            profileLikeView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    setIsLoading(false)
                    profileLikeView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                profileLikeView.onHandleException(t)
            }
        }
    }


    fun onDestroy() {
        if (::job.isInitialized) {
            job.cancel()
        }
    }
}