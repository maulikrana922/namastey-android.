package com.namastey.viewModel

import com.google.gson.JsonObject
import com.namastey.model.AppResponse
import com.namastey.model.NotificationOnOffBean
import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.AccountSettingsNotificationView
import com.namastey.uiView.BaseView
import com.namastey.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AccountSettingsNotificationViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var accountSettingsNotificationView: AccountSettingsNotificationView =
        baseView as AccountSettingsNotificationView
    private lateinit var job: Job

    fun onNotificationOnOff(jsonObject: JsonObject) {
        setIsLoading(true)
        job = GlobalScope.launch(Dispatchers.Main) {
            try {
                networkService.requestToNotificationOnOff(jsonObject)
                    .let { appResponse: AppResponse<NotificationOnOffBean> ->
                        setIsLoading(false)
                        if (appResponse.status == Constants.OK) {
                            accountSettingsNotificationView.onSuccessResponse(appResponse.data!!)
                            //accountSettingsNotificationView.onSuccess(appResponse.message)
                        } else {
                            accountSettingsNotificationView.onFailed(
                                appResponse.message,
                                appResponse.error
                            )
                        }
                    }
            } catch (exception: Throwable) {
                setIsLoading(false)
                accountSettingsNotificationView.onHandleException(exception)
            }
        }
    }

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}