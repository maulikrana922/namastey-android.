package com.namastey.uiView

import com.namastey.model.ProfileBean

interface ProfileView: BaseView {

    fun onSuccessResponse(profileBean: ProfileBean)
    fun onSuccessProfileLike(data: Any)

//    fun onSuccessProfileResponse(user: User)
}