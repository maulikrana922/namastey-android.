package com.namastey.viewModel

import com.google.gson.JsonObject
import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.ChooseInterestView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ChooseInterestViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private val chooseInterestView = baseView as ChooseInterestView
    private lateinit var job: Job

    fun closeFragment() {
        chooseInterestView.onClose()
    }

    fun onNextClick() {
        chooseInterestView.onNext()
    }

    fun getChooseInterestList(){
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (chooseInterestView.isInternetAvailable()){
                    networkService.requestToGetChooseInterest().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            chooseInterestView.onSuccess(appResponse.data!!)
                        else
                            chooseInterestView.onFailed(appResponse.message,appResponse.error)
                    }
                }else{
                    setIsLoading(false)
                    chooseInterestView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                chooseInterestView.onHandleException(t)
            }
        }
    }
    fun onDestroy(){
        if (::job.isInitialized){
            job.cancel()
        }
    }

    fun updateOrCreateUser(jsonObject: JsonObject) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (chooseInterestView.isInternetAvailable()){
                    networkService.requestToUpdateOrCreateUser(jsonObject).let { appResponse ->
                                                if (appResponse.status == Constants.OK)
                            chooseInterestView.onSuccessCreateOrUpdate(appResponse.data!!)
                        else
                            chooseInterestView.onFailed(appResponse.message,appResponse.error)
                    }
                }else{
                    setIsLoading(false)
                    chooseInterestView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                chooseInterestView.onHandleException(t)
            }
        }
    }
}