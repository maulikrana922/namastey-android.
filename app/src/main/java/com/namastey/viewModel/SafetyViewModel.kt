package com.namastey.viewModel

import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.SafetyView
import kotlinx.coroutines.Job

class SafetyViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var safetyView: SafetyView = baseView as SafetyView
    private lateinit var job: Job

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}