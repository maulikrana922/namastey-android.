package com.namastey.viewModel

import com.namastey.networking.NetworkService
import com.namastey.roomDB.DBHelper
import com.namastey.uiView.BaseView
import com.namastey.uiView.CountryView

class CountryViewModel constructor(
    private val networkService: NetworkService,
    private val dbHelper: DBHelper,
    baseView: BaseView
) : BaseViewModel(networkService, dbHelper, baseView) {

    private val countryView = baseView as CountryView

    fun closeFragment() {
        countryView.onClose()
    }



    fun onNextClick() {
//        countryView.onNext()
    }

    fun getVideoLanguage() {

    }


}