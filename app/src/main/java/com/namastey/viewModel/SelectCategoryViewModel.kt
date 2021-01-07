package com.namastey.viewModel

import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.ProfileSelectCategoryView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SelectCategoryViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var profileSelectCategoryView: ProfileSelectCategoryView =
        baseView as ProfileSelectCategoryView
    private lateinit var job: Job

    fun getCategoryList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileSelectCategoryView.isInternetAvailable()) {
                    networkService.requestToGetCategoryList().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            profileSelectCategoryView.onSuccessCategory(appResponse.data!!)
                        else
                            profileSelectCategoryView.onFailed(
                                appResponse.message,
                                appResponse.error,
                                appResponse.status
                            )
                    }
                } else {
                    setIsLoading(false)
                    profileSelectCategoryView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                profileSelectCategoryView.onHandleException(t)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized) {
            job.cancel()
        }
    }
}