package com.namastey.viewModel

import com.namastey.R
import com.namastey.model.AppResponse
import com.namastey.model.MatchesListBean
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.MatchesProfileView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MatchesProfileViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var matchesProfileView: MatchesProfileView = baseView as MatchesProfileView
    private lateinit var job: Job

    fun getMatchesList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToGetMatchesList().let { appResponse: AppResponse<ArrayList<MatchesListBean>> ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK) {
                        matchesProfileView.onSuccessMatchesList(appResponse.data!!)
                    } else {
                        matchesProfileView.onFailed(appResponse.message, appResponse.error)
                    }
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                matchesProfileView.onHandleException(exception)
            }
        }
    }

    fun getLikeUserCount() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (matchesProfileView.isInternetAvailable()) {
                    networkService.requestToGetLikedUserCount().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            matchesProfileView.onSuccessLikeUserCount(appResponse.data!!)
                        else
                            matchesProfileView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    setIsLoading(false)
                    matchesProfileView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                matchesProfileView.onHandleException(t)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }

}