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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfileViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var profileView: ProfileView = baseView as ProfileView
    private lateinit var job: Job

    fun getUserDetails() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileView.isInternetAvailable()) {
                    networkService.requestToGetUserDetail().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            profileView.onSuccessResponse(appResponse.data!!)
                        else
                            profileView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    setIsLoading(false)
                    profileView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                profileView.onHandleException(t)
            }
        }
    }

    fun updateProfilePic(profile_file: File) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileView.isInternetAvailable()) {
                    var mbProfile: MultipartBody.Part? = null
                    if (profile_file != null && profile_file.exists()) {
                        mbProfile = MultipartBody.Part.createFormData(
                            Constants.FILE,
                            profile_file.name,
                            RequestBody.create(MediaType.parse(Constants.IMAGE_TYPE), profile_file)
                        )
                    }

                    val rbDeviceType =
                        RequestBody.create(MediaType.parse(Constants.TEXT_PLAIN), Constants.ANDROID)

                    if (mbProfile != null) {
                        networkService.requestUpdateProfilePicAsync(mbProfile, rbDeviceType)
                            .let { appResponse ->
                                setIsLoading(false)
                                if (appResponse.status == Constants.OK)
                                    profileView.onSuccess(appResponse.message)
                                else
                                    profileView.onFailed(appResponse.message, appResponse.error)
                            }
                    }
                } else {
                    setIsLoading(false)
                    profileView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                profileView.onHandleException(t)
            }
        }
    }

    fun getUserFullProfile(userId: Long) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileView.isInternetAvailable()) {
                    networkService.requestToGetUserFullProfile(userId).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            profileView.onSuccessResponse(appResponse.data!!)
                        else
                            profileView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    setIsLoading(false)
                    profileView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                profileView.onHandleException(t)
            }
        }
    }

    fun followUser(userId: Long, isFollow: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileView.isInternetAvailable()) {
                    networkService.requestToFollowUser(userId,isFollow).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            profileView.onSuccess(appResponse.message)
                        else
                            profileView.onFailed(appResponse.message, appResponse.error)
                    }
                } else {
                    setIsLoading(false)
                    profileView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                profileView.onHandleException(t)
            }
        }
    }


    fun onDestroy() {
        if (::job.isInitialized) {
            job.cancel()
        }
    }
}