package com.namastey.viewModel

import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.FilterView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FilterViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var filterView: FilterView = baseView as FilterView
    private lateinit var job: Job

    fun getSearchUser(searchStr: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToSearchUser(searchStr).let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        filterView.onSuccessSearchList(appResponse.data!!)
                    else
                        filterView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                }

            } catch (t: Throwable) {
                setIsLoading(false)
                filterView.onHandleException(t)
            }
        }

    }

    fun getTredingVideoList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (filterView.isInternetAvailable()) {
                    networkService.requestToGetTredingVideo().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            filterView.onSuccessTreding(appResponse.data!!)
                        else
                            filterView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                    }
                } else {
                    setIsLoading(false)
                    filterView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                filterView.onHandleException(t)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}