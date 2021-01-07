package com.namastey.viewModel

import com.namastey.model.AppResponse
import com.namastey.model.SafetyBean
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.PersonalizeDataView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PersonalizeDataViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var personalizeDataView: PersonalizeDataView = baseView as PersonalizeDataView
    private lateinit var job: Job


    fun suggestYourAccountOnOff(isSuggest: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToSuggestYourAccountOnOff(isSuggest)
                    .let { appResponse: AppResponse<SafetyBean> ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK) {
                            personalizeDataView.onSuccessResponse(appResponse.data!!)
                        } else {
                            personalizeDataView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                        }
                    }
            } catch (exception: Throwable) {
                setIsLoading(false)
                personalizeDataView.onHandleException(exception)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}