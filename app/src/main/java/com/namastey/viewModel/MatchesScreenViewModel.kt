package com.namastey.viewModel

import android.util.Log
import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.MatchesScreenBasicView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MatchesScreenViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var matchesScreenBasicView: MatchesScreenBasicView = baseView as MatchesScreenBasicView
    private lateinit var job: Job


    fun getMatchScreen() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (matchesScreenBasicView.isInternetAvailable()) {
                   Log.e("MatchesProfileBasicVM", "isInternetAvailable: \t true")
                } else {
                    setIsLoading(false)
                    matchesScreenBasicView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                setIsLoading(false)
                matchesScreenBasicView.onHandleException(t)
            }
        }
    }


    fun onDestroy(){
        if (::job.isInitialized){
            job.cancel()
        }
    }
}