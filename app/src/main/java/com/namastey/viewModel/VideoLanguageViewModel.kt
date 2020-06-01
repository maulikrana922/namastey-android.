package com.namastey.viewModel

import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.VideoLanguageView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class VideoLanguageViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private val videoLanguageView = baseView as VideoLanguageView
    private lateinit var job: Job

    fun closeFragment() {
        videoLanguageView.onClose()
    }

    fun onNextClick() {
        videoLanguageView.onNext()
    }

    fun getVideoLanguage(locale: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (videoLanguageView.isInternetAvailable()){
                    networkService.requestToGetVideoLanguage(locale).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            videoLanguageView.onSuccess(appResponse.data!!)
                        else
                            videoLanguageView.onFailed(appResponse.message,appResponse.error)
                    }
                }else{
                    setIsLoading(false)
                    videoLanguageView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                videoLanguageView.onHandleException(t)
            }
        }
    }

    fun onDestroy(){
        if (::job.isInitialized){
            job.cancel()
        }
    }

}