package com.namastey.viewModel

import com.google.gson.JsonObject
import com.namastey.R
import com.namastey.model.AppResponse
import com.namastey.model.PurchaseBean
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.InAppPurchaseView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class InAppPurchaseViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var inAppPurchaseView: InAppPurchaseView = baseView as InAppPurchaseView
    private lateinit var job: Job

    fun purchaseReceiptVerify(jsonObject: JsonObject) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (inAppPurchaseView.isInternetAvailable()) {
                    networkService.requestPurchaseReceiptVerify(jsonObject)
                        .let { appResponse: AppResponse<PurchaseBean> ->
                            setIsLoading(false)
                            if (appResponse.status == Constants.OK) {
                                inAppPurchaseView.onSuccess(appResponse.data!!)
                            } else {
                                inAppPurchaseView.onFailed(
                                    appResponse.message,
                                    appResponse.error,
                                    appResponse.status
                                )
                            }
                        }
                } else {
                    setIsLoading(false)
                    inAppPurchaseView.showMsg(R.string.no_internet)
                }
            } catch (exception: Throwable) {
                setIsLoading(false)
                inAppPurchaseView.onHandleException(exception)
            }
        }
    }

    fun getPurchaseStatus() {
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                if (inAppPurchaseView.isInternetAvailable()) {
                    networkService.requestToGetPurchaseStatus().let { appResponse ->
                        //                        setIsLoading(false)
                        if (appResponse.status == Constants.OK)
                            inAppPurchaseView.onSuccessPurchaseStatus(appResponse.data!!)
                        else
                            inAppPurchaseView.onFailed(appResponse.message, appResponse.error, appResponse.status)
                    }
                } else {
//                    setIsLoading(false)
                    inAppPurchaseView.showMsg(R.string.no_internet)
                }
            } catch (t: Throwable) {
                inAppPurchaseView.onHandleException(t)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized) {
            job.cancel()
        }
    }

}
