package com.namastey.viewModel

import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.OTPView
import com.namastey.uiView.SelectGenderView
import com.namastey.uiView.VideoLanguageView

class VideoLanguageViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private val videoLanguageView = baseView as VideoLanguageView

    fun closeFragment() {
        videoLanguageView.onClose()
    }

    fun onNextClick() {
        videoLanguageView.onNext()
    }
}