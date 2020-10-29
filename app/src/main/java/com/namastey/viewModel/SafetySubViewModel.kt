package com.namastey.viewModel

import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.SafetySubView
import kotlinx.coroutines.Job

class SafetySubViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private var safetySubView: SafetySubView = baseView as SafetySubView
    private lateinit var job: Job

    fun onDestroy() {
        if (::job.isInitialized)
            job.cancel()
    }
}