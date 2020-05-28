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
}