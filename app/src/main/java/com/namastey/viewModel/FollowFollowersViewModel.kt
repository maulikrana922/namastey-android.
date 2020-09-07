package com.namastey.viewModel

import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.FolloFollowersView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FollowFollowersViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var folloFollowersView: FolloFollowersView = baseView as FolloFollowersView
    private lateinit var job: Job

    fun getSearchUser(searchStr: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToSearchUser(searchStr).let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        folloFollowersView.onSuccessSearchList(appResponse.data!!)
                    else
                        folloFollowersView.onFailed(appResponse.message, appResponse.error)
                }

            } catch (t: Throwable) {
                setIsLoading(false)
                folloFollowersView.onHandleException(t)
            }
        }

    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}