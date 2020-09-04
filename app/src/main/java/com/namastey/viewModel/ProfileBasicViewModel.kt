package com.namastey.viewModel

import com.google.gson.JsonObject
import com.namastey.R
import com.namastey.model.AppResponse
import com.namastey.model.SocialAccountBean
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

    fun getUserFullProfile(userId: Long) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileBasicView.isInternetAvailable()){
                    networkService.requestToGetUserFullProfile(userId).let { appResponse ->
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

    fun editProfile(jsonObject: JsonObject){
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileBasicView.isInternetAvailable()) {
                    networkService.requestCreateProfile(jsonObject)
                        .let { appResponse: AppResponse<Any> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                profileBasicView.onSuccess(appResponse.message)
                            } else {
                                profileBasicView.onFailed(appResponse.message, appResponse.error)
                            }
                        }
                } else {
                    setIsLoading(false)
                    profileBasicView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                profileBasicView.onHandleException(exception)
            }
        }
    }

    fun getSocialLink() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToGetSocialLinksList().let {
                        appResponse: AppResponse<ArrayList<SocialAccountBean>> ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK) {
                        profileBasicView.onSuccessSocialAccount(appResponse.data!!)
                    } else {
                        profileBasicView.onFailed(appResponse.message, appResponse.error)
                    }
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                profileBasicView.onHandleException(exception)
            }
        }
    }

    fun onDestroy(){
        if (::job.isInitialized){
            job.cancel()
        }
    }
}