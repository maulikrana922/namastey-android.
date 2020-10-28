package com.namastey.viewModel

import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.ContentLanguageView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ContentLanguageViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var contentLanguageView: ContentLanguageView = baseView as ContentLanguageView
    private lateinit var job: Job

    fun getContentLanguage(locale: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (contentLanguageView.isInternetAvailable()) {
                    networkService.requestToGetVideoLanguage(locale).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            contentLanguageView.onSuccess(appResponse.data!!)
                        else
                            contentLanguageView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    setIsLoading(false)
                    contentLanguageView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                contentLanguageView.onHandleException(t)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}