package com.namastey.viewModel

import com.namastey.model.AppResponse
import com.namastey.model.FollowRequestBean
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.FollowRequestView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FollowRequestViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var followRequestView: FollowRequestView = baseView as FollowRequestView
    private lateinit var job: Job

    fun getFollowRequestList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToFollowRequest().let {
                        appResponse: AppResponse<ArrayList<FollowRequestBean>> ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK) {
                        followRequestView.onSuccessFollowRequest(appResponse.data!!)
                    } else {
                        followRequestView.onFailed(appResponse.message, appResponse.error)
                    }
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                followRequestView.onHandleException(exception)
            }
        }
    }



    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }

}