package com.namastey.uiView

import com.namastey.roomDB.entity.User

interface OTPView: BaseView{

    fun onCloseOtp()

    fun onConfirm()

    fun onSuccessResponse(user: User)

}