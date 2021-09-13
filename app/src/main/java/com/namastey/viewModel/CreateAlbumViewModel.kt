package com.namastey.viewModel

import com.google.gson.JsonObject
import com.namastey.R
import com.namastey.model.AlbumBean
import com.namastey.model.AppResponse
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.CreateAlbumView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

class CreateAlbumViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var createAlbumView: CreateAlbumView = baseView as CreateAlbumView
    private lateinit var job: Job

    /**
     * Api call for add/edit album with name
     */
    fun addEditAlbum(jsonObject: JsonObject) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (createAlbumView.isInternetAvailable()) {
                    networkService.requestAddUpdateAlbum(jsonObject)
                        .let { appResponse: AppResponse<AlbumBean> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                createAlbumView.onSuccessResponse(appResponse.data!!)
                            } else {
                                createAlbumView.onFailed(
                                    appResponse.message,
                                    appResponse.error,
                                    appResponse.status
                                )
                            }
                        }
                } else {
                    setIsLoading(false)
                    createAlbumView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                createAlbumView.onHandleException(exception)
            }
        }
    }

    fun createProfile(jsonObject: JsonObject) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (createAlbumView.isInternetAvailable()) {
                    networkService.requestCreateProfile(jsonObject)
                        .let { appResponse: AppResponse<Any> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                createAlbumView.onSuccess(appResponse.message)
                            } else {
                                createAlbumView.onFailed(
                                    appResponse.message,
                                    appResponse.error,
                                    appResponse.status
                                )
                            }
                        }
                } else {
                    setIsLoading(false)
                    createAlbumView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                createAlbumView.onHandleException(exception)
            }
        }
    }

    fun getAlbumList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (createAlbumView.isInternetAvailable()) {
                    networkService.requestToGetAlbumWithDetails()
                        .let { appResponse: AppResponse<ArrayList<AlbumBean>> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                createAlbumView.onSuccessAlbumDetails(appResponse.data!!)
                            } else {
                                createAlbumView.onFailed(
                                    appResponse.message,
                                    appResponse.error,
                                    appResponse.status
                                )
                            }
                        }
                } else {
                    setIsLoading(false)
                    createAlbumView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                createAlbumView.onHandleException(exception)
            }
        }
    }

    fun getAlbumDetail(albumId: Long) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToGetAlbumDetails(albumId).let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        createAlbumView.onSuccessAlbumDetails(appResponse.data!!)
                    else
                        createAlbumView.onFailed(
                            appResponse.message,
                            appResponse.error,
                            appResponse.status
                        )
                }

            } catch (t: Throwable) {
                setIsLoading(false)
                createAlbumView.onHandleException(t)
            }
        }
    }

    fun removePostVideo(postId: Long, isSaved: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (createAlbumView.isInternetAvailable()) {
                    val jsonObject = JsonObject()
                    jsonObject.addProperty(Constants.POST_ID, postId)
                    jsonObject.addProperty(Constants.IS_SAVED, isSaved)
                    networkService.requestToDeletePost(jsonObject)
                        .let { appResponse: AppResponse<Any> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                createAlbumView.onSuccessDeletePost()
                            } else {
                                createAlbumView.onFailed(
                                    appResponse.message,
                                    appResponse.error,
                                    appResponse.status
                                )
                            }
                        }
                } else {
                    setIsLoading(false)
                    createAlbumView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                createAlbumView.onHandleException(exception)
            }
        }

    }

    fun deleteAlbum(albumId: Long, isSaved: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToDeleteAlbum(albumId,isSaved).let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        createAlbumView.onSuccessAlbumDelete(appResponse.message)
                    else
                        createAlbumView.onFailed(
                            appResponse.message,
                            appResponse.error,
                            appResponse.status
                        )
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                createAlbumView.onHandleException(t)
            }
        }
    }

    fun hideAlbum(albumId: Long, isHide: Int) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToHideAlbum(albumId, isHide).let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        createAlbumView.onSuccessAlbumHide(appResponse.message)
                    else
                        createAlbumView.onFailed(
                            appResponse.message,
                            appResponse.error,
                            appResponse.status
                        )
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                createAlbumView.onHandleException(t)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }

}