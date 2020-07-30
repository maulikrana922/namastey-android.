package com.namastey.viewModel

import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.ProfileBasicView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileBasicViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var profileBasicView: ProfileBasicView = baseView as ProfileBasicView
    private lateinit var job: Job

    fun getUserFullProfile() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileBasicView.isInternetAvailable()){
                    networkService.requestToGetUserFullProfile().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            profileBasicView.onSuccessProfileDetails(appResponse.data!!)
                        else
                            profileBasicView.onFailed(appResponse.message,appResponse.error)
                    }
                }else{
                    setIsLoading(false)
                    profileBasicView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                profileBasicView.onHandleException(t)
            }
        }
    }

    fun onDestroy(){
        if (::job.isInitialized){
            job.cancel()
        }
    }
}