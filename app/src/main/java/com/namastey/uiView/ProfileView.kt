package com.namastey.uiView

import com.namastey.model.ProfileBean
import com.namastey.roomDB.entity.User

interface ProfileView: BaseView {

    fun onSuccessResponse(profileBean: ProfileBean)
//    fun onSuccessProfileResponse(user: User)
}