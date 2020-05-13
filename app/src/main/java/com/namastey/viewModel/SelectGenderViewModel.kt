package com.namastey.viewModel

import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.OTPView
import com.namastey.uiView.SelectGenderView

class SelectGenderViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private val selectGenderView = baseView as SelectGenderView

    fun closeFragment() {
        selectGenderView.onClose()
    }

    fun onNextClick() {
        selectGenderView.onNext()
    }
}