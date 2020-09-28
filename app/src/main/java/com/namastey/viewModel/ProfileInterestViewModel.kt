package com.namastey.viewModel

import android.util.Log
import com.google.gson.JsonObject
import com.namastey.model.AppResponse
import com.namastey.model.AppResponseSpotify
import com.namastey.model.SocialAccountBean
import com.namastey.model.SpotifyBean
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.ProfileInterestView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileInterestViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var profileInterestView: ProfileInterestView = baseView as ProfileInterestView
    private lateinit var job: Job

    /**
     * add multiple social account link from add link fragment
     */
    fun addSocialLink(jsonObject: JsonObject) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                    networkService.requestAddSocialLinks(jsonObject)
                        .let { appResponse: AppResponse<ArrayList<SocialAccountBean>> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                profileInterestView.onSuccessResponse(appResponse.data!!)
                            } else {
                                profileInterestView.onFailed(appResponse.message, appResponse.error)
                            }
                        }
            } catch (exception: Throwable) {
                setIsLoading(false)
                profileInterestView.onHandleException(exception)
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
                            profileInterestView.onSuccessResponse(appResponse.data!!)
                        } else {
                            profileInterestView.onFailed(appResponse.message, appResponse.error)
                        }
                    }
            } catch (exception: Throwable) {
                setIsLoading(false)
                profileInterestView.onHandleException(exception)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }

    fun getSpotifyLink(token: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {

                networkService.requestToGetSpotifyLink("Bearer ".plus(token)).let { appResponseSpotify: AppResponseSpotify<SpotifyBean> ->
                    Log.d("spotify Response : ", appResponseSpotify.toString())
                    setIsLoading(false)
                    profileInterestView.onSuccessSpotify(appResponseSpotify.external_urls!!.spotify)
                }

            } catch (exception: Throwable) {
                setIsLoading(false)
                profileInterestView.onHandleException(exception)
            }
        }
    }
}