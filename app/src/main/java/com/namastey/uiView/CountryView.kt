package com.namastey.uiView

import com.namastey.roomDB.entity.Country

interface CountryView : BaseView {

    fun onClose()

    fun onSuccess(country: Country)

}