package com.namastey.viewModel

import com.google.gson.JsonObject
import com.namastey.R
import com.namastey.model.AlbumBean
import com.namastey.model.AppResponse
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.AlbumView
import com.namastey.uiView.BaseView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AlbumViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var albumView = baseView as AlbumView
    private lateinit var job: Job

    fun getAlbumList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToGetAlbumList().let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        albumView.onSuccessAlbumList(appResponse.data!!)
                    else
                        albumView.onFailed(appResponse.message,appResponse.error)
                }

            } catch (t: Throwable) {
                setIsLoading(false)
                albumView.onHandleException(t)
            }
        }

    }
    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }

    fun getAlbumDetail(albumId: Long) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToGetAlbumDetails(albumId).let { appResponse ->
                    setIsLoading(false)
                    if (appResponse.status == Constants.OK)
                        albumView.onSuccessAlbumList(appResponse.data!!)
                    else
                        albumView.onFailed(appResponse.message,appResponse.error)
                }

            } catch (t: Throwable) {
                setIsLoading(false)
                albumView.onHandleException(t)
            }
        }
    }

    fun editAlbum(jsonObject: JsonObject) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (albumView.isInternetAvailable()) {
                    networkService.requestAddUpdateAlbum(jsonObject)
                        .let { appResponse: AppResponse<AlbumBean> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                albumView.onSuccess(appResponse.message)
                            } else {
                                albumView.onFailed(appResponse.message, appResponse.error)
                            }
                        }
                } else {
                    setIsLoading(false)
                    albumView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                albumView.onHandleException(exception)
            }
        }
    }


}