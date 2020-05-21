package com.namastey.uiView

import com.namastey.roomDB.entity.User

interface SignUpView: BaseView {

    fun skipLogin()

    fun onSuccessResponse(user: User)

}