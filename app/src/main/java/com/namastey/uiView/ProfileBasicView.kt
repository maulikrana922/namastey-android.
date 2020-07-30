package com.namastey.uiView

import com.namastey.model.ProfileBean

interface ProfileBasicView: BaseView {

    fun onSuccessProfileDetails(profileBean: ProfileBean)

}