package com.namastey.viewModel

import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.ProfileView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var profileView: ProfileView = baseView as ProfileView
    private lateinit var job: Job

    fun getUserDetails(accessToken: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileView.isInternetAvailable()){
                    networkService.requestToGetUserDetail().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            profileView.onSuccessResponse(appResponse.data!!)
                        else
                            profileView.onFailed(appResponse.message,appResponse.error)
                    }
                }else{
                    setIsLoading(false)
                    profileView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                profileView.onHandleException(t)
            }
        }
    }

    fun onDestroy(){
        if (::job.isInitialized){
            job.cancel()
        }
    }
}