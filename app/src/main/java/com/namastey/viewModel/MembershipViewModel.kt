package com.namastey.viewModel

import com.namastey.R
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.MemberShipView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MembershipViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var memberShipView: MemberShipView = baseView as MemberShipView
    private lateinit var job: Job

    fun getMembershipPriceList() {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (memberShipView.isInternetAvailable()) {
                    networkService.requestToMembershipPriceList().let { appResponse ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            memberShipView.onSuccessMembershipList(appResponse.data!!)
                        else
                            memberShipView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                    }
                } else {
                    setIsLoading(false)
                    memberShipView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                memberShipView.onHandleException(t)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized) {
            job.cancel()
        }
    }
}