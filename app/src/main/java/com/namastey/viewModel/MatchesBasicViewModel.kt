package com.namastey.viewModel

import android.util.Log
import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.MatchesBasicView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MatchesBasicViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var matchesBasicView: MatchesBasicView = baseView as MatchesBasicView
    private lateinit var job: Job


//    fun getUserDetails() {
//        setIsLoading(true)
//        job = GlobalScope.launch(Dispatchers.Main) {
//            try {
//                if (matchesBasicView.isInternetAvailable()) {
//                   Log.e("MatchesProfileBasicVM", "isInternetAvailable: \t true")
//                } else {
//                    setIsLoading(false)
//                    matchesBasicView.showMsg(R.string.no_internet)
//                }
//            } catch (t: Throwable) {
//                setIsLoading(false)
//                matchesBasicView.onHandleException(t)
//            }
//        }
//    }


    fun onDestroy(){
        if (::job.isInitialized){
            job.cancel()
        }
    }
}