package com.namastey.viewModel

import android.util.Log
import com.google.gson.JsonObject
import com.namastey.model.*
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.ProfileInterestView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

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
                                profileInterestView.onFailed(appResponse.message, appResponse.error, appResponse.status)
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
                            profileInterestView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                        }
                    }
            } catch (exception: Throwable) {
                setIsLoading(false)
                profileInterestView.onHandleException(exception)
            }
        }
    }

    fun getInstagram(baseurl: String) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                    networkService.requestToGetInstagram(baseurl).let { any : Any ->
                        Log.d("instagram Response : ", any.toString())
                        setIsLoading(false)
                    }
            } catch (exception: Throwable) {
                setIsLoading(false)
                profileInterestView.onHandleException(exception)
            }
        }
    }

    fun getInstagramToken(
        apidata: String,
        client_id: String,
        client_secret: String,
        token: String,
        authorization_code: String,
        redirectUrl: String
    ) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToGetInstagramToken(apidata,client_id,client_secret,
                token,authorization_code,redirectUrl).let { instagramData: InstagramData ->
                    val accessTokenUrl = "https://graph.instagram.com/access_token?client_secret=".plus(client_secret).plus("&access_token=")
                        .plus(instagramData.access_token).plus("&grant_type=ig_exchange_token")
                    Log.d("Instagram Data :", instagramData.access_token)
                    Log.d("Instagram user_id :", instagramData.user_id)
                    fetchInstagramToken(accessTokenUrl,instagramData.user_id)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                profileInterestView.onHandleException(exception)
            }
        }
    }

    private fun fetchInstagramToken(
        accessTokenUrl: String,
        userid: String
    ) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToGetInstagramTokenLink(accessTokenUrl).let { instagramData: InstagramData ->
                    val usernameUrl = "https://graph.instagram.com/".plus(userid)
                        .plus("?access_token=").plus(instagramData.access_token)
                        .plus("&fields=username")

                    instagramFetchUsername(usernameUrl)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                profileInterestView.onHandleException(exception)
            }
        }
    }

    private fun instagramFetchUsername(usernameUrl: String) {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToGetInstagramTokenLink(usernameUrl).let { instagramData: InstagramData ->
                    Log.d("Instagram : ", instagramData.username)
                    setIsLoading(false)
                    profileInterestView.onSuccessInstagram(instagramData)
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