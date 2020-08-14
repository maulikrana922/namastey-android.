package com.namastey.viewModel

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
                        albumView.onFailed(appResponse.message, appResponse.error)
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
}