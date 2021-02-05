package com.namastey.viewModel

import com.namastey.R
import com.namastey.model.AppResponse
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
                            profileView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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
                                    profileView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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

    fun getUserFullProfile(userId: String, username: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileView.isInternetAvailable()) {
                    networkService.requestToGetUserFullProfile(userId, username).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            profileView.onSuccessResponse(appResponse.data!!)
                        else
                            profileView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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
                    networkService.requestToFollowUserProfile(userId,isFollow).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            profileView.onSuccessFollow(appResponse.data!!)
                        else
                            profileView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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

    fun likeUserProfile(likedUserId: Long, isLike: Int) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileView.isInternetAvailable()) {
                    networkService.requestToLikeUserProfile(likedUserId, isLike)
                        .let { appResponse ->
                            if (appResponse.status == Constants.OK)
                                appResponse.data?.let { profileView.onSuccessProfileLike(appResponse.data!!) }
                            else
                                profileView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                        }
                } else {
                    profileView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                profileView.onHandleException(t)
            }
        }
    }

    fun blockUser(userId: Long, isBlock: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileView.isInternetAvailable()) {
                    networkService.requestToBlockUser(userId, isBlock).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            profileView.onSuccessBlockUser(appResponse.message)
                        else
                            profileView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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

    fun savePost(postId: Long) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileView.isInternetAvailable()) {
                    networkService.requestToSavePost(postId).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            profileView.onSuccessSavePost(appResponse.message)
                        else
                            profileView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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

    fun reportUser(reportUserId: Long, reason: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileView.isInternetAvailable()) {
                    networkService.requestToReportUser(reportUserId, reason).let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            profileView.onSuccessReport(appResponse.message)
                        else
                            profileView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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

    fun getBoostPriceList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileView.isInternetAvailable()) {
                    networkService.requestToGetBoostPriceList().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            profileView.onSuccessBoostPriceList(appResponse.data!!)
                        else
                            profileView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                    }
                } else {
                    setIsLoading(false)
                    profileView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                profileView.onHandleException(t)
            }
        }
    }

    fun logOut() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profileView.isInternetAvailable()) {
                    networkService.requestToLogout()
                        .let { appResponse: AppResponse<Any> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                profileView.onLogoutSuccess(appResponse.message)
                            } else {
                                profileView.onLogoutFailed(appResponse.message, appResponse.error)
                            }
                        }
                } else {
                    setIsLoading(false)
                    profileView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                profileView.onHandleException(exception)
            }
        }

    }


    fun onDestroy() {
        if (::job.isInitialized) {
            job.cancel()
        }
    }
}