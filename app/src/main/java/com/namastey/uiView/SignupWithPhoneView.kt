package com.namastey.uiView

import com.namastey.roomDB.entity.Country
import com.namastey.roomDB.entity.User

interface SignupWithPhoneView: BaseView {

    fun onCloseSignup()

    fun onClickPhone()

    fun onClickEmail()

    fun onClickNext()

    fun onClickCountry()

    fun onSuccessResponse(user: User)

    fun onGetCountry(arrCountry: ArrayList<Country>)

}