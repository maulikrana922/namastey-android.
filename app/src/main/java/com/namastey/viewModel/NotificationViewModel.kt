package com.namastey.viewModel

import android.util.Log
import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.NotificationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotificationViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var notificationView: NotificationView = baseView as NotificationView
    private lateinit var job: Job

    fun getMatchScreen() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (notificationView.isInternetAvailable()) {
                    Log.e("notificationProfileView", "isInternetAvailable: \t true")
                } else {
                    setIsLoading(false)
                    notificationView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                notificationView.onHandleException(t)
            }
        }
    }


    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }

}