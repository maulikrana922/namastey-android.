package com.namastey.viewModel

import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.AccountSettingsView
import com.namastey.uiView.BaseView
import kotlinx.coroutines.Job

class AccountSettingsViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var accountSettingsView: AccountSettingsView = baseView as AccountSettingsView
    private lateinit var job: Job



    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}