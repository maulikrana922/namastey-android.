package com.namastey.viewModel

import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.ProfilePicView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ProfilePicViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var profilePicView: ProfilePicView = baseView as ProfilePicView
    private lateinit var job: Job

    fun updateProfilePic(profile_file: File) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (profilePicView.isInternetAvailable()) {
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
                                    profilePicView.onSuccess(appResponse.message)
                                else
                                    profilePicView.onFailed(
                                        appResponse.message,
                                        appResponse.error,
                                        appResponse.status
                                    )
                            }
                    }
                } else {
                    setIsLoading(false)
                    profilePicView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                profilePicView.onHandleException(t)
            }
        }
    }


    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}